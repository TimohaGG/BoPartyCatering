package com.boparty.bopartycatering.Repos;

import com.boparty.bopartycatering.Models.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepos extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
