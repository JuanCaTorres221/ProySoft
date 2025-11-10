package com.kairo_emocion.demo.controller;

import com.kairo_emocion.demo.dto.EmotionRequest;
import com.kairo_emocion.demo.model.Emotion;
import com.kairo_emocion.demo.exception.ResourceNotFoundException;
import com.kairo_emocion.demo.service.EmotionService;
import com.kairo_emocion.demo.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.List;

@RestController
@RequestMapping("/api/emotions")
public class EmotionController {

    @Autowired
    private EmotionService emotionService;

    @GetMapping
    public List<Emotion> getAllEmotions() {
        return emotionService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Emotion> getEmotionById(@PathVariable Long id) {
        try {
            Emotion emotion = emotionService.findById(id);
            return ResponseEntity.ok(emotion);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createEmotion(@Valid @RequestBody EmotionRequest emotionRequest, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Error de validación: " + result.getAllErrors());
        }
        try {
            Emotion emotion = new Emotion();
            emotion.setName(emotionRequest.getName());
            emotion.setColor(emotionRequest.getColor());
            emotion.setIntensity(emotionRequest.getIntensity());
            emotion.setDescription(emotionRequest.getDescription());

            Emotion savedEmotion = emotionService.createEmotion(emotion);
            return ResponseEntity.ok(savedEmotion);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmotion(@PathVariable Long id, @Valid @RequestBody EmotionRequest emotionRequest, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Error de validación: " + result.getAllErrors());
        }
        try {
            Emotion emotionData = new Emotion();
            emotionData.setName(emotionRequest.getName());
            emotionData.setColor(emotionRequest.getColor());
            emotionData.setIntensity(emotionRequest.getIntensity());
            emotionData.setDescription(emotionRequest.getDescription());

            Emotion updatedEmotion = emotionService.updateEmotion(id, emotionData);
            return ResponseEntity.ok(updatedEmotion);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmotion(@PathVariable Long id) {
        try {
            emotionService.deleteById(id);
            return ResponseEntity.ok("Emocion eliminada");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}