package id.ac.ui.cs.advprog.hiringgo.authentication.response;

import lombok.Getter;
import java.util.Map;

@Getter
public class LoginResponse {
    private String token;
    private long expiresIn;
    private Map<String, Object> user;

    public LoginResponse setToken(String token) {
        this.token = token;
        return this;
    }

    public LoginResponse setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }
    
    public Map<String, Object> getUser() {
        return user;
    }
    
    public LoginResponse setUser(Map<String, Object> user) {
        this.user = user;
        return this;
    }
}
