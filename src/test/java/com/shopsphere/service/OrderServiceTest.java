package com.shopsphere.service;

import com.shopsphere.dto.OrderItemRequest;
import com.shopsphere.dto.OrderRequest;
import com.shopsphere.dto.OrderResponse;
import com.shopsphere.entity.Order;
import com.shopsphere.entity.Product;
import com.shopsphere.entity.Role;
import com.shopsphere.entity.User;
import com.shopsphere.repository.OrderRepository;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    private User sara() {
        return User.builder().id(1L).fullName("Sara").email("sara@gmail.com")
                .password("hashed").role(Role.CUSTOMER).build();
    }

    private Product mouse(int stock) {
        return Product.builder().id(1L).name("Wireless Mouse")
                .price(new BigDecimal("19.99")).stock(stock).build();
    }

    @Test
    void placeOrder_reducesStockAndCalculatesTotal() {
        Product mouse = mouse(50);
        when(userRepository.findByEmail("sara@gmail.com")).thenReturn(Optional.of(sara()));
        when(productRepository.findById(1L)).thenReturn(Optional.of(mouse));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderRequest request = new OrderRequest(List.of(new OrderItemRequest(1L, 2)));
        OrderResponse response = orderService.placeOrder("sara@gmail.com", request);

        assertEquals(0, new BigDecimal("39.98").compareTo(response.total()));
        assertEquals(48, mouse.getStock());
    }

    @Test
    void placeOrder_notEnoughStock_isRefusedAndNothingIsSaved() {
        Product mouse = mouse(1);
        when(userRepository.findByEmail("sara@gmail.com")).thenReturn(Optional.of(sara()));
        when(productRepository.findById(1L)).thenReturn(Optional.of(mouse));

        OrderRequest request = new OrderRequest(List.of(new OrderItemRequest(1L, 5)));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> orderService.placeOrder("sara@gmail.com", request));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals(1, mouse.getStock());
        verify(orderRepository, never()).save(any());
    }
}