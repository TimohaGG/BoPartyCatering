package com.boparty.bopartycatering.Controllers;

import com.boparty.bopartycatering.Models.Position.Category;
import com.boparty.bopartycatering.Services.CategoryService;
import com.boparty.bopartycatering.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CategoryController {
    private final CategoryService categoryService;
    private final UserService userService;
    @Autowired
    public CategoryController(CategoryService categoryService, UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }
    @PostMapping("/category/add")
    public ResponseEntity<Category> addCategory(String categoryName) {
        if(categoryName.isEmpty())
            return ResponseEntity.badRequest().body(null);
        Category category = new Category();
        category.setName(categoryName);
        category.setUser(userService.getCurrentUser());
        categoryService.save(category);
        return ResponseEntity.ok(category);
    }


}
