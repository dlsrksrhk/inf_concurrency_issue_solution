package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.facade.NamedLockStockFacade;
import com.example.stock.facade.OptimisticLockStockFacade;
import com.example.stock.facade.RedisLettuceLockStockFacade;
import com.example.stock.facade.RedissonLockStockFacade;
import com.example.stock.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockServiceTest {

    @Autowired
    private StockService stockService;

    @Autowired
    private PerssimisticLockStockService perssimisticLockStockService;

    @Autowired
    private OptimisticLockStockFacade optimisticLockStockService;

    @Autowired
    private NamedLockStockFacade namedLockStockFacade;

    @Autowired
    private RedisLettuceLockStockFacade redisLettuceLockStockFacade;

    @Autowired
    private RedissonLockStockFacade redissonLockStockFacade;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    public void insert() {
        Stock stock = new Stock(1L, 100L);

        stockRepository.saveAndFlush(stock);
    }

    @AfterEach
    public void delete() {
        stockRepository.deleteAll();
    }

    @Test
    public void decrease_test() {
        stockService.decrease(1L, 1L);

        Stock stock = stockRepository.findById(1L).orElseThrow();
        // 100 - 1 = 99

        assertEquals(99, stock.getQuantity());
    }


    @Test
    public void 동시에_100명이_주문() throws InterruptedException {
        test100Threads(() -> {
            stockService.decrease(1L, 1L);
        });
    }

    @Test
    public void 동시에_100명이_주문_비관적_락() throws InterruptedException {
        test100Threads(() -> {
            perssimisticLockStockService.decrease(1L, 1L);
        });
    }

    @Test
    public void 동시에_100명이_주문_낙관적_락() throws InterruptedException {
        test100Threads(() -> {
            try {
                optimisticLockStockService.decrease(1L, 1L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void 동시에_100명이_주문_네임드_락() throws InterruptedException {
        test100Threads(() -> {
            namedLockStockFacade.decrease(1L, 1L);
        });
    }

    @Test
    public void 동시에_100명이_주문_Lettuce_락() throws InterruptedException {
        test100Threads(() -> {
            try {
                redisLettuceLockStockFacade.decrease(1L, 1L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void 동시에_100명이_주문_Redisson_락() throws InterruptedException {
        test100Threads(() -> {
            try {
                redissonLockStockFacade.decrease(1L, 1L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private <T> void test100Threads(Runnable job) throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    job.run();
                } finally {
                    latch.countDown();
                }
            });
        }

        //100개의 스레드 처리를 기다림
        latch.await();

        //결과 확인
        Stock stock = stockRepository.findById(1L).orElseThrow();

        // 100 - (100 * 1) = 0
        //재고가 0개이기를 기대
        assertEquals(0, stock.getQuantity());
    }

}