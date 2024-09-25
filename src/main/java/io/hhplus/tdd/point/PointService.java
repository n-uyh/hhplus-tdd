package io.hhplus.tdd.point;

import io.hhplus.tdd.CustomException;
import io.hhplus.tdd.ErrorCode;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointRepository userPointRepository;
    private final PointHistoryRepository pointHistoryRepository;

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

        // point history insert
        pointHistoryRepository.insert(id, amount, type);

        // userPoint update & return
        UserPoint userPoint = this.userPointRepository.selectById(id);
        return this.userPointRepository.insertOrUpdate(id, userPoint.point() + amount * type.getSign());
    }
}
