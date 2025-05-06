package id.ac.ui.cs.advprog.hiringgo.authentication.model;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.persistence.*;


@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
@Entity
@Table(name = "users")
public abstract class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Integer id;

    @Column(unique = true, length = 100, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Override
    public abstract List<? extends GrantedAuthority> getAuthorities();

    @Override
    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public User setUsername(String email) {
        this.email = email;
        return this;
    }
}
