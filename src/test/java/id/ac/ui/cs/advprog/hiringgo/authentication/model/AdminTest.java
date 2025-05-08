package id.ac.ui.cs.advprog.hiringgo.authentication.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AdminTest {

    @Test
    public void testConstructor() {
        Admin admin = new Admin("admin@example.com", "password");
        
        assertEquals("admin@example.com", admin.getUsername());
        assertEquals("password", admin.getPassword());
    }
    
    @Test
    public void testDefaultConstructor() {
        Admin admin = new Admin();
        admin.setUsername("admin@example.com");
        admin.setPassword("password");
        
        assertEquals("admin@example.com", admin.getUsername());
        assertEquals("password", admin.getPassword());
    }
    
    @Test
    public void testGetAuthorities() {
        Admin admin = new Admin();
        
        List<? extends GrantedAuthority> authorities = admin.getAuthorities();
        
        assertEquals(1, authorities.size());
        assertEquals(new SimpleGrantedAuthority("ADMIN"), authorities.get(0));
    }
    
    @Test
    public void testUserDetailsDefaultMethods() {
        Admin admin = new Admin();
        
        assertTrue(admin.isAccountNonExpired());
        assertTrue(admin.isAccountNonLocked());
        assertTrue(admin.isCredentialsNonExpired());
        assertTrue(admin.isEnabled());
    }
}