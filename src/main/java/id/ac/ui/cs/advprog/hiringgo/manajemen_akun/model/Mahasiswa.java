package id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Mahasiswa extends User {
    protected String name;
    protected String nim;

    public Mahasiswa(String name, String nim, String email, String password) {
        this.name = name;
        this.nim = nim;
        this.email = email;
        this.password = password;
        this.role = UserRole.MAHASISWA;
    }
}
