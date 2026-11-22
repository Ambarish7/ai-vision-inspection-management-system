# AI Vision Inspection Management System

A starter repository for an industrial visual-inspection workflow using a Spring Boot API, a Python YOLOv8/OpenCV inference service, and a PLC communication adapter/simulator.

> **Important:** This repository is a runnable starter/scaffold, not evidence that the metrics below were reproduced by this code. Add your own trained `best.pt` model and dataset. The reported model evaluation (87.5% mAP@50, 91.9% recall; 687 train / 154 validation images) belongs in the project documentation only if it matches your actual training run.

## Architecture

```text
Camera / Image Upload
        |
        v
Spring Boot API  ---- PostgreSQL (inspection metadata/history)
        |
        +---- HTTP ----> Python FastAPI inference service
        |                      |
        |                      +---- YOLOv8 + OpenCV (`best.pt`)
        |
        +---- PLC adapter ----> PLC / Modbus TCP simulator
                                  (request, busy, OK, NG, done)
```

Spring Boot is the application/API layer. Python performs model inference. The PLC adapter is isolated behind an interface so the simulator can be replaced with a real PLC integration. Never connect a test simulator to production PLC addresses.

## Repository layout

- `spring-backend/` — Spring Boot REST API and inference-service client.
- `python-inference/` — FastAPI service for YOLOv8 inference; returns class, confidence and bounding-box coordinates.
- `plc-simulator/` — simple Modbus TCP register/bit simulator for development.
- `docs/` — architecture, PLC handshake and API notes.
- `docker-compose.yml` — local development services.

## Prerequisites

- Java 17+
- Maven 3.9+
- Python 3.10 or 3.11 recommended for your selected PyTorch/Ultralytics wheels
- Docker Desktop (optional, for PostgreSQL)
- A trained Ultralytics YOLO model, e.g. `best.pt`

## 1. Run Python inference service

```powershell
cd python-inference
python -m venv .venv
.venv\Scripts\Activate.ps1
pip install -r requirements.txt
$env:MODEL_PATH="models/best.pt"
uvicorn app.main:app --host 0.0.0.0 --port 8001 --reload
```

Place your trained weights at `python-inference/models/best.pt` (create the `models` folder). If no model file is configured, the service starts in **demo mode** and returns a clearly marked simulated result; do not describe demo-mode output as real AI inference.

Health check: `GET http://localhost:8001/health`

Inference: `POST http://localhost:8001/infer` with multipart field `file`.

## 2. Run Spring Boot backend

```powershell
cd spring-backend
mvn spring-boot:run
```

Backend: `http://localhost:8080`

- `GET /api/inspections/health`
- `POST /api/inspections/infer` — multipart image upload; forwards image to Python service.

Set `VISION_SERVICE_URL` to change the Python service URL (default `http://localhost:8001`).

## 3. Run PLC simulator

The included simulator is a lightweight development example. See `plc-simulator/README.md` for its register map and run command. Validate your actual PLC protocol, device addresses, bit numbering and handshake timing against the PLC program before using real equipment.

## PLC handshake concept

A typical handshake is:

1. PLC sets `AI_REQUEST`.
2. Adapter detects request and sets `AI_BUSY`.
3. Inference runs and returns class/confidence/coordinates.
4. Adapter writes `AI_OK` or `AI_NG`, writes result coordinates, then sets `AI_DONE`.
5. PLC acknowledges/clears request; adapter clears busy/done according to the agreed ladder-logic sequence.

The exact sequence and bit addresses are machine-specific. Do not assume the example mapping matches your PLC.

## Model evaluation

Document evaluation using a held-out test set and the exact Ultralytics validation command/configuration. Report mAP@50, recall, precision, dataset split, class names and model version. Do not call mAP@50 “accuracy”.

## Security and production hardening

- Add authentication/authorization before exposing inspection endpoints outside a trusted network.
- Validate file type, file size and image dimensions; scan uploads where appropriate.
- Keep model weights and production credentials out of Git.
- Add durable inspection persistence, audit logging, retry/timeouts and PLC reconnect handling before production use.
- Use TLS and network segmentation for real industrial deployments.

## License

Add a license only after deciding which license you want to apply to your own code. YOLO/Ultralytics, model weights and datasets may have separate license/usage terms.
