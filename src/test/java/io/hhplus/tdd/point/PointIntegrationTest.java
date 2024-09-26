package io.hhplus.tdd.point;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.service.PointService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 포인트 통합테스트 (동시성 문제 확인)
 */
@SpringBootTest
@AutoConfigureMockMvc
public class PointIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PointService pointService;


    @Test
    void test() throws InterruptedException {
        // given
        long userId = 1L;
        final int COUNT = 20;

        ExecutorService executorService = Executors.newFixedThreadPool(COUNT);
        CountDownLatch latch = new CountDownLatch(COUNT);

        for (long i = 0; i < COUNT; i++) {

            try {
                executorService.submit(() -> {
                    pointService.chargeOrUse(userId, 200L, TransactionType.CHARGE);
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                latch.countDown();
            }

            try {
                executorService.submit(() -> {
                    pointService.chargeOrUse(userId, 100L, TransactionType.USE);
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                latch.countDown();
            }
        }

        latch.await();
        executorService.shutdown();

        // 모든 작업 완료 후 최종 검증
        UserPoint userPoint = pointService.findOneUserPoint(userId);
        assertEquals(100 * COUNT, userPoint.point());
    }

}
