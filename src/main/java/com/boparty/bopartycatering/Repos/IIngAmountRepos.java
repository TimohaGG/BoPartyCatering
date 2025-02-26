package com.boparty.bopartycatering.Repos;

import com.boparty.bopartycatering.Models.Position.Ingredient;
import com.boparty.bopartycatering.Models.Position.IngredientAmount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IIngAmountRepos extends JpaRepository<IngredientAmount,Long> {
    public List<IngredientAmount> findByPositionId(Long posId);
}
