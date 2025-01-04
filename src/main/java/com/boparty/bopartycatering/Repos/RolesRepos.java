package com.boparty.bopartycatering.Repos;

import com.boparty.bopartycatering.Models.User.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesRepos extends JpaRepository<Role, Long> {
}
