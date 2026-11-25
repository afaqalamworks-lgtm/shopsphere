package com.shopsphere.controller;

import com.shopsphere.dto.OrderRequest;
import com.shopsphere.dto.OrderResponse;
import com.shopsphere.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrder(Authentication authentication,
                                    @Valid @RequestBody OrderRequest request) {
        return orderService.placeOrder(authentication.getName(), request);
    }

    @GetMapping
    public List<OrderResponse> myOrders(Authentication authentication) {
        return orderService.myOrders(authentication.getName());
    }
}