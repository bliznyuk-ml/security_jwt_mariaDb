package com.example.security_jwt.exceptions;

public class DuplicateEmailException extends RuntimeException{
    public DuplicateEmailException(String message){
        super(message);
    }
}
