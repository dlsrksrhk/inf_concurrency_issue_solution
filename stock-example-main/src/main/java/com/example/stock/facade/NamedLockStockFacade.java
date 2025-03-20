package com.example.stock.facade;

import com.example.stock.repository.LockJpaRepository;
import com.example.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class NamedLockStockFacade {
    private final LockJpaRepository lockRepository;
    private final StockService stockService;

    private final String LOCK_NAME_PREFIX = "stock_";

    @Transactional
    public void decrease(Long id, Long quantity) {
        try {
            lockRepository.getLock(LOCK_NAME_PREFIX + id);
            stockService.decrease(id, quantity);
        } finally {
            lockRepository.releaseLock(LOCK_NAME_PREFIX + id);
        }
    }
}
