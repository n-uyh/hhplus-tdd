package io.hhplus.tdd.point;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PointServiceTest {

    @Autowired
    PointService pointService;

    @Test
    @DisplayName("특정유저의 포인트 조회시 유저의 id와 유저포인트의 id는 동일하다")
    void findOneUserPoint() {
        // 특정유저의 id
        long id = 1L;

        // 특정유저의 포인트 조회
        UserPoint userPoint = pointService.findOneUserPoint(id);

        // 검증
        assertEquals(id, userPoint.id());
    }

}
