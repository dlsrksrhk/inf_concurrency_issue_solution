package com.example.stock.facade;

import com.example.stock.service.OptimisticLockStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OptimisticLockStockFacade {
    private final OptimisticLockStockService optimisticLockStockService;

    public void decrease(Long id, Long quantity) throws InterruptedException {
        while (true) {
            try {
                optimisticLockStockService.decrease(id, quantity);
                break;
            } catch (Exception e) { //ObjectOptimisticLockingFailureException
                Thread.sleep(50);
            }
        }
    }
}
