package com.kairo_emocion.demo.service;

import com.kairo_emocion.demo.model.User;
import com.kairo_emocion.demo.repository.UserRepository;
import com.kairo_emocion.demo.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testCreateUser_ShouldReturnSavedUser() {
        User user = new User();
        user.setName("Daniel");
        user.setEmail("daniel@example.com");
        user.setPassword("secreta123");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        User saved = userService.createUser(user);

        assertNotNull(saved);
        assertEquals(1L, saved.getId());
        assertEquals("Daniel", saved.getName());
        assertEquals("daniel@example.com", saved.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testFindById_ShouldReturnUser_WhenExists() {
        User user = new User();
        user.setId(2L);
        user.setName("Liliana");
        user.setEmail("lili@example.com");

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        User found = userService.findById(2L);

        assertNotNull(found);
        assertEquals("Liliana", found.getName());
        verify(userRepository, times(1)).findById(2L);
    }

    @Test
    void testFindById_ShouldThrowException_WhenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.findById(99L));

        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    void testUpdateUser_ShouldUpdateFields() {
        User existing = new User();
        existing.setId(3L);
        existing.setName("Nombre viejo");
        existing.setEmail("viejo@example.com");
        existing.setPassword("vieja123");

        when(userRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User update = new User();
        update.setName("Nombre nuevo");
        update.setEmail("nuevo@example.com");
        update.setPassword("nueva123");

        User result = userService.updateUser(3L, update);

        assertNotNull(result);
        assertEquals("Nombre nuevo", result.getName());
        assertEquals("nuevo@example.com", result.getEmail());
        assertEquals("nueva123", result.getPassword());
        verify(userRepository).findById(3L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testDeleteUser_ShouldDeleteSuccessfully() {
        User user = new User();
        user.setId(4L);
        user.setName("A borrar");

        when(userRepository.findById(4L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user); // 👈 cambiamos aquí

        assertDoesNotThrow(() -> userService.deleteById(4L));

        verify(userRepository, times(1)).findById(4L);
        verify(userRepository, times(1)).delete(user); // 👈 y aquí también
    }


    @Test
    void testUserValidation_ShouldDetectInvalidData() {
        User invalidUser = new User();
        invalidUser.setName("");              // viola @NotBlank y @Size
        invalidUser.setEmail("correo-malo");  // viola @Email
        invalidUser.setPassword("123");       // viola @Size(min = 6)

        Set<ConstraintViolation<User>> violations = validator.validate(invalidUser);

        assertFalse(violations.isEmpty(), "Debe haber violaciones de validacion");

        boolean hasNameViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("name"));

        boolean hasEmailViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"));

        boolean hasPasswordViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("password"));

        assertTrue(hasNameViolation);
        assertTrue(hasEmailViolation);
        assertTrue(hasPasswordViolation);
    }
}