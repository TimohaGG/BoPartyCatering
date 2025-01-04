package com.boparty.bopartycatering.Repos;

import com.boparty.bopartycatering.Models.Position.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionsRepos extends JpaRepository<Position,Long> {
}
