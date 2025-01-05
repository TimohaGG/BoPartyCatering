package com.boparty.bopartycatering.Services;

import com.boparty.bopartycatering.Models.Position.Category;
import com.boparty.bopartycatering.Repos.CategoriesRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private CategoriesRepos categoriesRepos;
    @Autowired
    public CategoryService(CategoriesRepos categoriesRepos) {
        this.categoriesRepos = categoriesRepos;
    }

    public List<Category> findAll() {
        return categoriesRepos.findAll();
    }
}
