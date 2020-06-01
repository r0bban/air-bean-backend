package io.r0bban.airbean.integration;

import io.r0bban.airbean.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface UserRepository extends JpaRepository<User, Long>{

    User findByEmailIs(String email);
}
