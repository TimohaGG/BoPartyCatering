package com.boparty.bopartycatering.Services;

import com.boparty.bopartycatering.Models.Position.Category;
import com.boparty.bopartycatering.Repos.CategoriesRepos;
import com.boparty.bopartycatering.Repos.UserRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoriesRepos categoriesRepos;
    private final UserService userService;
    @Autowired
    public CategoryService(CategoriesRepos categoriesRepos, UserService userService, UserRepos userRepos) {
        this.categoriesRepos = categoriesRepos;
        this.userService = userService;
    }

    public List<Category> findAll() {
        userService.getCategories();
        return userService.getCategories();
    }

    public void save(Category category) {

        categoriesRepos.save(category);
    }
}
