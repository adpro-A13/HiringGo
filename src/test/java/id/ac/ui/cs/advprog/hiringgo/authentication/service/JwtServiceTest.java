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


    @Test
    void isTokenValid_withNonUserInstance_shouldReturnFalse() {
        // Test the first branch: !(userDetails instanceof User user)
        UserDetails nonUserDetails = mock(UserDetails.class);
        when(nonUserDetails.getUsername()).thenReturn(testUsername);

        String token = jwtService.generateToken(userDetails);

        boolean isValid = jwtService.isTokenValid(token, nonUserDetails);

        assertFalse(isValid);
    }

    @Test
    void isTokenValid_withNullTokenVersion_shouldReturnFalse() {
        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setId(UUID.randomUUID());
        mahasiswa.setUsername(testUsername);
        mahasiswa.setPassword("password");
        mahasiswa.setTokenVersion(1);

        UserDetails nonUserDetails = mock(UserDetails.class);
        when(nonUserDetails.getUsername()).thenReturn(testUsername);

        String tokenWithoutVersion = jwtService.generateToken(nonUserDetails);

        boolean isValid = jwtService.isTokenValid(tokenWithoutVersion, mahasiswa);

        assertFalse(isValid);
    }

    @Test
    void isTokenValid_withMatchingTokenVersionButDifferentUsername_shouldReturnFalse() {
        // Test the final return condition where tokenVersion matches but username doesn't
        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setId(UUID.randomUUID());
        mahasiswa.setUsername(testUsername);
        mahasiswa.setPassword("password");
        mahasiswa.setTokenVersion(1);

        String token = jwtService.generateToken(mahasiswa);

        // Create another user with same token version but different username
        Mahasiswa differentMahasiswa = new Mahasiswa();
        differentMahasiswa.setId(UUID.randomUUID());
        differentMahasiswa.setUsername("different@example.com");
        differentMahasiswa.setPassword("password");
        differentMahasiswa.setTokenVersion(1); // Same token version

        boolean isValid = jwtService.isTokenValid(token, differentMahasiswa);

        assertFalse(isValid);
    }

    @Test
    void isTokenValid_withExpiredTokenButValidVersionAndUsername_shouldReturnFalse() {
        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setId(UUID.randomUUID());
        mahasiswa.setUsername(testUsername);
        mahasiswa.setPassword("password");
        mahasiswa.setTokenVersion(1);

        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L); // -1 second

        String expiredToken = jwtService.generateToken(mahasiswa);

        ReflectionTestUtils.setField(jwtService, "jwtExpiration", testExpirationTime);

        boolean isValid = false;
        try {
            isValid = jwtService.isTokenValid(expiredToken, mahasiswa);
        } catch (Exception e) {
            isValid = false;
        }

        assertFalse(isValid);
    }

    @Test
    void isTokenValid_withValidTokenVersionUsernameAndNotExpired_shouldReturnTrue() {
        // Test the successful path where all conditions pass
        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setId(UUID.randomUUID());
        mahasiswa.setUsername(testUsername);
        mahasiswa.setPassword("password");
        mahasiswa.setTokenVersion(1);

        String token = jwtService.generateToken(mahasiswa);

        boolean isValid = jwtService.isTokenValid(token, mahasiswa);

        assertTrue(isValid);
    }

    @Test
    void generateToken_withUserDetails_shouldIncludeTokenVersion() {
        // Test that generateToken(UserDetails) includes tokenVersion when userDetails is instanceof User
        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setId(UUID.randomUUID());
        mahasiswa.setUsername(testUsername);
        mahasiswa.setPassword("password");
        mahasiswa.setTokenVersion(5);

        String token = jwtService.generateToken(mahasiswa);

        Integer extractedTokenVersion = jwtService.extractClaim(token, claims -> claims.get("tokenVersion", Integer.class));

        assertNotNull(extractedTokenVersion);
        assertEquals(5, extractedTokenVersion);
    }

    @Test
    void generateToken_withExtraClaimsAndUserDetails_shouldIncludeTokenVersion() {
        // Test that generateToken(Map, UserDetails) includes tokenVersion when userDetails is instanceof User
        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setId(UUID.randomUUID());
        mahasiswa.setUsername(testUsername);
        mahasiswa.setPassword("password");
        mahasiswa.setTokenVersion(3);

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("customClaim", "customValue");

        String token = jwtService.generateToken(extraClaims, mahasiswa);

        Integer extractedTokenVersion = jwtService.extractClaim(token, claims -> claims.get("tokenVersion", Integer.class));
        String customValue = jwtService.extractClaim(token, claims -> claims.get("customClaim", String.class));

        assertNotNull(extractedTokenVersion);
        assertEquals(3, extractedTokenVersion);
        assertEquals("customValue", customValue);
    }

    @Test
    void generateToken_withNonUserUserDetails_shouldNotIncludeTokenVersion() {
        // Test that generateToken doesn't include tokenVersion when userDetails is not instanceof User
        UserDetails nonUserDetails = mock(UserDetails.class);
        when(nonUserDetails.getUsername()).thenReturn(testUsername);

        String token = jwtService.generateToken(nonUserDetails);

        Integer extractedTokenVersion = jwtService.extractClaim(token, claims -> claims.get("tokenVersion", Integer.class));

        assertNull(extractedTokenVersion);
    }

    @Test
    void isTokenValid_tokenVersionComparisonWithDifferentTypes_shouldReturnFalse() {
        // Test when user's tokenVersion is different from what's in the token
        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setId(UUID.randomUUID());
        mahasiswa.setUsername(testUsername);
        mahasiswa.setPassword("password");
        mahasiswa.setTokenVersion(1);

        // Generate a token with the current version
        String token = jwtService.generateToken(mahasiswa);

        // Now change the user's tokenVersion to simulate version mismatch
        mahasiswa.setTokenVersion(2); // Different version

        boolean isValid = jwtService.isTokenValid(token, mahasiswa);

        // Should return false because token has version 1 but user now has version 2
        assertFalse(isValid);
    }


    @Test
    void isTokenValid_withMatchingUsernameButExpiredToken_shouldReturnFalse() {
        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setId(UUID.randomUUID());
        mahasiswa.setUsername(testUsername);
        mahasiswa.setPassword("password");
        mahasiswa.setTokenVersion(1);

        String token = jwtService.generateToken(mahasiswa);

        JwtService spyJwtService = spy(jwtService);

        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1L);
        String veryShortToken = jwtService.generateToken(mahasiswa);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", testExpirationTime);

        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        boolean isValid = true;
        try {
            isValid = jwtService.isTokenValid(veryShortToken, mahasiswa);
        } catch (Exception e) {
            isValid = false;
        }

        assertFalse(isValid);
    }

    @Test
    void isTokenValid_usernameMatchesButTokenExpired_shouldReturnFalse() {
        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setId(UUID.randomUUID());
        mahasiswa.setUsername(testUsername);
        mahasiswa.setPassword("password");
        mahasiswa.setTokenVersion(1);

        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -86400000L);
        String expiredToken = jwtService.generateToken(mahasiswa);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", testExpirationTime);

        boolean isValid = true;
        try {
            isValid = jwtService.isTokenValid(expiredToken, mahasiswa);
        } catch (Exception e) {
            isValid = false;
        }

        assertFalse(isValid);
    }
}