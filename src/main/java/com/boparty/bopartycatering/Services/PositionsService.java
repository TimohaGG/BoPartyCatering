package com.boparty.bopartycatering.Services;

import com.boparty.bopartycatering.Models.Position.*;
import com.boparty.bopartycatering.Repos.*;
import org.checkerframework.checker.regex.qual.Regex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PositionsService {

    private final PositionsRepos positionsRepos;

    private final PositionAmountRepos positionAmountRepos;
    private final IIngredientsRepos ingredientsRepos;
    private final IIngAmountRepos ingAmountRepos;
    private final IUnitRepos unitRepos;
    private final IIngCategoryRepos iIngCategoryRepos;
    @Autowired
    public PositionsService(PositionsRepos positionsRepos, PositionAmountRepos positionAmountRepos, IIngredientsRepos ingredientsRepos, IIngAmountRepos iIngAmountRepos, IUnitRepos unitRepos, IIngCategoryRepos iIngCategoryRepos) {
        this.positionsRepos = positionsRepos;
        this.positionAmountRepos = positionAmountRepos;
        this.ingredientsRepos = ingredientsRepos;
        this.ingAmountRepos = iIngAmountRepos;
        this.unitRepos = unitRepos;
        this.iIngCategoryRepos = iIngCategoryRepos;
    }


    public Position getPositionById(Long id){
        Position res = positionsRepos.findById(id).orElse(null);
        return res;
    }

    public void save(PositionAmount position){
        positionAmountRepos.save(position);
    }

    public Position save(Position position){
        return positionsRepos.save(position);
    }

    public void saveIngredients(List<IngredientAmount> ingredients){
        ingAmountRepos.saveAll(ingredients);
    }

    public void saveAll(List<PositionAmount> positions){
        positionAmountRepos.saveAll(positions);
    }

    public void removeZeroPositions(long orderId,List<PositionAmount> positions){
        if(orderId!=0){
            boolean isFound = false;
            List<PositionAmount> tmp =  positionAmountRepos.findAll().stream().filter(x->x.getOrder().getId().equals(orderId)).toList();
            for(PositionAmount positionAmount:tmp){
                for (PositionAmount pos : positions) {
                    if(positionAmount.getPositionId()==pos.getPositionId()){
                        isFound = true;
                    }

                }
                if(!isFound){
                    positionAmountRepos.delete(positionAmount);
                }
                isFound = false;

            }

            //positionAmountRepos.deleteAll(tmp);
        }

    }

    public IngredientAmount addIngredient(IngAmountDTO ingredient, long posId){
        IngredientAmount tmp = new IngredientAmount();
        Position pos = positionsRepos.findById(posId).orElse(null);
        if(pos != null){
            tmp.setPosition(pos);
        }

        Ingredient ingTmp = ingredientsRepos.findById(ingredient.getIngId()).orElse(null);
        if(ingTmp == null){
            return null;
        }

        Units tmpUnit = unitRepos.findById(ingredient.getUnitId()).orElse(null);
        if(tmpUnit == null){
            return null;
        }


        tmp.setPosition(pos);
        tmp.setIngredient(ingTmp);
        tmp.setAmount( ingredient.getAmount());
        tmp.setUnit(tmpUnit);
        return tmp;
    }

    public List<Ingredient> getAllIngredients(){
        return ingredientsRepos.findAll();
    }
    public List<Units> getAllUnits(){
        return unitRepos.findAll();
    }
        public List<IngredientAmount> getSelectedIngs(long posId){
        return ingAmountRepos.findAll().stream().filter(x->x.getPosition().getId()==posId).collect(Collectors.toList());
    }

    public Units getUnitById(long id){
        return unitRepos.findById(id).orElse(null);
    }

    public void removeIngAmount(long posId, List<IngredientAmount> ings){
        List<IngredientAmount> res = ingAmountRepos.findByPositionId(posId);
        res.removeIf(x->ings.stream().anyMatch(y->x.getIngredient().getId()==y.getIngredient().getId()));
        ingAmountRepos.deleteAll(res);
    }

    public Ingredient saveIngredient(Ingredient ingredient){
        return ingredientsRepos.save(ingredient);
    }

    public Ingredient getIngredientById(long id){
        return ingredientsRepos.findById(id).orElse(null);
    }

    public void removeIngredient(long id){
        ingAmountRepos.deleteAllByIngredientId(id);
        ingredientsRepos.deleteById(id);
    }

    public List<Position> getPositions(long categoryId){
        return positionsRepos.findAllByCategoryId(categoryId);
    }

    public void deletePosition(long id){
        positionsRepos.deleteById(id);
    }

    public List<IngredientCategory> getAllIngsCategories(){
        return iIngCategoryRepos.findAll();
    }

    public IngredientCategory findIngById(long id){
        return iIngCategoryRepos.findById(id).orElse(null);
    }

    public List<Position> getPositionsByCategory(Category category){
        return positionsRepos.findAllByCategoryId(category.getId());
    }

    public List<Position> getPositionsByNamePart(String namePart){
        return positionsRepos.findAllByNameContainsIgnoreCase(namePart);
    }

    public List<PositionAmount> parsePositions(String text) {
        System.out.println(text);
        text = text.replaceAll("\n"," ");
        int index = text.indexOf("грн");
        if(index != -1){
            text = text.substring(index+4);
        }
        System.out.println(text);
        List<PositionAmount> positionAmounts = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?<text>.*?)\\s(?<n1>\\d+)\\s(?<n2>\\d+)\\s(?<n3>\\d+)(?=\\s|$)");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            PositionAmount posAmount = parsePosition(matcher);
            if(posAmount!=null)
                positionAmounts.add(posAmount);
        }


        return positionAmounts;
    }

    private PositionAmount parsePosition(Matcher text){
        String name = text.group("text").trim();
        int amount = Integer.parseInt(text.group("n2"));
        System.out.println(name);
        System.out.println(amount);
        return null;
    }
}
