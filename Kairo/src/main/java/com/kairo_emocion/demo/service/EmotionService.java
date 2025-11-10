package com.kairo_emocion.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

import com.kairo_emocion.demo.repository.EmotionRepository;
import com.kairo_emocion.demo.model.Emotion;
import com.kairo_emocion.demo.exception.ResourceNotFoundException;

@Service
@Transactional
public class EmotionService {

    private final EmotionRepository repo;

    @Autowired
    private DiaryService diaryService;

    @Autowired
    public EmotionService(EmotionRepository repo) {
        this.repo = repo;
    }

    public Emotion createEmotion(Emotion emotion) {
        if (emotion.getName() == null || emotion.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la emoción no puede estar vacío");
        }
        if (emotion.getColor() == null || emotion.getColor().trim().isEmpty()) {
            throw new IllegalArgumentException("El color no puede estar vacío");
        }
        if (emotion.getIntensity() == null || emotion.getIntensity() < 1 || emotion.getIntensity() > 5) {
            throw new IllegalArgumentException("La intensidad debe estar entre 1 y 5");
        }

        if (repo.findByName(emotion.getName()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una emoción con este nombre: " + emotion.getName());
        }

        return repo.save(emotion);
    }

    public List<Emotion> findAll() {
        return repo.findAll();
    }

    public Emotion findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Emoción no encontrada: " + id));
    }

    public Emotion updateEmotion(Long id, Emotion emotionData) {
        Emotion emotion = findById(id);
        emotion.setName(emotionData.getName());
        emotion.setDescription(emotionData.getDescription());
        emotion.setColor(emotionData.getColor());
        emotion.setIntensity(emotionData.getIntensity());
        return repo.save(emotion);
    }

    public Emotion save(Emotion emotion) {
        return repo.save(emotion);
    }

    public void deleteById(Long id) {
        Emotion emotion = findById(id);

        if (diaryService.isEmotionUsed(id)) {
            throw new IllegalStateException("No se puede eliminar la emoción porque está siendo usada en entradas del diario");
        }

        repo.delete(emotion);
    }

    public Optional<Emotion> findByName(String name) {
        return repo.findByName(name);
    }

    public List<Emotion> findByIntensity(Integer intensity) {
        return repo.findByIntensity(intensity);
    }
}