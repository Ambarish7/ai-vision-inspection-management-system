import os
from pathlib import Path
from typing import Any

import cv2
import numpy as np
from fastapi import FastAPI, File, HTTPException, UploadFile
from ultralytics import YOLO

app = FastAPI(title="AI Vision Inference Service", version="1.0.0")
MODEL_PATH = Path(os.getenv("MODEL_PATH", "models/best.pt"))
model = None
if MODEL_PATH.is_file():
    model = YOLO(str(MODEL_PATH))

@app.get("/health")
def health() -> dict[str, Any]:
    return {"status": "UP", "modelLoaded": model is not None, "modelPath": str(MODEL_PATH)}

@app.post("/infer")
async def infer(file: UploadFile = File(...)) -> dict[str, Any]:
    if not file.content_type or not file.content_type.startswith("image/"):
        raise HTTPException(status_code=415, detail="Upload must be an image")
    raw = await file.read()
    if not raw or len(raw) > 10 * 1024 * 1024:
        raise HTTPException(status_code=400, detail="Image is empty or exceeds 10 MB")
    image = cv2.imdecode(np.frombuffer(raw, np.uint8), cv2.IMREAD_COLOR)
    if image is None:
        raise HTTPException(status_code=400, detail="Unable to decode image")

    if model is None:
        # Explicit demo fallback; never confuse this with actual model inference.
        return {"model": "not-loaded", "simulated": True, "detections": [], "decision": "REVIEW"}

    try:
        result = model.predict(source=image, verbose=False)[0]
        detections = []
        names = result.names
        if result.boxes is not None:
            for box in result.boxes:
                coords = box.xyxy[0].cpu().tolist()
                class_id = int(box.cls[0].item())
                detections.append({
                    "classId": class_id,
                    "className": str(names[class_id]),
                    "confidence": float(box.conf[0].item()),
                    "x1": float(coords[0]), "y1": float(coords[1]),
                    "x2": float(coords[2]), "y2": float(coords[3]),
                })
        decision = "NG" if detections else "OK"
        return {"model": MODEL_PATH.name, "simulated": False,
                "imageWidth": int(image.shape[1]), "imageHeight": int(image.shape[0]),
                "detections": detections, "decision": decision}
    except Exception as exc:
        raise HTTPException(status_code=500, detail=f"Inference failed: {exc}") from exc
