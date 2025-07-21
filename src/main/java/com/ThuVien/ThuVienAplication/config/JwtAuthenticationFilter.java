package com.ThuVien.ThuVienAplication.config;

import com.ThuVien.ThuVienAplication.model.dto.response.staffRp.CurrentUserDto;
import com.ThuVien.ThuVienAplication.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component // <<-- Annotation này rất quan trọng để Spring nhận diện đây là một Bean
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        try {
            username = jwtUtil.extractUsername(jwt);
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = new User(username, "", Collections.emptyList());

//            if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
//                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                        userDetails,
//                        null,
//                        userDetails.getAuthorities()
//                );
//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//            }
            // Thay vì tạo UserDetails đơn giản, ta sẽ tạo CurrentUserDto từ token
            try {
                if (jwtUtil.validateToken(jwt, username)) {
                    // Giải mã toàn bộ claims từ token
                    Claims claims = jwtUtil.extractAllClaims(jwt);

                    // Tạo đối tượng CurrentUserDto từ claims
                    CurrentUserDto currentUser = new CurrentUserDto();
                    currentUser.setUsername(claims.getSubject());
                    currentUser.setMaNV((Integer) claims.get("maNV"));
                    currentUser.setHoTenDayDu((String) claims.get("hoTen"));
                    currentUser.setPassword((String) claims.get("password"));
                    // Lấy các claims khác nếu có...

                    // Tạo một đối tượng Authentication mới, với Principal là CurrentUserDto
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            currentUser, // <<-- ĐẶT CẢ OBJECT VÀO ĐÂY
                            null,
                            Collections.emptyList() // Không dùng role nên để rỗng
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                // Token không hợp lệ, bỏ qua để filter chain tiếp tục và trả về lỗi 401/403
            }
        }
        filterChain.doFilter(request, response);
    }
}