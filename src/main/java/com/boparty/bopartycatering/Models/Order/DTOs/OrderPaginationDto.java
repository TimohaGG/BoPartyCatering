package com.boparty.bopartycatering.Models.Order.DTOs;

import com.boparty.bopartycatering.Models.Order.Orders;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class OrderPaginationDto {
    List<Orders> ordersList;
    long totalAmount;
    int firstPagination;
    int totalPaginationPages = 8;
}
