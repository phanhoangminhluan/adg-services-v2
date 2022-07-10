package com.adg.api.department.InternationalPayment.inventory.service;

import com.adg.api.department.InternationalPayment.inventory.repository.OrderTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.11 04:20
 */
@Service
public class OrderTransactionService {

    @Autowired
    private OrderTransactionRepository repository;

    public double countReleaseQuantityByOrderId(UUID orderId) {
        Double val = this.repository.countReleaseQuantityByOrderId(orderId);
        return Objects.isNull(val) ? 0 : val;
    }
}
