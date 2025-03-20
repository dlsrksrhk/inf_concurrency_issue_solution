package com.example.stock.facade;

import com.example.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class RedissonLockStockFacade {
    private final RedissonClient redissonClient;
    private final StockService stockService;

    private final String LOCK_NAME_PREFIX = "stock_";

    public void decrease(Long id, Long quantity) throws InterruptedException {
        RLock lock = redissonClient.getLock(LOCK_NAME_PREFIX + id);

        try {
            boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);//wait 10s, lock 1s
            if (!available) {
                System.out.println("Lock not available");
                return;
            }

            stockService.decrease(id, quantity);
        } finally {
            lock.unlock();
        }
    }
}
