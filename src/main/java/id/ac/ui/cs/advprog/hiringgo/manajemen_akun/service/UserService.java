package id.ac.ui.cs.advprog.hiringgo.manajemen_akun.service;

import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model.User;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createAdmin(String email, String password);
    User createDosen(String nip, String name, String email, String password);
    
    List<User> getAllUsers();
    Optional<User> getUserByEmail(String email);
    List<User> getUsersByRole(UserRole role);
    
    boolean changeUserToAdmin(String email);
    boolean changeUserToDosen(String email, String nip, String name);
    boolean changeUserToMahasiswa(String email, String nim, String name);
    
    boolean updateUserPassword(String email, String newPassword);
    
    boolean deleteUser(String email);
}