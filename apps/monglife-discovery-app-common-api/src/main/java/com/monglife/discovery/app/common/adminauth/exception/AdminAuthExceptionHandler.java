package com.monglife.discovery.app.common.adminauth.exception;

import com.monglife.core.dto.response.ResponseDto;
import com.monglife.discovery.app.common.adminauth.controller.AdminAuthController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * 관리자 로그인 컨트롤러 한정. 그 밖의 ErrorException 은 CommonExceptionHandler 가 400 으로 내린다.
 * 401 은 앱의 전역 인터셉터와 충돌하므로 쓰지 않는다.
 */
@RestControllerAdvice(basePackageClasses = AdminAuthController.class)
public class AdminAuthExceptionHandler {

    @ExceptionHandler(NotAdminAccountException.class)
    private ResponseEntity<ResponseDto<Map<String, Object>>> handleNotAdminAccount(NotAdminAccountException e) {
        return status(HttpStatus.FORBIDDEN, e.getErrorCode().toResponseDto(HttpStatus.FORBIDDEN.value(), e.getResult()));
    }

    @ExceptionHandler(TooManyEmailCodeRequestsException.class)
    private ResponseEntity<ResponseDto<Map<String, Object>>> handleTooManyRequests(TooManyEmailCodeRequestsException e) {
        return status(HttpStatus.TOO_MANY_REQUESTS, e.getErrorCode().toResponseDto(HttpStatus.TOO_MANY_REQUESTS.value(), e.getResult()));
    }

    @ExceptionHandler(ExpiredEmailCodeException.class)
    private ResponseEntity<ResponseDto<Map<String, Object>>> handleExpiredCode(ExpiredEmailCodeException e) {
        return status(HttpStatus.GONE, e.getErrorCode().toResponseDto(HttpStatus.GONE.value(), e.getResult()));
    }

    private static <T> ResponseEntity<T> status(HttpStatus status, T body) {
        return ResponseEntity.status(status.value()).body(body);
    }
}
