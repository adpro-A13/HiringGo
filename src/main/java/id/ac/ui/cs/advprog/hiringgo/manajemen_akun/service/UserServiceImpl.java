package id.ac.ui.cs.advprog.hiringgo.manajemen_akun.service;

import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.factory.UserFactory;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model.User;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserFactory userFactory;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserFactory userFactory) {
        this.userRepository = userRepository;
        this.userFactory = userFactory;
    }

    @Override
    public User createAdmin(String email, String password) {
        User admin = userFactory.createAdmin(email, password);
        return userRepository.save(admin);
    }

    @Override
    public User createDosen(String nip, String name, String email, String password) {
        User dosen = userFactory.createDosen(nip, name, email, password);
        return userRepository.save(dosen);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }
    
    @Override
    public boolean changeUserToAdmin(String email) {
        return userRepository.updateUserToAdmin(email);
    }
    
    @Override
    public boolean changeUserToDosen(String email, String nip, String name) {
        return userRepository.updateUserToDosen(email, nip, name);
    }
    
    @Override
    public boolean changeUserToMahasiswa(String email, String nim, String name) {
        return userRepository.updateUserToMahasiswa(email, nim, name);
    }
    
    @Override
    public boolean updateUserPassword(String email, String newPassword) {
        return userRepository.updateUserPassword(email, newPassword);
    }

    @Override
    public boolean deleteUser(String email) {
        return userRepository.deleteByEmail(email);
    }
}