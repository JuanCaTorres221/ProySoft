package com.kairo_emocion.demo.service;

import com.kairo_emocion.demo.exception.ResourceNotFoundException;
import com.kairo_emocion.demo.model.User;
import com.kairo_emocion.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    public User getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    public User save(User user) {
        return repo.save(user);
    }

    public User createZone(User z) {
        return repo.save(z);
    }

    public List<User> getAll() { return repo.findAll(); }


    public User updateUser(Long id, User data) {
        User z = getById(id);

        z.setName(data.getName());
        z.setEmail(data.getEmail());
        z.setPassword(data.getPassword());
        return repo.save(z);
    }

    public void deleteUser(Long id) {
        User z = getById(id);
        if (z.getDiarios() != null && !z.getDiarios().isEmpty()) {
            throw new IllegalStateException("Cannot delete user that still has emotions");
        }
        repo.delete(z);
    }

    public Optional<User> findByEmail(String email) {
        return repo.findByEmail(email);
    }
}
