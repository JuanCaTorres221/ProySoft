package com.kairo_emocion.demo.controller;

import com.kairo_emocion.demo.dto.DiaryRequest;
import com.kairo_emocion.demo.model.Diary;
import com.kairo_emocion.demo.exception.ResourceNotFoundException;
import com.kairo_emocion.demo.model.Emotion;
import com.kairo_emocion.demo.model.User;
import com.kairo_emocion.demo.service.DiaryService;
import com.kairo_emocion.demo.service.UserService;
import com.kairo_emocion.demo.service.EmotionService;
import com.kairo_emocion.demo.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diaries")
public class DiaryController {

    @Autowired
    private DiaryService diaryService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmotionService emotionService;

    @GetMapping
    public List<Diary> getAllDiaries() {
        return diaryService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Diary> getDiaryById(@PathVariable Long id) {
        try {
            Diary diary = diaryService.findById(id);
            return ResponseEntity.ok(diary);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createDiary(@Valid @RequestBody DiaryRequest diaryRequest, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Error de validación: " + result.getAllErrors());
        }
        try {
            // Buscar el usuario
            User user = userService.findById(diaryRequest.getUserId());

            // Buscar la emoción
            Emotion emotion = emotionService.findById(diaryRequest.getEmotionId());

            // Crear la entidad Diary
            Diary diary = new Diary();
            diary.setUser(user);
            diary.setEmotion(emotion);
            diary.setNotes(diaryRequest.getNotes());
            diary.setEntryDate(diaryRequest.getEntryDate());

            Diary savedDiary = diaryService.save(diary);
            return ResponseEntity.ok(savedDiary);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body("Recurso no encontrado: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDiary(@PathVariable Long id, @Valid @RequestBody DiaryRequest diaryRequest, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Error de validación: " + result.getAllErrors());
        }
        try {
            // Buscar el usuario
            User user = userService.findById(diaryRequest.getUserId());

            // Buscar la emoción
            Emotion emotion = emotionService.findById(diaryRequest.getEmotionId());

            Diary diaryData = new Diary();
            diaryData.setUser(user);
            diaryData.setEmotion(emotion);
            diaryData.setNotes(diaryRequest.getNotes());
            diaryData.setEntryDate(diaryRequest.getEntryDate());

            Diary updatedDiary = diaryService.updateDiaryEntry(id, diaryData);
            return ResponseEntity.ok(updatedDiary);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body("Recurso no encontrado: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDiary(@PathVariable Long id) {
        try {
            diaryService.deleteById(id);
            return ResponseEntity.ok("Entrada eliminada");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getDiariesByUser(@PathVariable Long userId) {
        try {
            List<Diary> diaries = diaryService.findByUserId(userId);
            return ResponseEntity.ok(diaries);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}