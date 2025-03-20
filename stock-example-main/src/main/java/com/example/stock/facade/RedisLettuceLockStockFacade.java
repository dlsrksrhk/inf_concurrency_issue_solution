package com.example.stock.facade;

import com.example.stock.repository.RedisLockRepository;
import com.example.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedisLettuceLockStockFacade {
    private final RedisLockRepository lockRepository;
    private final StockService stockService;

    private final String LOCK_NAME_PREFIX = "stock_";

    public void decrease(Long id, Long quantity) throws InterruptedException {
        while (!lockRepository.lock(LOCK_NAME_PREFIX + id)) {
            Thread.sleep(100);
        }

        try {
            stockService.decrease(id, quantity);
        } finally {
            lockRepository.unlock(LOCK_NAME_PREFIX + id);
        }
    }
}
