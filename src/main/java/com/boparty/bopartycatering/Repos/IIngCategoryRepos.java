package com.boparty.bopartycatering.Repos;

import com.boparty.bopartycatering.Models.Position.IngredientCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IIngCategoryRepos extends JpaRepository<IngredientCategory, Long> {
}
