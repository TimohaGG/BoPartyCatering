package com.boparty.bopartycatering.Repos;

import com.boparty.bopartycatering.Models.Position.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IIngredientsRepos extends JpaRepository<Ingredient,Long> {
}
