package id.ac.ui.cs.advprog.hiringgo.manajemen_akun.factory;

import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model.Admin;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {    
    public User createAdmin(String email, String password) {
        return new Admin(email, password);
    }
    
    public User createDosen(String nip, String name, String email, String password) {
        return new Dosen(nip, name, email, password);
    }
    
    public User createMahasiswa(String name, String nim, String email, String password) {
        return new Mahasiswa(name, nim, email, password);
    }
    
    public User convertToAdmin(User user) {
        return createAdmin(user.getEmail(), user.getPassword());
    }
    
    public User convertToDosen(User user, String nip, String name) {
        return createDosen(nip, name, user.getEmail(), user.getPassword());
    }
    
    public User convertToMahasiswa(User user, String nim, String name) {
        return createMahasiswa(name, nim, user.getEmail(), user.getPassword());
    }
}