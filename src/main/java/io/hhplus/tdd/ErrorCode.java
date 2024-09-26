package io.hhplus.tdd;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    USER_ID_ERROR("US001","유저 id 오류입니다."),

    POINT_AMOUNT_ERROR("PO001", "포인트 금액 오류입니다."),
    POINT_REMAINING_ERROR("PO002","포인트 잔고부족 오류입니다."),
    ;


    private final String code;
    private final String message;
}
