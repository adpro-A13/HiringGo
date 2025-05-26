package id.ac.ui.cs.advprog.hiringgo.authentication.repository;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    List<User> findAllByEmailIn(Collection<String> emails);
}