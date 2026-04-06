package com.boparty.bopartycatering.Repos;

import com.boparty.bopartycatering.Models.Position.Units;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.format.annotation.DurationFormat;

public interface IUnitRepos extends JpaRepository<Units, Long> {
}
