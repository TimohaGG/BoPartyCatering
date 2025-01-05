package com.boparty.bopartycatering.Repos;

import com.boparty.bopartycatering.Models.Position.PositionAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface PositionAmountRepos extends JpaRepository<PositionAmount, Long> {
}
