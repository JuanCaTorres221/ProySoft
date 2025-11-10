package com.kairo_emocion.demo.controller;

import com.kairo_emocion.demo.model.User;
import com.kairo_emocion.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "user/list";
    }


    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        return "user/create";
    }

    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "user/create";
        }
        try {
            userService.save(user);
            return "redirect:/users";
        } catch (Exception e) {
            model.addAttribute("error", "Error al crear el usuario: " + e.getMessage());
            return "user/create";
        }
    }


    @GetMapping("/{id}")
    public String getUserDetails(@PathVariable Long id, Model model) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "user/details";
        } else {
            return "redirect:/users";
        }
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "user/edit";
        } else {
            return "redirect:/users";
        }
    }


    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id, @Valid @ModelAttribute User user,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "user/edit";
        }
        try {
            user.setId(id);
            userService.save(user);
            return "redirect:/users";
        } catch (Exception e) {
            model.addAttribute("error", "Error al actualizar el usuario: " + e.getMessage());
            return "user/edit";
        }
    }


    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/users";
    }
}
