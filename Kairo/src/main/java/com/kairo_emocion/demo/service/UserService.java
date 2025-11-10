package com.kairo_emocion.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;
import com.kairo_emocion.demo.repository.UserRepository;
import com.kairo_emocion.demo.model.User;
import com.kairo_emocion.demo.exception.ResourceNotFoundException;

@Service
@Transactional
public class UserService {

    private UserRepository repo;

    @Autowired
    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public User createUser(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        if (user.getPassword().length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }

        if (repo.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con este email: " + user.getEmail());
        }

        return repo.save(user);
    }

    public List<User> findAll() {
        return repo.findAll();
    }

    public User findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));
    }

    public User updateUser(Long id, User userData) {
        User user = findById(id);
        user.setName(userData.getName());
        user.setEmail(userData.getEmail());
        user.setPassword(userData.getPassword());
        return repo.save(user);
    }

    public User save(User user) {
        return repo.save(user);
    }

    public void deleteById(Long id) {
        User user = findById(id);

        if (user.getDiarios() != null && !user.getDiarios().isEmpty()) {
            throw new IllegalStateException("No se puede eliminar el usuario porque tiene entradas en el diario");
        }

        repo.delete(user);
    }

    public Optional<User> findByEmail(String email) {
        return repo.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return repo.findByEmail(email).isPresent();
    }
}
