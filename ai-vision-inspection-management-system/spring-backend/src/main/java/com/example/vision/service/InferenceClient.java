package com.example.vision.service;

import com.example.vision.dto.InferenceResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class InferenceClient {
    private final RestClient client;

    public InferenceClient(@Value("${vision.service-url:http://localhost:8001}") String baseUrl) {
        this.client = RestClient.builder().baseUrl(baseUrl).build();
    }

    public InferenceResponse infer(byte[] image, String filename, String contentType) {
        ByteArrayResource resource = new ByteArrayResource(image) {
            @Override public String getFilename() { return filename == null ? "inspection.jpg" : filename; }
        };
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource);
        return client.post().uri("/infer")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body).retrieve().body(InferenceResponse.class);
    }
}
