package group_10.group._0.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import group_10.group._0.dto.request.AuthenticationRequest;
import group_10.group._0.dto.request.IntrospectRequest;
import group_10.group._0.dto.request.LogoutRequest;
import group_10.group._0.dto.request.RefreshRequest;
import group_10.group._0.dto.response.AuthenticationResponse;
import group_10.group._0.dto.response.IntrospectResponse;
import group_10.group._0.entity.Invalidatedtoken;
import group_10.group._0.entity.Users;
import group_10.group._0.exception.AppExceptions;
import group_10.group._0.exception.ErrorCode;
import group_10.group._0.mapper.UsersMapper;
import group_10.group._0.repository.InvalidatedTokenRepository;
import group_10.group._0.repository.UsersRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UsersRepository TKRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long tokenTime;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long refreshTime;

    UsersMapper userMapper;


    public IntrospectResponse introspect(IntrospectRequest request)
            throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;

        try {
            verifyToken(token, false);

        } catch (AppExceptions e) {
            isValid = false;
        }

        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = TKRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppExceptions(ErrorCode.USER_NOT_EXISTED));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getMatKhau());
        if (!authenticated)
            throw new AppExceptions(ErrorCode.UNAUTHENTICATED);


        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .thongTinUser(userMapper.toTaikhoanResponse(user))
                .build();
    }

    private String generateToken(Users taikhoan) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(taikhoan.getEmail())
                .issuer("Social media Group 10")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(tokenTime, ChronoUnit.SECONDS).toEpochMilli())) //Het han sau 1 ngay
                .jwtID(UUID.randomUUID().toString())
//                .claim("role", buildScope(taikhoan))
                .claim("id", taikhoan.getMaNguoiDung())
                .build();


        Payload payload = new Payload(claimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        //Ky token
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Can't create JWS object", e);
            throw new RuntimeException(e);
        }
    }


//    *****  Khuc nay se de role trong token  *****
//    private String buildScope(Users taikhoan) {
//        StringJoiner scopeJoiner = new StringJoiner(" ");
//        if (taikhoan.getIdvaitro() != null && taikhoan.getIdvaitro().getTenvaitro() != null) {
//            scopeJoiner.add(taikhoan.getIdvaitro().getTenvaitro());
//        }
//        return scopeJoiner.toString();
//    }

    private SignedJWT verifyToken(String token, boolean isRefresh)
            throws JOSEException, ParseException {

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expityTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant().plus(refreshTime, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        //Neu chu ky het han hoac khong duoc verified thi cut
        if(!(verified && expityTime.after(new Date())))
            throw new AppExceptions(ErrorCode.UNAUTHENTICATED);

        if(invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppExceptions(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }

    public void logout(LogoutRequest tokenRequest)
            throws ParseException, JOSEException {
        try {
            var signedToken = verifyToken(tokenRequest.getToken(), true);
            String jwtID = signedToken.getJWTClaimsSet().getJWTID();
            Date expityTime = signedToken.getJWTClaimsSet().getExpirationTime();

            Invalidatedtoken invalidatedToken = new Invalidatedtoken().builder()
                    .id(jwtID)
                    .expirationtime(expityTime)
                    .build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppExceptions e) {
            log.info("Token da het han roi: ", e);
        }
    }

    public AuthenticationResponse refreshToken(RefreshRequest requestToken)
            throws ParseException, JOSEException {
        var signJWT = verifyToken(requestToken.getToken(),true);

        var jwtID = signJWT.getJWTClaimsSet().getJWTID();
        var expityTime = signJWT.getJWTClaimsSet().getExpirationTime();

        Invalidatedtoken invalidatedToken = new Invalidatedtoken().builder()
                .id(jwtID)
                .expirationtime(expityTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);

        var email = signJWT.getJWTClaimsSet().getSubject();

        var taiKhoan = TKRepository.findByEmail(email)
                .orElseThrow(() -> new AppExceptions(ErrorCode.UNAUTHENTICATED));

        var token = generateToken(taiKhoan);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }


    /**
     * Hàm dùng để giải mã token và lấy ra maNguoiDung (id)
     */
    public Integer getMaNguoiDungFromToken(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = verifyToken(token, false);

        Integer maNguoiDung = signedJWT.getJWTClaimsSet().getIntegerClaim("id");

        if (maNguoiDung == null) {
            throw new AppExceptions(ErrorCode.UNAUTHENTICATED);
        }

        return maNguoiDung;
    }

}

