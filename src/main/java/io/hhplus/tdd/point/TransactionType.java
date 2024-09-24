package io.hhplus.tdd.point;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 포인트 트랜잭션 종류
 * - CHARGE : 충전
 * - USE : 사용
 */
@AllArgsConstructor
@Getter
public enum TransactionType {
    CHARGE(1),
    USE(-1)
    ;

    private final int sign;
}
