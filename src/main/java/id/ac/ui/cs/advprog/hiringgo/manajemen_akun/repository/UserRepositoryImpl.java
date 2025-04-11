package id.ac.ui.cs.advprog.hiringgo.manajemen_akun.repository;

import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.factory.UserFactory;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model.Admin;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model.User;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model.UserRole;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<String, User> users = new HashMap<>();
    private final UserFactory userFactory;

    public UserRepositoryImpl(UserFactory userFactory) {
        this.userFactory = userFactory;
        
        Admin defaultAdmin = (Admin) userFactory.createAdmin("admin@hiringgo.com", "admin123");
        users.put(defaultAdmin.getEmail(), defaultAdmin);
    }

    @Override
    public User save(User user) {
        if (users.containsKey(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }
        users.put(user.getEmail(), user);
        return user;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(users.get(email));
    }

    @Override
    public List<User> findByRole(UserRole role) {
        return users.values().stream()
                .filter(user -> user.getRole() == role)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteByEmail(String email) {
        return users.remove(email) != null;
    }
    
    @Override
    public boolean updateUserToAdmin(String email) {
        User user = users.get(email);
        if (user != null) {
            if (user.getRole() == UserRole.ADMIN) {
                return true;
            }
            
            User convertedUser = userFactory.convertToAdmin(user);
            users.put(email, convertedUser);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean updateUserToDosen(String email, String nip, String name) {
        User user = users.get(email);
        if (user != null) {
            if (user.getRole() == UserRole.DOSEN) {
                return true;
            }
            
            User convertedUser = userFactory.convertToDosen(user, nip, name);
            users.put(email, convertedUser);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean updateUserToMahasiswa(String email, String nim, String name) {
        User user = users.get(email);
        if (user != null) {
            if (user.getRole() == UserRole.MAHASISWA) {
                return true;
            }
            
            User convertedUser = userFactory.convertToMahasiswa(user, nim, name);
            users.put(email, convertedUser);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean updateUserPassword(String email, String newPassword) {
        User user = users.get(email);
        if (user != null) {
            user.setPassword(newPassword);
            return true;
        }
        return false;
    }
}