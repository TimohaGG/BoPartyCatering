package com.boparty.bopartycatering.Controllers;

import com.boparty.bopartycatering.Models.Position.*;
import com.boparty.bopartycatering.Services.CategoryService;
import com.boparty.bopartycatering.Services.PositionsService;
import com.boparty.bopartycatering.Services.UserService;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Controller
public class PositionController {

    private final UserService userService;
    private PositionsService positionsService;
    private CategoryService categoriesService;
    private Position temp;

    private List<IngredientAmount> selectedIngredients;
    @Autowired
    public PositionController(PositionsService positionsService, CategoryService categoriesService, UserService userService) {
        this.positionsService = positionsService;
        this.categoriesService = categoriesService;
        this.userService = userService;
        temp = new Position();
        selectedIngredients = new ArrayList<>();
    }

    @GetMapping("/create/position")
    public String createPosition(@Nullable Long posId, Model model) {

        if(posId==null) {
            temp = new Position();
            selectedIngredients = new ArrayList<>();
        }
        model.addAttribute("categories", categoriesService.findAll());
        List<Ingredient> ings = positionsService.getAllIngredients();

        if(selectedIngredients != null) {
            if(posId != null) {
                Position pos = positionsService.getPositionById(posId);
                temp = pos;
                selectedIngredients = pos.getIngredients();
            }

            ings = ings.stream().filter(x->{
                IngredientAmount tmp = selectedIngredients.stream().filter(y-> Objects.equals(y.getIngredient().getId(), x.getId())).findFirst().orElse(null);
                return tmp == null;
            }).toList();
            model.addAttribute("selectedIngs",selectedIngredients);
        }

        model.addAttribute("position", temp);
        model.addAttribute("ingredients",ings);
        model.addAttribute("units",positionsService.getAllUnits());
        model.addAttribute("currentPos", posId== null ? 0:posId);
        return "Positions/createPosition";
    }

    @PostMapping("/create/position")
    public String createPosition(@ModelAttribute("position") Position position,Model model) {
        try{
            if(!position.getMultipartFile().isEmpty())
                position.setImage(position.getMultipartFile().getBytes());
            position.setIngredients(selectedIngredients);
            //positionsService.removeIngAmount(position.getId(),selectedIngredients);
            //
            Position saved = positionsService.save(position);

            saved.getIngredients().forEach(x->{x.setPosition(saved);});


            positionsService.save(saved);
            selectedIngredients.clear();
        }catch (Exception e){
            System.out.printf(e.getMessage());
        }


        return "redirect:/";
    }

    @GetMapping("/positions")
    public String positions(Model model) {
        model.addAttribute("positions",userService.getPositions());
        return "Positions/index";
    }

    @PostMapping("/position/addingredient/{posId}")
    public ResponseEntity<Double> addIngredient(@RequestBody IngAmountDTO ing, Model model, @PathVariable Long posId) {
        IngredientAmount tmp = positionsService.addIngredient(ing, posId);
        if(tmp.getAmount()==0){
            selectedIngredients.removeIf(x->x.getIngredient().getId().equals(tmp.getIngredient().getId()));
            return ResponseEntity.ok(tmp.getAmount());
        }
        if(tmp != null) {
            IngredientAmount selTmp = selectedIngredients.stream().filter(x->x.getIngredient().getId() == tmp.getIngredient().getId()).findFirst().orElse(null);
            if(selTmp==null)
                selectedIngredients.add(tmp);
            else
                selTmp.setAmount(tmp.getAmount());
            return ResponseEntity.ok(tmp.getAmount());
        }
        return ResponseEntity.ok(0.0);

    }

    @PostMapping("/position/updateUnits")
    public ResponseEntity<Boolean> updateUnit(@RequestParam Long id,@RequestParam Long unitId, Model model) {
        selectedIngredients.stream().filter(x->x.getIngredient().getId()==id).findFirst().ifPresent(x->{
            Units tmp = positionsService.getUnitById(unitId);
            if(tmp!=null){
                x.setUnit(tmp);
            }
        });
        return ResponseEntity.ok(true);
    }

    @PostMapping("/ingredient/create")
    public String createIngredient(@RequestBody String name, Model model) {
        Ingredient ing = new Ingredient();
        ing.setName(name);
        ing.setUser(userService.getCurrentUser());
        Ingredient saved = positionsService.saveIngredient(ing);
        model.addAttribute("ing", ing);
        model.addAttribute("units", positionsService.getAllUnits());
        return "fragments/_ingredient :: ingredient";
    }

    @PostMapping("/ingredient/remove/{id}")
    public ResponseEntity<Boolean> removeIngredient(@PathVariable Long id, Model model) {
        Ingredient ing = positionsService.getIngredientById(id);
        if(ing!=null){
            positionsService.removeIngredient(id);
        }
        return ResponseEntity.ok(true);
    }

}
