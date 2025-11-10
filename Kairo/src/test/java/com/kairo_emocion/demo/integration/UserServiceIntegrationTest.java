package com.kairo_emocion.demo.integration;

import com.kairo_emocion.demo.model.User;
import com.kairo_emocion.demo.repository.UserRepository;
import com.kairo_emocion.demo.service.UserService;
import com.kairo_emocion.demo.exception.ResourceNotFoundException;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        // Limpieza de datos antes de cada test
        entityManager.createNativeQuery("DELETE FROM diary").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM user").executeUpdate();
        entityManager.flush();

        // Datos iniciales
        user1 = new User();
        user1.setName("Carlos Perez");
        user1.setEmail("carlos@example.com");
        user1.setPassword("123456");

        user2 = new User();
        user2.setName("Laura Gomez");
        user2.setEmail("laura@example.com");
        user2.setPassword("abcdef");

        userRepository.save(user1);
        userRepository.save(user2);
    }

    // ---------------------------------------------------------------------
    // 1️⃣ Crear usuario exitosamente
    // ---------------------------------------------------------------------
    @Test
    void testCreateUserSuccessfully() {
        User nuevo = new User();
        nuevo.setName("Andrés López");
        nuevo.setEmail("andres@example.com");
        nuevo.setPassword("pass123");

        User creado = userService.createUser(nuevo);

        assertNotNull(creado.getId());
        assertEquals("Andrés López", creado.getName());
        assertTrue(userRepository.findByEmail("andres@example.com").isPresent());
    }

    // ---------------------------------------------------------------------
    // 2️⃣ Error si email ya existe
    // ---------------------------------------------------------------------
    @Test
    void testCreateUserFailsWhenEmailExists() {
        User duplicado = new User();
        duplicado.setName("Carlos Perez");
        duplicado.setEmail("carlos@example.com");
        duplicado.setPassword("654321");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> userService.createUser(duplicado));
        assertTrue(ex.getMessage().contains("Ya existe un usuario"));
    }

    // ---------------------------------------------------------------------
    // 3️⃣ Error si la contraseña es muy corta
    // ---------------------------------------------------------------------
    @Test
    void testCreateUserFailsWhenPasswordTooShort() {
        User invalido = new User();
        invalido.setName("Nuevo User");
        invalido.setEmail("nuevo@example.com");
        invalido.setPassword("123"); // Menos de 6 caracteres

        Exception ex = assertThrows(IllegalArgumentException.class, () -> userService.createUser(invalido));
        assertTrue(ex.getMessage().contains("6 caracteres"));
    }

    // ---------------------------------------------------------------------
    // 4️⃣ Buscar todos los usuarios
    // ---------------------------------------------------------------------
    @Test
    void testFindAllUsers() {
        List<User> users = userService.findAll();
        assertTrue(users.size() >= 2);
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals("carlos@example.com")));
    }

    // ---------------------------------------------------------------------
    // 5️⃣ Buscar por ID exitoso
    // ---------------------------------------------------------------------
    @Test
    void testFindByIdSuccess() {
        User encontrado = userService.findById(user1.getId());
        assertEquals("Carlos Perez", encontrado.getName());
    }

    // ---------------------------------------------------------------------
    // 6️⃣ Buscar por ID inexistente
    // ---------------------------------------------------------------------
    @Test
    void testFindByIdNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> userService.findById(9999L));
    }

    // ---------------------------------------------------------------------
    // 7️⃣ Actualizar usuario
    // ---------------------------------------------------------------------
    @Test
    void testUpdateUser() {
        User datos = new User();
        datos.setName("Carlos Actualizado");
        datos.setEmail("carlosnuevo@example.com");
        datos.setPassword("nuevaPass123");

        User actualizado = userService.updateUser(user1.getId(), datos);

        assertEquals("Carlos Actualizado", actualizado.getName());
        assertEquals("carlosnuevo@example.com", actualizado.getEmail());
    }

    // ---------------------------------------------------------------------
    // 8️⃣ Eliminar usuario sin diarios
    // ---------------------------------------------------------------------
    @Test
    void testDeleteUserSuccessfully() {
        userService.deleteById(user2.getId());
        Optional<User> eliminado = userRepository.findById(user2.getId());
        assertTrue(eliminado.isEmpty());
    }

    // ---------------------------------------------------------------------
    // 9️⃣ Error al eliminar usuario con diarios (simulación)
    // ---------------------------------------------------------------------
    @Test
    void testDeleteUserFailsIfHasDiaries() {
        User userConDiarios = new User();
        userConDiarios.setName("Test Diario");
        userConDiarios.setEmail("test@diario.com");
        userConDiarios.setPassword("validpass");

        // Simulamos que tiene diarios
        userConDiarios.setDiarios(List.of(new com.kairo_emocion.demo.model.Diary()));
        userRepository.save(userConDiarios);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> userService.deleteById(userConDiarios.getId()));

        assertTrue(ex.getMessage().contains("tiene entradas"));
    }

    // ---------------------------------------------------------------------
    // 🔟 Buscar por email
    // ---------------------------------------------------------------------
    @Test
    void testFindByEmail() {
        Optional<User> user = userService.findByEmail("carlos@example.com");
        assertTrue(user.isPresent());
        assertEquals("Carlos Perez", user.get().getName());
    }

    // ---------------------------------------------------------------------
    // 11️⃣ Verificar existencia por email
    // ---------------------------------------------------------------------
    @Test
    void testExistsByEmail() {
        assertTrue(userService.existsByEmail("laura@example.com"));
        assertFalse(userService.existsByEmail("noexiste@example.com"));
    }
}
