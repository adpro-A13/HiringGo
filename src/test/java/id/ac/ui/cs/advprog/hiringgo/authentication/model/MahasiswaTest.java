package id.ac.ui.cs.advprog.hiringgo.authentication.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class MahasiswaTest {

    @Test
    public void testConstructor() {
        Mahasiswa mahasiswa = new Mahasiswa("test@example.com", "password", "Test User", "12345678");
        
        assertEquals("test@example.com", mahasiswa.getUsername());
        assertEquals("password", mahasiswa.getPassword());
        assertEquals("Test User", mahasiswa.getFullName());
        assertEquals("12345678", mahasiswa.getNim());
    }
    
    @Test
    public void testDefaultConstructor() {
        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setUsername("test@example.com");
        mahasiswa.setPassword("password");
        mahasiswa.setFullName("Test User");
        mahasiswa.setNim("12345678");
        
        assertEquals("test@example.com", mahasiswa.getUsername());
        assertEquals("password", mahasiswa.getPassword());
        assertEquals("Test User", mahasiswa.getFullName());
        assertEquals("12345678", mahasiswa.getNim());
    }
    
    @Test
    public void testGetAuthorities() {
        Mahasiswa mahasiswa = new Mahasiswa();
        
        List<? extends GrantedAuthority> authorities = mahasiswa.getAuthorities();
        
        assertEquals(1, authorities.size());
        assertEquals(new SimpleGrantedAuthority("MAHASISWA"), authorities.get(0));
    }
    
    @Test
    public void testUserDetailsDefaultMethods() {
        Mahasiswa mahasiswa = new Mahasiswa();
        
        assertTrue(mahasiswa.isAccountNonExpired());
        assertTrue(mahasiswa.isAccountNonLocked());
        assertTrue(mahasiswa.isCredentialsNonExpired());
        assertTrue(mahasiswa.isEnabled());
    }

    @Test
    public void testIdNotNull() {
        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setId(null);
        assertNull(mahasiswa.getId());
    }

    @Test
    public void testSetId() {
        Mahasiswa mahasiswa = new Mahasiswa();
        UUID uuid = UUID.randomUUID();
        mahasiswa.setId(uuid);
        assertNotNull(mahasiswa.getId());
        assertEquals(mahasiswa.getId().getClass(), UUID.class);
        assertEquals(mahasiswa.getId(), uuid);
    }

    @Test
    void testIncrementTokenVersion() {
        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setTokenVersion(5);

        mahasiswa.incrementTokenVersion();

        assertEquals(6, mahasiswa.getTokenVersion());
    }

    @Test
    void testIncrementTokenVersionFromZero() {
        Mahasiswa mahasiswa = new Mahasiswa();
        // tokenVersion defaults to 0
        assertEquals(0, mahasiswa.getTokenVersion());

        mahasiswa.incrementTokenVersion();

        assertEquals(1, mahasiswa.getTokenVersion());
    }

    @Test
    void testMultipleIncrementTokenVersion() {
        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setTokenVersion(10);

        mahasiswa.incrementTokenVersion();
        mahasiswa.incrementTokenVersion();
        mahasiswa.incrementTokenVersion();

        assertEquals(13, mahasiswa.getTokenVersion());
    }
}