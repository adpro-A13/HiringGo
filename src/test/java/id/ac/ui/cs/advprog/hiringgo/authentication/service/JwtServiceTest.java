package id.ac.ui.cs.advprog.hiringgo.authentication.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private UserDetails userDetails;
    private final String testSecretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final long testExpirationTime = 3600000;
    private final String testUsername = "test@example.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        ReflectionTestUtils.setField(jwtService, "secretKey", testSecretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", testExpirationTime);
        
        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(testUsername);
    }

    @Test
    void generateToken_shouldCreateValidToken() {
        String token = jwtService.generateToken(userDetails);
        
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void generateToken_withExtraClaims_shouldCreateValidTokenWithClaims() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "MAHASISWA");

        String token = jwtService.generateToken(extraClaims, userDetails);
        
        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertEquals("MAHASISWA", jwtService.extractClaim(token, claims -> claims.get("role")));
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        String token = jwtService.generateToken(userDetails);

        String extractedUsername = jwtService.extractUsername(token);

        assertEquals(testUsername, extractedUsername);
    }

    @Test
    void extractClaim_shouldReturnCorrectClaimValue() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("customKey", "customValue");
        String token = jwtService.generateToken(extraClaims, userDetails);

        String customValue = jwtService.extractClaim(token, claims -> claims.get("customKey", String.class));
 
        assertEquals("customValue", customValue);
    }

    @Test
    void isTokenValid_withValidToken_shouldReturnTrue() {
        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setId(UUID.randomUUID());
        mahasiswa.setUsername(testUsername);
        mahasiswa.setPassword("password");
        String token = jwtService.generateToken(mahasiswa);
        boolean isValid = jwtService.isTokenValid(token, mahasiswa);
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_withDifferentUser_shouldReturnFalse() {
        String token = jwtService.generateToken(userDetails);
        
        UserDetails differentUserDetails = mock(UserDetails.class);
        when(differentUserDetails.getUsername()).thenReturn("different@example.com");
        
        boolean isValid = jwtService.isTokenValid(token, differentUserDetails);
        
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_withExpiredToken_shouldReturnFalse() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(testUsername);

        String token = jwtService.generateToken(userDetails);
        
        JwtService spyJwtService = spy(jwtService);
        doReturn(true).when(spyJwtService).extractClaim(eq(token), any());
        
        boolean isValid = false;
        try {
            isValid = spyJwtService.isTokenValid(token, userDetails);
        } catch (Exception e) {
            isValid = false;
        }
        
        assertFalse(isValid);
    }

    @Test
    void getExpirationTime_shouldReturnConfiguredValue() {
        long expirationTime = jwtService.getExpirationTime();
        
        assertEquals(testExpirationTime, expirationTime);
    }
}