package com.example.vision.controller;

import com.example.vision.dto.InferenceResponse;
import com.example.vision.service.InferenceClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/inspections")
public class InspectionController {
    private final InferenceClient inferenceClient;
    public InspectionController(InferenceClient inferenceClient) { this.inferenceClient = inferenceClient; }

    @GetMapping("/health")
    public ResponseEntity<String> health() { return ResponseEntity.ok("Inspection API is UP"); }

    @PostMapping(value = "/infer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public InferenceResponse infer(@RequestPart("file") MultipartFile file) throws Exception {
        if (file.isEmpty() || file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("Image must be non-empty and no larger than 10 MB");
        }
        String type = file.getContentType();
        if (type == null || !type.startsWith("image/")) {
            throw new IllegalArgumentException("Only image uploads are supported");
        }
        return inferenceClient.infer(file.getBytes(), file.getOriginalFilename(), type);
    }
}
