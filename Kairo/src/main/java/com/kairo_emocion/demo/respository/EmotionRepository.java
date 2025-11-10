package com.kairo_emocion.demo.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kairo_emocion.demo.model.Emotion;

public interface EmotionRepository extends JpaRepository<Emotion, Long> { }
