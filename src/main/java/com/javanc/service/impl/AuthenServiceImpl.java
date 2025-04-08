package com.javanc.service.impl;

import ch.qos.logback.classic.spi.IThrowableProxy;
import com.javanc.controlleradvice.customeException.AppException;
import com.javanc.controlleradvice.customeException.UserNotExistsException;
import com.javanc.enums.ErrorCode;
import com.javanc.model.request.AuthenRequest;
import com.javanc.model.response.AuthenResponse;
import com.javanc.repository.RoleRepository;
import com.javanc.repository.UserRepository;
import com.javanc.repository.entity.RoleEntity;
import com.javanc.repository.entity.UserEntity;
import com.javanc.service.AuthenService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
@PropertySource("classpath:application.yml")
@Slf4j
public class AuthenServiceImpl implements AuthenService {


    @NonFinal
    @Value("${jwt.sign_key}")
    String SIGN_KEY;

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;

    private String generateToken(UserEntity user){
        // thuật toán mã hóa
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        // data trong body
            JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                    // user đăng nhập
                    .subject(user.getEmail())
                    .issuer("duytrinh.com")
                    // thời gian ký
                    .issueTime(new Date())
                    // thời gian hết hạn
                    .expirationTime(new Date(
                            Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                    ))
                    .claim("scope", buildScope(user))
                    // Lưu để lấy thông tin token
                    .jwtID(UUID.randomUUID().toString())
                    .build();

            Payload payload = new Payload(jwtClaimsSet.toJSONObject());

            JWSObject jwsObject = new JWSObject(header, payload);
            try {
                jwsObject.sign(new MACSigner(SIGN_KEY.getBytes()));
                return jwsObject.serialize();
            } catch (Exception e) {
                log.error("Cannot generate token", e);
                throw new RuntimeException(e);
            }
    }

    @Override
    public AuthenResponse login(AuthenRequest authenRequest) {
        UserEntity user =  userRepository.findByEmail(authenRequest.getEmail()).orElseThrow(
                () -> new UserNotExistsException("User not found")
        );
        boolean authenticated = passwordEncoder.matches(authenRequest.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String token = generateToken(user);

        return AuthenResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public AuthenResponse register(AuthenRequest authenRequest) {

        if (userRepository.existsByEmail(authenRequest.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        RoleEntity role = roleRepository.findByCode("USER")
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
        String password = passwordEncoder.encode(authenRequest.getPassword());
        UserEntity user = UserEntity.builder()
                .email(authenRequest.getEmail())
                .password(password)
                .roles(List.of(role))
                .build();
        userRepository.save(user);
        return AuthenResponse.builder()
                .email(user.getEmail())
                .build();
    }

    @Override
    public AuthenResponse introspect(AuthenRequest authenRequest) throws JOSEException, ParseException {
        String token = authenRequest.getToken();

        JWSVerifier verifier = new MACVerifier(SIGN_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        boolean verified = signedJWT.verify(verifier);
        if (!(verified && expiryTime.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return AuthenResponse.builder()
                .valid(true)
                .build();
    }

    private String buildScope(UserEntity user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if(!user.getRoles().isEmpty()){
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getCode());
                if(!role.getPermissions().isEmpty()){
                    role.getPermissions().forEach(permission -> {
                        stringJoiner.add((permission.getName()));
                    });
                }
            });
        }
        return stringJoiner.toString();
    }
}
