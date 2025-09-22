package com.example.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResponseDTO<T> {
    private String status;
    private String message;
    private T data;

    public ResponseDTO(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
    
    public ResponseDTO() {}

    public static <T> ResponseDTO<T> success(T data) {
        return new ResponseDTO<>("success", null, data);
    }

    public static <T> ResponseDTO<T> error(String message) {
        return new ResponseDTO<>("error", message, null);
    }


}
