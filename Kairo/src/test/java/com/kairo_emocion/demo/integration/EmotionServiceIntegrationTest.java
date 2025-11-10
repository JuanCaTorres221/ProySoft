package com.kairo_emocion.demo.integration;

import com.kairo_emocion.demo.model.Emotion;
import com.kairo_emocion.demo.repository.EmotionRepository;
import com.kairo_emocion.demo.service.EmotionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class EmotionServiceIntegrationTest {

    @Autowired
    private EmotionService emotionService;

    @Autowired
    private EmotionRepository emotionRepository;

    private Emotion emotion1;
    private Emotion emotion2;

    @BeforeEach
    void setUp() {
        emotionRepository.deleteAll();

        emotion1 = new Emotion();
        emotion1.setName("Alegría");
        emotion1.setDescription("Sentimiento de felicidad o satisfacción");
        emotion1.setIntensity(8);
        emotion1.setColor("#FFD700");

        emotion2 = new Emotion();
        emotion2.setName("Tristeza");
        emotion2.setDescription("Sentimiento de pesar o melancolía");
        emotion2.setIntensity(4);
        emotion2.setColor("#1E90FF");

        emotionRepository.save(emotion1);
        emotionRepository.save(emotion2);
    }

    @Test
    void testFindAllEmotions() {
        List<Emotion> emotions = emotionService.findAll();
        assertEquals(2, emotions.size());
    }

    @Test
    void testFindEmotionById() {
        Optional<Emotion> found = emotionService.findById(emotion1.getId());
        assertTrue(found.isPresent());
        assertEquals("Alegría", found.get().getName());
    }

    @Test
    void testCreateEmotion() {
        Emotion newEmotion = new Emotion();
        newEmotion.setName("Ira");
        newEmotion.setDescription("Sentimiento de enojo o furia");
        newEmotion.setIntensity(9);
        newEmotion.setColor("#FF0000");

        Emotion saved = emotionService.save(newEmotion);

        assertNotNull(saved.getId());
        assertEquals("Ira", saved.getName());

        List<Emotion> emotions = emotionService.findAll();
        assertEquals(3, emotions.size());
    }

    @Test
    void testUpdateEmotion() {
        emotion1.setIntensity(10);
        Emotion updated = emotionService.save(emotion1);

        assertEquals(10, updated.getIntensity());
    }

    @Test
    void testDeleteEmotion() {
        emotionService.deleteById(emotion2.getId());

        Optional<Emotion> deleted = emotionService.findById(emotion2.getId());
        assertTrue(deleted.isEmpty());
    }

    @Test
    void testFindEmotionByNonExistingId() {
        Optional<Emotion> notFound = emotionService.findById(999L);
        assertTrue(notFound.isEmpty());
    }
}

