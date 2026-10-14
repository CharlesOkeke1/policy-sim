package org.cokeke.policysim.common;

public record ApiResponse<T>(int code, String message, String error, T result) {
    //GENERIC OK
    public static <T> ApiResponse<T> ok(T result) {
        return new ApiResponse<>(200, "success", null, result);
    }

    //GENERIC OK WITH MESSAGE
    public static <T> ApiResponse<T> ok(T result, String mesg) {
        return new ApiResponse<>(200, mesg, null, result);
    }

    //CREATED
    public static <T> ApiResponse<T> ok(int code, T result) {
        return new ApiResponse<>(code, "success", null, result);
    }

    //ERROR
    public static <T> ApiResponse<T> error(int code, String error) {
        return new ApiResponse<>(code, null, error, null);
    }
}
