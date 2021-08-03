package com.mealchak.mealchakserverapplication.handler;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class MultipartUploadExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleFileUploadException(MaxUploadSizeExceededException exception,
                                            HttpServletRequest httpServletRequest,
                                            HttpServletResponse httpServletResponse){
        return "파일 업로드는 최대 20MB까지 가능합니다.";
    }
}
