package id.ac.ui.cs.advprog.hiringgo.authentication.model;

import id.ac.ui.cs.advprog.hiringgo.authentication.Enums.UserRoleEnums;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Entity
@DiscriminatorValue("DOSEN")
public class Dosen extends User {
    @Column(unique = true)
    private String nip;

    @Column()
    private String fullName;

    public String getNip() { return nip; }
    public User setNip(String nip) { 
        this.nip = nip; 
        return this;
    }

    public String getFullName() { return fullName; }
    public User setFullName(String fullName) { 
        this.fullName = fullName; 
        return this;
    }
    
    @Override
    public List<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(UserRoleEnums.DOSEN.getValue()));
    }

    public Dosen() {}
    
    public Dosen(String email, String password, String fullName, String nip) {
        setUsername(email);
        setPassword(password);
        setFullName(fullName);
        setNip(nip);
    }
}
