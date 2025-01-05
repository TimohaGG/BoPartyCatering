package com.boparty.bopartycatering.Controllers;

import com.boparty.bopartycatering.Models.Position.Position;
import com.boparty.bopartycatering.Repos.CategoriesRepos;
import com.boparty.bopartycatering.Repos.PositionsRepos;
import com.boparty.bopartycatering.Services.CategoryService;
import com.boparty.bopartycatering.Services.PositionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class PositionController {

    private PositionsService positionsService;
    private CategoryService categoriesService;
    @Autowired
    public PositionController(PositionsService positionsService, CategoryService categoriesService) {
        this.positionsService = positionsService;
        this.categoriesService = categoriesService;
    }

    @GetMapping("/create/position")
    public String createPosition(Model model) {
        model.addAttribute("position", new Position());
        model.addAttribute("categories", categoriesService.findAll());
        return "Positions/createPosition";
    }

    @PostMapping("/create/position")
    public String createPosition(@ModelAttribute("position") Position position,Model model) {
        try{
            position.setImage(position.getMultipartFile().getBytes());
            positionsService.save(position);
        }catch (Exception e){
            System.out.printf(e.getMessage());
        }


        return "redirect:/";
    }

    @GetMapping("/positions")
    public String positions(Model model) {
        model.addAttribute("positions",positionsService.getPositions());
        return "Positions/index";
    }
}
