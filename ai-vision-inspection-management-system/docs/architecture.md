# Architecture notes

## Responsibilities

- **Spring Boot:** API boundary, request validation, workflow orchestration, identity/security extension point, inspection metadata persistence extension point.
- **Python inference service:** image decoding, YOLOv8 prediction and normalized detection response.
- **PLC adapter:** translates inspection outcomes into machine-specific PLC protocol writes. Keep protocol details out of inference logic.
- **PLC simulator:** development-only testing aid.

## Inference response contract

```json
{
  "model": "yolov8",
  "simulated": false,
  "detections": [
    {"classId": 0, "className": "defect", "confidence": 0.93,
     "x1": 120.0, "y1": 80.0, "x2": 240.0, "y2": 190.0}
  ],
  "decision": "NG"
}
```

Coordinates are pixel coordinates in the submitted image. Define and document any resizing/coordinate transform if the downstream PLC expects camera-native coordinates.
