package id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Admin extends User {
    public Admin(String email, String password) {
        this.email = email;
        this.password = password;
        this.role = "ADMIN";
    }
}
