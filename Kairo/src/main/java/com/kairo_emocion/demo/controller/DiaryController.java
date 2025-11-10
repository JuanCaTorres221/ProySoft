package com.kairo_emocion.demo.controller;

import com.kairo_emocion.demo.model.Diary;
import com.kairo_emocion.demo.model.User;
import com.kairo_emocion.demo.model.Emotion;
import com.kairo_emocion.demo.service.DiaryService;
import com.kairo_emocion.demo.service.UserService;
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
@RequestMapping("/diary")
public class DiaryController {

    @Autowired
    private DiaryService DiaryService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmotionService emotionService;

    @GetMapping
    public String listdiary(Model model) {
        List<Diary> entries = DiaryService.findAll();
        model.addAttribute("entries", entries);
        return "diary/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("diary", new Diary());

        List<User> users = userService.findAll();
        List<Emotion> emotions = emotionService.findAll();

        model.addAttribute("users", users);
        model.addAttribute("emotions", emotions);

        return "diary/create";
    }

    @PostMapping("/create")
    public String creatediary(@Valid @ModelAttribute("diary") Diary diary,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            // Recargar datos si hay error
            List<User> users = userService.findAll();
            List<Emotion> emotions = emotionService.findAll();
            model.addAttribute("users", users);
            model.addAttribute("emotions", emotions);
            return "diary/create";
        }
        try {
            DiaryService.save(diary);
            return "redirect:/diary";
        } catch (Exception e) {
            model.addAttribute("error", "Error al crear la entrada: " + e.getMessage());

            // Recargar datos si hay excepción
            List<User> users = userService.findAll();
            List<Emotion> emotions = emotionService.findAll();
            model.addAttribute("users", users);
            model.addAttribute("emotions", emotions);

            return "diary/create";
        }
    }

    @GetMapping("/{id}")
    public String getdiaryDetails(@PathVariable Long id, Model model) {
        Optional<Diary> entry = DiaryService.findById(id);
        if (entry.isPresent()) {
            model.addAttribute("entry", entry.get());
            return "diary/details";
        } else {
            return "redirect:/diary";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Diary> entry = DiaryService.findById(id);
        if (entry.isPresent()) {
            model.addAttribute("diary", entry.get());

            // Cargar usuarios y emociones
            List<User> users = userService.findAll();
            List<Emotion> emotions = emotionService.findAll();
            model.addAttribute("users", users);
            model.addAttribute("emotions", emotions);

            return "diary/edit";
        } else {
            return "redirect:/diary";
        }
    }

    @PostMapping("/edit/{id}")
    public String updatediary(@PathVariable Long id,
                              @Valid @ModelAttribute("diary") Diary diary,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            // Recargar datos si hay error
            List<User> users = userService.findAll();
            List<Emotion> emotions = emotionService.findAll();
            model.addAttribute("users", users);
            model.addAttribute("emotions", emotions);
            return "diary/edit";
        }
        try {
            diary.setId(id);
            DiaryService.save(diary);
            return "redirect:/diary";
        } catch (Exception e) {
            model.addAttribute("error", "Error al actualizar la entrada: " + e.getMessage());

            // Recargar datos si hay excepción
            List<User> users = userService.findAll();
            List<Emotion> emotions = emotionService.findAll();
            model.addAttribute("users", users);
            model.addAttribute("emotions", emotions);

            return "diary/edit";
        }
    }

    @GetMapping("/delete/{id}")
    public String deletediary(@PathVariable Long id) {
        DiaryService.deleteById(id);
        return "redirect:/diary";
    }

    @GetMapping("/user/{userId}")
    public String getEntriesByUser(@PathVariable Long userId, Model model) {
        List<Diary> userEntries = DiaryService.findByUserId(userId);
        Optional<User> user = userService.findById(userId);

        if (user.isPresent()) {
            model.addAttribute("entries", userEntries);
            model.addAttribute("user", user.get());
            return "diary/user-entries";
        } else {
            return "redirect:/users";
        }
    }
}