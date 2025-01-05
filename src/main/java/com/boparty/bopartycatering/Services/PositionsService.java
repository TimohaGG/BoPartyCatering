package com.boparty.bopartycatering.Services;

import com.boparty.bopartycatering.Models.Position.Category;
import com.boparty.bopartycatering.Models.Position.Position;
import com.boparty.bopartycatering.Models.Position.PositionAmount;
import com.boparty.bopartycatering.Repos.CategoriesRepos;
import com.boparty.bopartycatering.Repos.PositionAmountRepos;
import com.boparty.bopartycatering.Repos.PositionsRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PositionsService {

    private PositionsRepos positionsRepos;
    private CategoriesRepos categoriesRepos;
    private PositionAmountRepos positionAmountRepos;
    @Autowired
    public PositionsService(PositionsRepos positionsRepos, CategoriesRepos categoriesRepos, PositionAmountRepos positionAmountRepos) {
        this.positionsRepos = positionsRepos;
        this.categoriesRepos = categoriesRepos;
        this.positionAmountRepos = positionAmountRepos;
    }

    public List<Category> getCategories(){
        return categoriesRepos.findAll();
    }

    public List<Position> getPositions(){
        return positionsRepos.findAll();
    }

    public Position getPositionById(Long id){
        return positionsRepos.findById(id).orElse(null);
    }
    public void save(PositionAmount position){
        positionAmountRepos.save(position);
    }

    public void saveAll(List<PositionAmount> positions){
        positionAmountRepos.saveAll(positions);
    }
}
