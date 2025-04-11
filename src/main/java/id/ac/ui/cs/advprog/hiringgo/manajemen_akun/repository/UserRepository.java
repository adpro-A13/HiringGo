package id.ac.ui.cs.advprog.hiringgo.manajemen_akun.repository;

import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    
    List<User> findAll();
    Optional<User> findByEmail(String email);
    List<User> findByRole(String role);
    
    boolean updateUserToAdmin(String email);
    boolean updateUserToDosen(String email, String nip, String name);
    boolean updateUserToMahasiswa(String email, String nim, String name);
    
    boolean updateUserPassword(String email, String newPassword);
    
    boolean deleteByEmail(String email);
}