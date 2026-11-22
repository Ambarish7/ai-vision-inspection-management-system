package com.example.vision.dto;

import java.util.List;

public record InferenceResponse(String model, boolean simulated, Integer imageWidth,
                                Integer imageHeight, List<Detection> detections, String decision) {
    public record Detection(int classId, String className, double confidence,
                            double x1, double y1, double x2, double y2) {}
}
