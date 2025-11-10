package com.kairo_emocion.demo.integration;

import com.kairo_emocion.demo.exception.ResourceNotFoundException;
import com.kairo_emocion.demo.model.Diary;
import com.kairo_emocion.demo.model.Emotion;
import com.kairo_emocion.demo.model.User;
import com.kairo_emocion.demo.repository.DiaryRepository;
import com.kairo_emocion.demo.repository.EmotionRepository;
import com.kairo_emocion.demo.repository.UserRepository;
import com.kairo_emocion.demo.service.DiaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class DiaryServiceIntegrationTest {

    @Autowired
    private DiaryService diaryService;

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmotionRepository emotionRepository;

    private User user;
    private Emotion emotion;
    private Diary existingDiary;

    @BeforeEach
    void setUp() {
        // limpiar datos para asegurar aislamiento
        diaryRepository.deleteAll();
        userRepository.deleteAll();
        emotionRepository.deleteAll();

        user = new User();
        user.setName("Usuario Test");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user = userRepository.save(user);

        emotion = new Emotion();
        emotion.setName("Alegría");
        emotion.setColor("amarillo");
        emotion.setIntensity(8);
        emotion.setDescription("Muy contento");
        emotion = emotionRepository.save(emotion);

        existingDiary = new Diary();
        existingDiary.setUser(user);
        existingDiary.setEmotion(emotion);
        existingDiary.setNotes("Entrada inicial");
        existingDiary.setEntryDate(LocalDate.now());
        existingDiary = diaryRepository.save(existingDiary);
    }

    @Test
    @DisplayName("createDiaryEntry: guarda una nueva entrada válida")
    void createDiaryEntry_shouldSave() {
        Diary d = new Diary();
        d.setUser(user);
        d.setEmotion(emotion);
        d.setNotes("Prueba crear");
        d.setEntryDate(LocalDate.now().plusDays(1));

        Diary saved = diaryService.createDiaryEntry(d);

        assertNotNull(saved.getId(), "El ID no debe ser null luego de guardar");
        assertEquals("Prueba crear", saved.getNotes());
        // también verificar que repo contiene la entrada
        assertTrue(diaryRepository.findById(saved.getId()).isPresent());
    }

    @Test
    @DisplayName("createDiaryEntry: lanza IllegalArgumentException si falta usuario/emoción/fecha")
    void createDiaryEntry_missingFields_shouldThrow() {
        Diary d1 = new Diary();
        d1.setEmotion(emotion);
        d1.setEntryDate(LocalDate.now());

        assertThrows(IllegalArgumentException.class, () -> diaryService.createDiaryEntry(d1),
                "Debe lanzar IllegalArgumentException cuando falta user");

        Diary d2 = new Diary();
        d2.setUser(user);
        d2.setEntryDate(LocalDate.now());

        assertThrows(IllegalArgumentException.class, () -> diaryService.createDiaryEntry(d2),
                "Debe lanzar IllegalArgumentException cuando falta emotion");

        Diary d3 = new Diary();
        d3.setUser(user);
        d3.setEmotion(emotion);
        // fecha nula
        assertThrows(IllegalArgumentException.class, () -> diaryService.createDiaryEntry(d3),
                "Debe lanzar IllegalArgumentException cuando falta entryDate");
    }

    @Test
    @DisplayName("findById: devuelve la entrada existente y lanza ResourceNotFoundException si no existe")
    void findById_and_notFound() {
        Diary found = diaryService.findById(existingDiary.getId());
        assertEquals(existingDiary.getNotes(), found.getNotes());

        Long missingId = 99999L;
        assertThrows(ResourceNotFoundException.class, () -> diaryService.findById(missingId));
    }

    @Test
    @DisplayName("updateDiaryEntry: actualiza campos de la entrada existente")
    void updateDiaryEntry_shouldUpdate() {
        Diary update = new Diary();
        update.setUser(user);
        update.setEmotion(emotion);
        update.setNotes("Notas actualizadas");
        update.setEntryDate(LocalDate.now().plusDays(2));

        Diary updated = diaryService.updateDiaryEntry(existingDiary.getId(), update);

        assertEquals("Notas actualizadas", updated.getNotes());
        assertEquals(LocalDate.now().plusDays(2), updated.getEntryDate());
    }

    @Test
    @DisplayName("deleteById: elimina la entrada")
    void deleteById_shouldRemove() {
        Long id = existingDiary.getId();
        diaryService.deleteById(id);
        assertFalse(diaryRepository.findById(id).isPresent());
    }

    @Test
    @DisplayName("isEmotionUsed: detecta si una emoción está siendo usada")
    void isEmotionUsed_shouldReturnTrueWhenUsed() {
        boolean used = diaryService.isEmotionUsed(emotion.getId());
        assertTrue(used);

        // crear una emoción no usada
        Emotion unused = new Emotion();
        unused.setName("Tristeza");
        unused.setColor("azul");
        unused.setIntensity(3);
        unused.setDescription("Triste");
        unused = emotionRepository.save(unused);

        assertFalse(diaryService.isEmotionUsed(unused.getId()));
    }

    @Test
    @DisplayName("findByUserId y findByUserAndDateRange funcionan correctamente")
    void findByUserAndDateRange_and_findByUserId() {
        // ya existe una entrada para 'user' en setUp
        List<Diary> byUser = diaryService.findByUserId(user.getId());
        assertFalse(byUser.isEmpty());
        assertTrue(byUser.stream().anyMatch(d -> d.getId().equals(existingDiary.getId())));

        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now().plusDays(5);
        List<Diary> between = diaryService.findByUserAndDateRange(user.getId(), start, end);
        assertFalse(between.isEmpty());
    }

    @Test
    @DisplayName("existsByUserIdAndEntryDate devuelve true si ya existe una entrada en la fecha")
    void existsByUserIdAndEntryDate_shouldDetect() {
        boolean exists = diaryService.existsByUserIdAndEntryDate(user.getId(), existingDiary.getEntryDate());
        assertTrue(exists);

        boolean notExists = diaryService.existsByUserIdAndEntryDate(user.getId(), LocalDate.now().plusYears(1));
        assertFalse(notExists);
    }

    @Test
    @DisplayName("findAll: devuelve todas las entradas")
    void findAll_shouldReturnList() {
        List<Diary> all = diaryService.findAll();
        assertNotNull(all);
        assertTrue(all.size() >= 1, "Debe contener al menos la entrada creada en setUp");
    }
}
