package com.testejava.Service.exceptions;

public class ResourceNotFoundException extends  RuntimeException{

    public ResourceNotFoundException(String msg){
        super(msg);
    }
}
