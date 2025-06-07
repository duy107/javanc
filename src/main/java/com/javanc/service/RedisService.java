package com.javanc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.javanc.model.request.auth.RegisterRequest;

public interface RedisService {
    void saveOTP(String email, String otp);
    void savePendingUser(String email, String otp, RegisterRequest registerRequest) throws JsonProcessingException;
    RegisterRequest getPendingUser(String email, String otp) throws JsonProcessingException;
    String getOTP(String email);
    void deleteOTP(String email);
}
