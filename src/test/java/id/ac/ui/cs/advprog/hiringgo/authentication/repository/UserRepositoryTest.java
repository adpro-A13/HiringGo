package id.ac.ui.cs.advprog.hiringgo.authentication.repository;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Admin;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void whenFindByEmail_thenReturnUser() {
        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setId(UUID.randomUUID());
        mahasiswa.setUsername("test@example.com");
        mahasiswa.setPassword("password123");
        mahasiswa.setFullName("Test User");
        mahasiswa.setNim("12345678");
        entityManager.persist(mahasiswa);
        entityManager.flush();

        Optional<User> found = userRepository.findByEmail("test@example.com");

        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getUsername());
        assertEquals("Test User", ((Mahasiswa) found.get()).getFullName());
    }

    @Test
    public void whenFindByNonExistentEmail_thenReturnEmpty() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        assertFalse(found.isPresent());
    }

    @Test
    public void whenSaveMultipleUserTypes_thenFindAll() {
        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setId(UUID.randomUUID());
        mahasiswa.setUsername("student@example.com");
        mahasiswa.setPassword("password");
        mahasiswa.setFullName("Student Name");
        mahasiswa.setNim("87654321");
        entityManager.persist(mahasiswa);

        Dosen dosen = new Dosen();
        dosen.setId(UUID.randomUUID());
        dosen.setUsername("professor@example.com");
        dosen.setPassword("password");
        dosen.setNip("12345");
        dosen.setFullName("Professor Name");
        entityManager.persist(dosen);

        Admin admin = new Admin();
        admin.setId(UUID.randomUUID());
        admin.setUsername("admin@example.com");
        admin.setPassword("password");
        entityManager.persist(admin);
        entityManager.flush();

        Iterable<User> users = userRepository.findAll();
        long count = userRepository.count();

        assertEquals(3, count, "Should have 3 users");
        
        boolean foundMahasiswa = false;
        boolean foundDosen = false;
        boolean foundAdmin = false;
        
        for (User user : users) {
            if (user instanceof Mahasiswa) {
                foundMahasiswa = true;
                assertEquals("student@example.com", user.getUsername());
            } else if (user instanceof Dosen) {
                foundDosen = true;
                assertEquals("professor@example.com", user.getUsername());
            } else if (user instanceof Admin) {
                foundAdmin = true;
                assertEquals("admin@example.com", user.getUsername());
            }
        }
        
        assertTrue(foundMahasiswa, "Should have found Mahasiswa");
        assertTrue(foundDosen, "Should have found Dosen");
        assertTrue(foundAdmin, "Should have found Admin");
    }
    
    @Test
    public void whenDelete_thenRemoveUser() {
        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setId(UUID.randomUUID());
        mahasiswa.setUsername("delete@example.com");
        mahasiswa.setPassword("password");
        mahasiswa.setFullName("To Delete");
        mahasiswa.setNim("11111111");
        entityManager.persist(mahasiswa);
        entityManager.flush();
        
        Optional<User> savedUser = userRepository.findByEmail("delete@example.com");
        assertTrue(savedUser.isPresent());
        
        userRepository.delete(savedUser.get());
        entityManager.flush();
        
        Optional<User> deletedUser = userRepository.findByEmail("delete@example.com");
        assertFalse(deletedUser.isPresent());
    }
}