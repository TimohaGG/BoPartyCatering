package com.boparty.bopartycatering.Repos;

import com.boparty.bopartycatering.Models.Position.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriesRepos extends JpaRepository<Category, Long> {
}
