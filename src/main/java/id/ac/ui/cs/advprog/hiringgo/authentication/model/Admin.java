package id.ac.ui.cs.advprog.hiringgo.authentication.model;

import id.ac.ui.cs.advprog.hiringgo.authentication.Enums.UserRoleEnums;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {
    @Override
    public List<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(UserRoleEnums.ADMIN.getValue()));
    }

    public Admin() {}
    
    public Admin(String email, String password) {
        setUsername(email);
        setPassword(password);
    }
}
