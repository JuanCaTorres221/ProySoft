package com.kairo_emocion.demo.service;

import com.kairo_emocion.demo.model.Diary;
import com.kairo_emocion.demo.model.User;
import com.kairo_emocion.demo.model.Emotion;
import com.kairo_emocion.demo.repository.DiaryRepository;
import com.kairo_emocion.demo.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiaryServiceTest {

    @Mock
    private DiaryRepository diaryRepository;

    @InjectMocks
    private DiaryService diaryService;

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testCreateDiaryEntry_ShouldReturnSavedDiary() {
        User user = new User();
        user.setId(1L);

        Emotion emotion = new Emotion();
        emotion.setId(1L);

        Diary diary = new Diary();
        diary.setUser(user);
        diary.setEmotion(emotion);
        diary.setNotes("Hoy me siento bien");
        diary.setEntryDate(LocalDate.of(2025, 1, 1));

        when(diaryRepository.save(any(Diary.class))).thenAnswer(invocation -> {
            Diary d = invocation.getArgument(0);
            d.setId(10L);
            return d;
        });

        Diary saved = diaryService.createDiaryEntry(diary);

        assertNotNull(saved);
        assertEquals(10L, saved.getId());
        assertEquals("Hoy me siento bien", saved.getNotes());
        verify(diaryRepository, times(1)).save(diary);
    }

    @Test
    void testFindByUserId_ShouldReturnList() {
        User user = new User();
        user.setId(2L);

        Diary diary = new Diary();
        diary.setId(20L);
        diary.setUser(user);
        diary.setNotes("Entrada de prueba");
        diary.setEntryDate(LocalDate.now());

        when(diaryRepository.findByUserId(2L))
                .thenReturn(Collections.singletonList(diary));

        var result = diaryService.findByUserId(2L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(20L, result.get(0).getId());
        verify(diaryRepository, times(1)).findByUserId(2L);
    }

    @Test
    void testExistsByUserIdAndEntryDate_ShouldReturnTrue_WhenExists() {
        Long userId = 3L;
        LocalDate date = LocalDate.of(2025, 1, 1);

        when(diaryRepository.existsByUserIdAndEntryDate(userId, date))
                .thenReturn(true);

        boolean exists = diaryService.existsByUserIdAndEntryDate(userId, date);

        assertTrue(exists);
        verify(diaryRepository, times(1))
                .existsByUserIdAndEntryDate(userId, date);
    }

    @Test
    void testFindById_ShouldThrowException_WhenNotFound() {
        when(diaryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> diaryService.findById(99L));

        verify(diaryRepository, times(1)).findById(99L);
    }

    @Test
    void testDiaryValidation_ShouldDetectInvalidData() {
        // asumimos @NotNull en user, emotion y entryDate
        Diary invalid = new Diary();
        invalid.setUser(null);
        invalid.setEmotion(null);
        invalid.setEntryDate(null);

        Set<ConstraintViolation<Diary>> violations = validator.validate(invalid);

        assertFalse(violations.isEmpty(), "Debe haber violaciones de validacion");

        boolean hasUserViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("user"));
        boolean hasEmotionViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("emotion"));
        boolean hasEntryDateViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("entryDate"));

        assertTrue(hasUserViolation);
        assertTrue(hasEmotionViolation);
        assertTrue(hasEntryDateViolation);
    }
}