package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointTable userPointTable;

    /**
     * 특정유저의 포인트 조회
     */
    public UserPoint findOneUserPoint(long id) {
        return userPointTable.selectById(id);
    }
}
