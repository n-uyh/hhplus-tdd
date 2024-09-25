package io.hhplus.tdd.point;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import io.hhplus.tdd.CustomException;
import io.hhplus.tdd.ErrorCode;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import io.hhplus.tdd.point.service.PointService;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private UserPointRepository userPointRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @InjectMocks
    private PointService pointService;


    @Test
    @DisplayName("특정유저의 포인트 조회시 유저의 id가 0이하인 경우 USER_ID_ERROR 오류가 발생한다")
    void findOneUserPoint_then_USER_ID_ERROR() {
        // 특정유저의 id
        long id = 0L;

        // 포인트 조회시 오류발생
        CustomException e = assertThrows(CustomException.class, () -> pointService.findOneUserPoint(id));

        // 오류코드 검증
        assertEquals(ErrorCode.USER_ID_ERROR.getCode(), e.getErrorCode().getCode());
    }

    @Test
    @DisplayName("특정유저의 포인트 조회성공 시 유저의 id와 유저포인트의 id는 동일하다")
    void findOneUserPoint() {
        // 특정유저의 id
        long id = 1L;
        long existingPoint = 300L;
        when(userPointRepository.selectById(id)).thenReturn(new UserPoint(id,existingPoint,System.currentTimeMillis()));

        // 특정유저의 포인트 조회
        UserPoint userPoint = pointService.findOneUserPoint(id);

        // 검증
        assertEquals(id, userPoint.id());
        assertEquals(existingPoint, userPoint.point());
    }

    @Test
    @DisplayName("특정유저의 포인트 충전 시 유저 id가 0 이하인 경우는 USER_ID_ERROR 오류가 발생한다.")
    void chargeUserPoint_then_USER_ID_ERROR() {
        // case1
        // 특정유저의 id, 충전 금액
        long id = 0;
        long amount = 2000;
        TransactionType type = TransactionType.CHARGE;

        // 충전시 오류발생
        CustomException e = assertThrows(CustomException.class, () -> pointService.chargeOrUse(id, amount, type));

        // 오류코드 검증
        assertEquals(ErrorCode.USER_ID_ERROR.getCode(), e.getErrorCode().getCode());

        // case2
        long id2 = -1;
        CustomException e2 = assertThrows(CustomException.class, () -> pointService.chargeOrUse(id2, amount, type));
        assertEquals(ErrorCode.USER_ID_ERROR.getCode(), e2.getErrorCode().getCode());
    }

    @Test
    @DisplayName("특정유저의 포인트 충전 시 충전금액이 0 이하인 경우는 POINT_AMOUNT_ERROR 오류가 발생한다.")
    void chargeUserPoint_then_POINT_AMOUNT_ERROR() {
        // case1
        // 특정유저의 id, 충전 금액
        long id = 1L;
        long amount = 0;
        TransactionType type = TransactionType.CHARGE;

        // 충전시 오류발생
        CustomException e = assertThrows(CustomException.class, () -> pointService.chargeOrUse(id, amount, type));

        // 오류코드 검증
        assertEquals(ErrorCode.POINT_AMOUNT_ERROR.getCode(), e.getErrorCode().getCode());

        // case2
        long amount2 = -2000;
        CustomException e2 = assertThrows(CustomException.class, () -> pointService.chargeOrUse(id, amount2, type));
        assertEquals(ErrorCode.POINT_AMOUNT_ERROR.getCode(), e2.getErrorCode().getCode());
    }

    @Test
    @DisplayName("특정유저의 포인트 충전 성공케이스 테스트")
    void chargeUserPoint() {
        long id = 1L;
        AtomicLong currentPoint = new AtomicLong(3000L); // 충전,사용 케이스 동시 테스트 위한 가변변수 선언.

        when(userPointRepository.selectById(id))
            .thenAnswer(invocation -> new UserPoint(id, currentPoint.get(), System.currentTimeMillis()));

        when(userPointRepository.insertOrUpdate(eq(id),anyLong()))
            .thenAnswer(invocation -> {
                long updatedPoint = invocation.getArgument(1);
                currentPoint.set(updatedPoint);
                return new UserPoint(id,updatedPoint,System.currentTimeMillis());
            });

        // 충전케이스 테스트
        long charge = 2000L;
        UserPoint userPoint = pointService.chargeOrUse(id, charge, TransactionType.CHARGE);
        assertEquals(5000L, userPoint.point());

        // 사용케이스 테스트
        long use = 200L;
        UserPoint userPoint2 = pointService.chargeOrUse(id, use, TransactionType.USE);
        assertEquals(4800L,  userPoint2.point());
    }

}
