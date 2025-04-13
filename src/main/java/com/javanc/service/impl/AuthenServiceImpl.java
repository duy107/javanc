package com.javanc.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.javanc.controlleradvice.customeException.AppException;
import com.javanc.controlleradvice.customeException.UserNotExistsException;
import com.javanc.enums.ErrorCode;
import com.javanc.model.request.AuthenRequest;
import com.javanc.model.request.auth.RegisterRequest;
import com.javanc.model.response.AuthenResponse;
import com.javanc.repository.*;
import com.javanc.repository.entity.*;
import com.javanc.service.AuthenService;
import com.javanc.service.EmailService;
import com.javanc.service.RedisService;
import com.javanc.service.UploadImageFileService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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


    @NonFinal
    @Value("${jwt.expiry_time_token}")
    private Long EXPIRY_TIME_TOKEN;

    @NonFinal
    @Value("${jwt.expiry_time_refreshToken}")
    private Long EXPIRY_TIME_REFRESH_TOKEN;


    UserRepository userRepository;
    RoleRepository roleRepository;
    AddressRepository addressRepository;
    UserAddressRepository userAddressRepository;

    PasswordEncoder passwordEncoder;
    InvalidatedTokenRepository invalidatedTokenRepository;
    UploadImageFileService uploadImageFileService;
    RedisService redisService;
    EmailService emailService;

    private String generateToken(UserEntity user) {
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
                        Instant.now().plus(EXPIRY_TIME_TOKEN, ChronoUnit.SECONDS).toEpochMilli()
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
        UserEntity user = userRepository.findByEmail(authenRequest.getEmail()).orElseThrow(
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
    public void sendEmail(MultipartFile avatar, RegisterRequest registerRequest) throws IOException, MessagingException {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
//        String src = uploadImageFileService.uploadImage(avatar);
//        registerRequest.setSrc(src);
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
        redisService.savePendingUser(registerRequest.getEmail(), otp, registerRequest);
        emailService.sendSimpleEmail(registerRequest.getEmail(), otp);
    }

    @Override
    public AuthenResponse introspect(AuthenRequest authenRequest) throws JOSEException, ParseException {
//        String token = authenRequest.getToken();
//
//        JWSVerifier verifier = new MACVerifier(SIGN_KEY.getBytes());
//        SignedJWT signedJWT = SignedJWT.parse(token);
//        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
//        boolean verified = signedJWT.verify(verifier);
//        if (!(verified && expiryTime.after(new Date()))) {
//            throw new AppException(ErrorCode.UNAUTHENTICATED);
//        }
//        return AuthenResponse.builder()
//                .valid(true)
//                .build();
        var token = authenRequest.getToken();
        boolean isValid = true;
        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }
        return AuthenResponse.builder()
                .valid(isValid)
                .token(token)
                .build();
    }

    @Override
    public AuthenResponse refreshToken(AuthenRequest authenRequest) throws ParseException, JOSEException {
        // Authentication token
        SignedJWT singedJWT = verifyToken(authenRequest.getToken(), true);
        // get id and expiry time to create InvalidToken
        String jti = singedJWT.getJWTClaimsSet().getJWTID();
        Date expirationTime = singedJWT.getJWTClaimsSet().getExpirationTime();
        InvalidatedTokenEntity invalidatedTokenEntity = InvalidatedTokenEntity.builder()
                .id(jti)
                .expiryTime(expirationTime)
                .build();
        invalidatedTokenRepository.save(invalidatedTokenEntity);

        // create new token
        String email = singedJWT.getJWTClaimsSet().getSubject();
        UserEntity user = userRepository.findByEmail(email).orElseThrow(()
                -> new AppException(ErrorCode.USER_NOT_EXISTS));
        String token = generateToken(user);
        return AuthenResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public void register(AuthenRequest authenRequest) throws JsonProcessingException {
        if (userRepository.existsByEmail(authenRequest.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        RegisterRequest registerRequest = redisService.getPendingUser(authenRequest.getEmail(), authenRequest.getOtp());
        if(registerRequest == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
        RoleEntity role = roleRepository.findByCode("USER").orElseThrow(
                () -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)
        );

        UserEntity newUser = UserEntity.builder()
                .email(registerRequest.getEmail())
                .name(registerRequest.getName())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .roles(List.of(role))
                .avatar(registerRequest.getSrc())
                .phone(registerRequest.getPhone())
                .build();

        AddressEntity newAddress = AddressEntity.builder()
                .cityId(registerRequest.getCityId())
                .districtId(registerRequest.getDistrictId())
                .wardId(registerRequest.getWardId())
                .detail(registerRequest.getDetail())
                .build();

        userRepository.save(newUser);
        addressRepository.save(newAddress);

        UserAddressEntity userAddress = UserAddressEntity.builder()
                .user(newUser)
                .address(newAddress)
                .isDefault(true)
                .build();
        userAddressRepository.save(userAddress);
    }


    // kiem tra tinh hop le cua token (sai / het han)
    // SignedJWT: đại diện cho 1 token đã được ký
    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {

        // xác thực token với thuận toán HMAC (SIGN_KEY.getBytes(): khóa dùng để xác minh chữ ký của JWT)
        JWSVerifier verifier = new MACVerifier(SIGN_KEY.getBytes());

        // chuyển chuỗi JWT thành đối tượng SingedJWT để trích xuất thông tin
        SignedJWT signedJWT = SignedJWT.parse(token);
        // lấy thời gian hết hạn từ token
        Date expiryTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                .toInstant().plus(EXPIRY_TIME_REFRESH_TOKEN, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        // xác thực token với thuật toán HMAC
        boolean verified = signedJWT.verify(verifier);

        // token sai hoac het han
        if (!(verified && expiryTime.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }


    private String buildScope(UserEntity user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!user.getRoles().isEmpty()) {
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getCode());
                if (!role.getPermissions().isEmpty()) {
                    role.getPermissions().forEach(permission -> {
                        stringJoiner.add((permission.getName()));
                    });
                }
            });
        }
        return stringJoiner.toString();
    }
}
