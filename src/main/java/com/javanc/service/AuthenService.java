package com.javanc.service;

import com.javanc.model.request.AuthenRequest;
import com.javanc.model.response.AuthenResponse;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthenService {
    AuthenResponse login(AuthenRequest authenRequest);
    AuthenResponse register(AuthenRequest authenRequest);
    AuthenResponse introspect(AuthenRequest authenRequest) throws JOSEException, ParseException;
    AuthenResponse refreshToken(AuthenRequest authenRequest) throws ParseException, JOSEException;
}
