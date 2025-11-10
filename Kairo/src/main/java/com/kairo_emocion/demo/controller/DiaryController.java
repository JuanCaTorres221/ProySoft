package com.kairo_emocion.demo.controller;

import com.kairo_emocion.demo.model.Diary;
import com.kairo_emocion.demo.exception.ResourceNotFoundException;
import com.kairo_emocion.demo.service.DiaryService;
import org.springframework.http.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diaries")
public class DiaryController {

    @Autowired
    private DiaryService diaryService;

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
    public ResponseEntity<?> createDiary(@Valid @RequestBody Diary diary, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Error de validación");
        }
        try {
            Diary savedDiary = diaryService.createDiaryEntry(diary);
            return ResponseEntity.ok(savedDiary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDiary(@PathVariable Long id, @Valid @RequestBody Diary diaryData, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Error de validación");
        }
        try {
            Diary updatedDiary = diaryService.updateDiaryEntry(id, diaryData);
            return ResponseEntity.ok(updatedDiary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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