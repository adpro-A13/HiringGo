package id.ac.ui.cs.advprog.hiringgo.authentication.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DosenTest {

    @Test
    public void testConstructor() {
        Dosen dosen = new Dosen("professor@example.com", "password", "Prof. Name", "12345");
        
        assertEquals("professor@example.com", dosen.getUsername());
        assertEquals("password", dosen.getPassword());
        assertEquals("Prof. Name", dosen.getFullName());
        assertEquals("12345", dosen.getNip());
    }
    
    @Test
    public void testDefaultConstructor() {
        Dosen dosen = new Dosen();
        dosen.setUsername("professor@example.com");
        dosen.setPassword("password");
        dosen.setFullName("Prof. Name");
        dosen.setNip("12345");
        
        assertEquals("professor@example.com", dosen.getUsername());
        assertEquals("password", dosen.getPassword());
        assertEquals("Prof. Name", dosen.getFullName());
        assertEquals("12345", dosen.getNip());
    }
    
    @Test
    public void testGetAuthorities() {
        Dosen dosen = new Dosen();
        
        List<? extends GrantedAuthority> authorities = dosen.getAuthorities();
        
        assertEquals(1, authorities.size());
        assertEquals(new SimpleGrantedAuthority("DOSEN"), authorities.get(0));
    }
    
    @Test
    public void testUserDetailsDefaultMethods() {
        Dosen dosen = new Dosen();
        
        assertTrue(dosen.isAccountNonExpired());
        assertTrue(dosen.isAccountNonLocked());
        assertTrue(dosen.isCredentialsNonExpired());
        assertTrue(dosen.isEnabled());
    }
}