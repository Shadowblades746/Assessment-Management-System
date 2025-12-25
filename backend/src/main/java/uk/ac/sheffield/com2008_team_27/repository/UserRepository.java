package uk.ac.sheffield.com2008_team_27.repository;

import uk.ac.sheffield.com2008_team_27.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    Optional<User> findByForename(String forename);
    boolean existsByForename(String forename);

    Optional<User> findBySurname(String surname);
    boolean existsBySurname(String surname);

    Optional<User> findByForenameIgnoreCaseAndSurnameIgnoreCase(String forename, String surname);


}
