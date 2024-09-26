package io.hhplus.tdd.point.service;

import io.hhplus.tdd.CustomException;
import io.hhplus.tdd.ErrorCode;
import io.hhplus.tdd.point.LockManager;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import java.util.List;
import java.util.concurrent.locks.Lock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointRepository userPointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    private final LockManager lockManager = new LockManager();

    /**
     * 특정유저의 포인트 조회
     */
    public UserPoint findOneUserPoint(long id) {
        // user 검증 - 유저 id가 0 이하인 경우 오류 발생
        if (id <= 0) {
            throw new CustomException(ErrorCode.USER_ID_ERROR);
        }
        return userPointRepository.selectById(id);
    }


    /**
     * 특정유저의 포인트 충전 및 사용
     */
    public UserPoint chargeOrUse(long id, long amount, TransactionType type) {
        // user 검증 - 유저 id가 0 이하인 경우 오류 발생
        if (id <= 0) {
            throw new CustomException(ErrorCode.USER_ID_ERROR);
        }

        // point 금액 검증 - 금액이 0 이하일 경우 오류 발생
        if (amount <= 0) {
            throw new CustomException(ErrorCode.POINT_AMOUNT_ERROR);
        }

        Lock lock = lockManager.getLock(id);
        try {
            lock.lock();

            UserPoint userPoint = userPointRepository.selectById(id);
            long postPoint = userPoint.point() + amount * type.getSign();
            long min = 0;
            long max = 100_000L;
            if (postPoint < min) {
                throw new CustomException(ErrorCode.POINT_MIN_ERROR);
            } else if (postPoint > max) {
                throw new CustomException(ErrorCode.POINT_MAX_ERROR);
            }

            // point history insert
            pointHistoryRepository.insert(id, amount, type);

            // userPoint update & return
            return userPointRepository.insertOrUpdate(id, postPoint);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 특정유저의 포인트 충전/사용 내역 조회
     */
    public List<PointHistory> findPointHistoriesByUserId(long userId) {
        return pointHistoryRepository.selectAllByUserId(userId);
    }
}
