package com.javanc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.javanc.model.request.AuthenRequest;
import com.javanc.model.request.auth.RegisterRequest;
import com.javanc.model.response.AuthenResponse;
import com.nimbusds.jose.JOSEException;
import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;

public interface AuthenService {
    AuthenResponse login(AuthenRequest authenRequest);
    void sendEmail(MultipartFile avatar, RegisterRequest registerRequest) throws IOException, MessagingException;
    AuthenResponse introspect(AuthenRequest authenRequest) throws JOSEException, ParseException;
    AuthenResponse refreshToken(AuthenRequest authenRequest) throws ParseException, JOSEException;
    void register(AuthenRequest authenRequest) throws JsonProcessingException;
}
