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

    private final PositionsRepos positionsRepos;

    private final PositionAmountRepos positionAmountRepos;
    @Autowired
    public PositionsService(PositionsRepos positionsRepos, PositionAmountRepos positionAmountRepos) {
        this.positionsRepos = positionsRepos;
        this.positionAmountRepos = positionAmountRepos;
    }


    public Position getPositionById(Long id){
        Position res = positionsRepos.findById(id).orElse(null);
        return res;
    }

    public void save(PositionAmount position){
        positionAmountRepos.save(position);
    }

    public void save(Position position){
        positionsRepos.save(position);
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
}
