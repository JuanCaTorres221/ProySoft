package com.kairo_emocion.demo.integration;

import com.kairo_emocion.demo.model.Emotion;
import com.kairo_emocion.demo.repository.EmotionRepository;
import com.kairo_emocion.demo.service.EmotionService;
import com.kairo_emocion.demo.service.DiaryService;
import com.kairo_emocion.demo.exception.ResourceNotFoundException;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
public class EmotionServiceIntegrationTest {

    @Autowired
    private EmotionService emotionService;

    @Autowired
    private EmotionRepository emotionRepository;

    // Usamos el entity manager para limpiar relaciones sin romper FK
    @Autowired
    private EntityManager entityManager;

    // Mockeamos solo el DiaryService porque depende de otra entidad
    private DiaryService diaryServiceMock;

    private Emotion alegria;
    private Emotion tristeza;

    @BeforeEach
    void initData() {
        // --- El truco: limpiar primero la tabla 'diary' antes de 'emotion' ---
        entityManager.createNativeQuery("DELETE FROM diary").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM emotion").executeUpdate();
        entityManager.flush();

        // Reinyectamos mock de DiaryService
        diaryServiceMock = mock(DiaryService.class);
        emotionService = new EmotionService(emotionRepository);
        // Inyectamos el mock manualmente al servicio
        try {
            var field = EmotionService.class.getDeclaredField("diaryService");
            field.setAccessible(true);
            field.set(emotionService, diaryServiceMock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        alegria = new Emotion();
        alegria.setName("Alegría");
        alegria.setColor("#FFFF00");
        alegria.setIntensity(3);
        alegria.setDescription("Sensación de felicidad");

        tristeza = new Emotion();
        tristeza.setName("Tristeza");
        tristeza.setColor("#0000FF");
        tristeza.setIntensity(2);
        tristeza.setDescription("Sensación de melancolía");

        emotionRepository.save(alegria);
        emotionRepository.save(tristeza);
    }

    // ---------------------------------------------------------------------
    // 1️⃣ Crear emoción exitosamente
    // ---------------------------------------------------------------------
    @Test
    void testCreateEmotionSuccessfully() {
        Emotion nueva = new Emotion();
        nueva.setName("Esperanza");
        nueva.setColor("#00FF00");
        nueva.setIntensity(4);
        nueva.setDescription("Sensación de optimismo");

        Emotion creada = emotionService.createEmotion(nueva);

        assertNotNull(creada.getId());
        assertEquals("Esperanza", creada.getName());
        assertTrue(emotionRepository.findByName("Esperanza").isPresent());
    }

    // ---------------------------------------------------------------------
    // 2️⃣ Error si el nombre ya existe
    // ---------------------------------------------------------------------
    @Test
    void testCreateEmotionFailsWhenNameExists() {
        Emotion duplicada = new Emotion();
        duplicada.setName("Alegría");
        duplicada.setColor("#FFFF00");
        duplicada.setIntensity(3);
        duplicada.setDescription("Duplicado");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> emotionService.createEmotion(duplicada));
        assertTrue(ex.getMessage().contains("Ya existe"));
    }

    // ---------------------------------------------------------------------
    // 3️⃣ Buscar todas las emociones
    // ---------------------------------------------------------------------
    @Test
    void testFindAllEmotions() {
        List<Emotion> emociones = emotionService.findAll();

        assertNotNull(emociones);
        assertTrue(emociones.size() >= 2);
        assertTrue(
                emociones.stream().anyMatch(e -> e.getName().equals("Alegría")) &&
                        emociones.stream().anyMatch(e -> e.getName().equals("Tristeza"))
        );
    }

    // ---------------------------------------------------------------------
    // 4️⃣ Buscar emoción por ID existente
    // ---------------------------------------------------------------------
    @Test
    void testFindByIdSuccess() {
        Emotion encontrada = emotionService.findById(alegria.getId());
        assertEquals("Alegría", encontrada.getName());
    }

    // ---------------------------------------------------------------------
    // 5️⃣ Buscar emoción por ID inexistente
    // ---------------------------------------------------------------------
    @Test
    void testFindByIdNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> emotionService.findById(9999L));
    }

    // ---------------------------------------------------------------------
    // 6️⃣ Actualizar emoción
    // ---------------------------------------------------------------------
    @Test
    void testUpdateEmotion() {
        Emotion data = new Emotion();
        data.setName("Alegría intensa");
        data.setColor("#FFD700");
        data.setIntensity(5);
        data.setDescription("Más fuerte que la alegría común");

        Emotion actualizada = emotionService.updateEmotion(alegria.getId(), data);

        assertEquals("Alegría intensa", actualizada.getName());
        assertEquals(5, actualizada.getIntensity());
    }

    // ---------------------------------------------------------------------
    // 7️⃣ Eliminar emoción correctamente
    // ---------------------------------------------------------------------
    @Test
    void testDeleteEmotionSuccessfully() {
        when(diaryServiceMock.isEmotionUsed(tristeza.getId())).thenReturn(false);

        emotionService.deleteById(tristeza.getId());

        Optional<Emotion> eliminada = emotionRepository.findById(tristeza.getId());
        assertTrue(eliminada.isEmpty());
    }

    // ---------------------------------------------------------------------
    // 8️⃣ Error al eliminar si está en uso en Diary
    // ---------------------------------------------------------------------
    @Test
    void testDeleteEmotionFailsIfUsedInDiary() {
        when(diaryServiceMock.isEmotionUsed(alegria.getId())).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> emotionService.deleteById(alegria.getId()));

        assertTrue(ex.getMessage().contains("No se puede eliminar"));
        verify(diaryServiceMock, times(1)).isEmotionUsed(alegria.getId());
    }

    // ---------------------------------------------------------------------
    // 9️⃣ Buscar por intensidad
    // ---------------------------------------------------------------------
    @Test
    void testFindByIntensity() {
        List<Emotion> result = emotionService.findByIntensity(3);
        assertTrue(result.stream().anyMatch(e -> e.getName().equals("Alegría")));
    }
}

