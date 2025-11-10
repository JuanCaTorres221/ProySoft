package com.kairo_emocion.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kairo_emocion.demo.model.Emotion;
import java.util.List;
import java.util.Optional;

public interface EmotionRepository extends JpaRepository<Emotion, Long> {
    Optional<Emotion> findByName(String name);
    List<Emotion> findByIntensity(Integer intensity);
}