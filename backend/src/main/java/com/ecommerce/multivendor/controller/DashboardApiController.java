package com.ecommerce.multivendor.controller;

import com.ecommerce.multivendor.entity.User;
import com.ecommerce.multivendor.repository.OrderItemRepository;
import com.ecommerce.multivendor.repository.OrderRepository;
import com.ecommerce.multivendor.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardApiController {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserService userService;

    @GetMapping("/admin/chart")
    public List<Map<String, Object>> getAdminChartData() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        List<Object[]> results = orderRepository.countOrdersByDateAfter(startDate);
        return formatChartData(results);
    }

    @GetMapping("/seller/chart")
    public List<Map<String, Object>> getSellerChartData(@AuthenticationPrincipal UserDetails userDetails) {
        User seller = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        List<Object[]> results = orderItemRepository.countOrderItemsByDateForSellerAfter(seller.getId(), startDate);
        return formatChartData(results);
    }

    private List<Map<String, Object>> formatChartData(List<Object[]> results) {
        List<Map<String, Object>> data = new ArrayList<>();
        for (Object[] result : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("date", result[0].toString());
            map.put("count", result[1]);
            data.add(map);
        }
        return data;
    }
}
