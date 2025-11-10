package com.kairo_emocion.demo.controller;

import com.kairo_emocion.demo.model.Emotion;
import com.kairo_emocion.demo.service.EmotionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/emotions")
public class EmotionController {

    @Autowired
    private EmotionService emotionService;

    @GetMapping
    public String listEmotions(Model model) {
        List<Emotion> emotions = emotionService.findAll();
        model.addAttribute("emotions", emotions);
        return "emotion/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("emotion", new Emotion());
        return "emotion/create";
    }

    @PostMapping("/create")
    public String createEmotion(@Valid @ModelAttribute Emotion emotion,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "emotion/create";
        }
        try {
            emotionService.save(emotion);
            return "redirect:/emotions";
        } catch (Exception e) {
            model.addAttribute("error", "Error al crear la emoción: " + e.getMessage());
            return "emotion/create";
        }
    }

    @GetMapping("/{id}")
    public String getEmotionDetails(@PathVariable Long id, Model model) {
        Optional<Emotion> emotion = emotionService.findById(id);
        if (emotion.isPresent()) {
            model.addAttribute("emotion", emotion.get());
            return "emotion/details";
        } else {
            return "redirect:/emotions";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Emotion> emotion = emotionService.findById(id);
        if (emotion.isPresent()) {
            model.addAttribute("emotion", emotion.get());
            return "emotion/edit";
        } else {
            return "redirect:/emotions";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateEmotion(@PathVariable Long id, @Valid @ModelAttribute Emotion emotion,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "emotion/edit";
        }
        try {
            emotion.setId(id);
            emotionService.save(emotion);
            return "redirect:/emotions";
        } catch (Exception e) {
            model.addAttribute("error", "Error al actualizar la emoción: " + e.getMessage());
            return "emotion/edit";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteEmotion(@PathVariable Long id) {
        emotionService.deleteById(id);
        return "redirect:/emotions";
    }
}