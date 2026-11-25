package com.shopsphere.service;

import com.shopsphere.dto.OrderItemRequest;
import com.shopsphere.dto.OrderRequest;
import com.shopsphere.dto.OrderResponse;
import com.shopsphere.entity.Order;
import com.shopsphere.entity.OrderItem;
import com.shopsphere.entity.OrderStatus;
import com.shopsphere.entity.Product;
import com.shopsphere.entity.User;
import com.shopsphere.repository.OrderRepository;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderResponse placeOrder(String email, OrderRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PLACED)
                .total(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest line : request.items()) {
            Product product = productRepository.findById(line.productId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Product not found: " + line.productId()));

            if (product.getStock() < line.quantity()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "Not enough stock for: " + product.getName());
            }

            product.setStock(product.getStock() - line.quantity());

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(line.quantity())
                    .unitPrice(product.getPrice())
                    .build();
            order.getItems().add(item);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(line.quantity())));
        }

        order.setTotal(total);
        return OrderResponse.from(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> myOrders(String email) {
        return orderRepository.findByUserEmailOrderByCreatedAtDesc(email)
                .stream()
                .map(OrderResponse::from)
                .toList();
    }
}