package id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dosen extends User{
    public String nip;
    public String name;

    public Dosen(String nip, String name, String email, String password) {
        this.nip = nip;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = UserRole.DOSEN;
    }
}
