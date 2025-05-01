package id.ac.ui.cs.advprog.hiringgo.authentication.model;

import id.ac.ui.cs.advprog.hiringgo.authentication.Enums.UserRoleEnums;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Entity
@DiscriminatorValue("MAHASISWA")
public class Mahasiswa extends User {
    @Column(unique = true)
    private String nim;

    @Column()
    private String fullName;

    public String getNim() { return nim; }
    public User setNim(String nim) { 
        this.nim = nim; 
        return this;
    }

    public String getFullName() { return fullName; }
    public User setFullName(String fullName) { 
        this.fullName = fullName; 
        return this;
    }

    @Override
    public List<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(UserRoleEnums.MAHASISWA.getValue()));
    }

    public Mahasiswa() {}
    
    public Mahasiswa(String email, String password, String fullName, String nim) {
        setUsername(email);
        setPassword(password);
        setFullName(fullName);
        setNim(nim);
    }
}
