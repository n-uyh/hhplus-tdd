package io.hhplus.tdd;

public record ErrorResponse(
        String code,
        String message
) {

    public static ErrorResponse error(ErrorCode code) {
        return new ErrorResponse(code.getCode(), code.getMessage());
    }
}
