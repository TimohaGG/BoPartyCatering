package com.boparty.bopartycatering.Repos;

import com.boparty.bopartycatering.Models.Position.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PositionsRepos extends JpaRepository<Position,Long> {
    List<Position> findAllByCategoryId(Long categoryId);
}
