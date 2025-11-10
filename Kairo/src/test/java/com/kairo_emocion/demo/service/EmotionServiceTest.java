package com.kairo_emocion.demo.service;

import com.kairo_emocion.demo.model.Emotion;
import com.kairo_emocion.demo.repository.EmotionRepository;
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

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmotionServiceTest {

    @Mock
    private EmotionRepository emotionRepository;

    @InjectMocks
    private EmotionService emotionService;

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testCreateEmotion_ShouldReturnSavedEmotion() {
        Emotion emotion = new Emotion();
        emotion.setName("Felicidad");
        emotion.setColor("#FFFF00");
        emotion.setIntensity(5);
        emotion.setDescription("Muy feliz");

        when(emotionRepository.save(any(Emotion.class))).thenAnswer(invocation -> {
            Emotion e = invocation.getArgument(0);
            e.setId(1L);
            return e;
        });

        Emotion saved = emotionService.createEmotion(emotion);

        assertNotNull(saved);
        assertEquals(1L, saved.getId());
        assertEquals("Felicidad", saved.getName());
        assertEquals(5, saved.getIntensity());
        verify(emotionRepository, times(1)).save(emotion);
    }

    @Test
    void testFindById_ShouldReturnEmotion_WhenExists() {
        Emotion emotion = new Emotion();
        emotion.setId(2L);
        emotion.setName("Tristeza");
        emotion.setColor("#0000FF");
        emotion.setIntensity(3);

        when(emotionRepository.findById(2L)).thenReturn(Optional.of(emotion));

        Emotion found = emotionService.findById(2L);

        assertNotNull(found);
        assertEquals("Tristeza", found.getName());
        verify(emotionRepository, times(1)).findById(2L);
    }

    @Test
    void testFindById_ShouldThrowException_WhenNotFound() {
        when(emotionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> emotionService.findById(99L));

        verify(emotionRepository, times(1)).findById(99L);
    }

    @Test
    void testUpdateEmotion_ShouldUpdateFields() {
        Emotion existing = new Emotion();
        existing.setId(3L);
        existing.setName("Vieja Emocion");
        existing.setColor("#111111");
        existing.setIntensity(2);

        when(emotionRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(emotionRepository.save(any(Emotion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Emotion update = new Emotion();
        update.setName("Nueva Emocion");
        update.setColor("#FF0000");
        update.setIntensity(4);
        update.setDescription("Actualizada");

        Emotion result = emotionService.updateEmotion(3L, update);

        assertNotNull(result);
        assertEquals("Nueva Emocion", result.getName());
        assertEquals("#FF0000", result.getColor());
        assertEquals(4, result.getIntensity());
        verify(emotionRepository).findById(3L);
        verify(emotionRepository).save(any(Emotion.class));
    }

    @Test
    void testEmotionValidation_ShouldDetectInvalidData() {
        Emotion invalid = new Emotion();
        invalid.setName("");          // viola @NotBlank
        invalid.setColor("");         // viola @NotBlank
        invalid.setIntensity(11);     // viola @Max(5), asumiendo @Max(5)

        Set<ConstraintViolation<Emotion>> violations = validator.validate(invalid);

        assertFalse(violations.isEmpty(), "Debe haber violaciones de validacion");

        boolean hasNameViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("name"));
        boolean hasColorViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("color"));
        boolean hasIntensityViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("intensity"));

        assertTrue(hasNameViolation);
        assertTrue(hasColorViolation);
        assertTrue(hasIntensityViolation);
    }
}