package com.kairo_emocion.demo.seeders;

import com.kairo_emocion.demo.model.Emotion;
import com.kairo_emocion.demo.repository.EmotionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class EmotionSeeder {

    private final EmotionRepository emotionRepository;

    public EmotionSeeder(EmotionRepository emotionRepository) {
        this.emotionRepository = emotionRepository;
    }

    @PostConstruct
    public void seedEmotions() {
        if (emotionRepository.count() == 0) {
            List<Emotion> emotions = List.of(
                    new Emotion(null, "Triste", "Azul", 2, "Sentimiento de pena o melancolía"),
                    new Emotion(null, "Enojado", "Rojo", 4, "Sentimiento de ira o frustración"),
                    new Emotion(null, "Cansado", "Gris", 2, "Sensación de agotamiento físico o mental"),
                    new Emotion(null, "Ansioso", "Naranja", 3, "Estado de inquietud o preocupación"),
                    new Emotion(null, "Sorprendido", "Verde", 4, "Sensación de calma y tranquilidad"),
                    new Emotion(null, "Calmado", "Dorado", 5, "Deseo de lograr algo con entusiasmo"),
                    new Emotion(null, "Motivado", "Violeta", 4, "Reacción ante algo inesperado"),
                    new Emotion(null, "Feliz", "Amarillo", 5, "Sentimiento de alegría y bienestar")

            );
            emotionRepository.saveAll(emotions);
            System.out.println(" Emociones iniciales creadas correctamente.");
        }
    }
}
