package com.boparty.bopartycatering.Repos;

import com.boparty.bopartycatering.Models.Position.Position;
import com.google.common.base.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PositionsRepos extends JpaRepository<Position,Long> {
    List<Position> findAllByCategoryId(Long categoryId);
    List<Position> findAllByCategory_User_id(Long categoryUserId);
    List<Position> findAllByNameContainsIgnoreCase(String name);

    Optional<Position> findByName(String name);
    Optional<Position> findByNameContainingIgnoreCase(String name);

    @Query("SELECT p FROM Position p WHERE LOWER(REPLACE(p.name, ' ', '')) = LOWER(REPLACE(:name, ' ', ''))")
    Optional<Position> findByNameIgnoreCaseAndWhitespace(@Param("name") String name);
}
