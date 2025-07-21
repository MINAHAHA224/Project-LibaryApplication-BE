package com.ThuVien.ThuVienAplication.utils;

import com.ThuVien.ThuVienAplication.model.dto.response.staffRp.CurrentUserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Tạo một secret key đủ mạnh. Key này phải được giữ bí mật.
    // Trong thực tế, nên lấy từ file cấu hình.
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Thời gian hết hạn của token (ví dụ: 1 ngày)
    private final long expiration = 86400000; // 24 * 60 * 60 * 1000

    public String generateToken(CurrentUserDto user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("maNV", user.getMaNV());
        claims.put("hoTen", user.getHoTenDayDu());
        claims.put("password", user.getPassword());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}