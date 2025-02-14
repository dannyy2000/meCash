package org.example.general;

import lombok.*;
import org.example.data.model.enums.Status;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T>{

    private String message;
    private Status status;
    private int statusCode;
    private T data;


    public ApiResponse(String message, Status status){
        this.message = message;
        this.status = status;
    }

    public ApiResponse(String message, Status status, T data) {
        this.message = message;
        this.status = status;
        this.data = data;
    }
//    public ApiResponse(String message, Status status, T data,T info) {
//        this.message = message;
//        this.status = status;
//        this.data = data;
    ////        this.info = info;
//    }


    public ApiResponse(Status status){
        this.status = status;
    }

}
