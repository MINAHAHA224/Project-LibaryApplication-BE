//package com.ThuVien.ThuVienAplication.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//     private final JwtAuthenticationFilter jwtAuthFilter; // Sẽ tạo filter này ở bước sau
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable()) // Tắt CSRF vì dùng token
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Cấu hình session stateless
//
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/auth/**" , "/dausach/**"
//                                , "/api/book-titles/**" ,"/api/book-titles"
//                                , "/api/readers" , "/api/readers/**"
//                                , "/api/staffs" , "/api/staffs/**"
//                                , "/api/backups" , "/api/backups/**"
//                                , "/api/rentals" , "/api/rentals/**"
//                                , "/api/returns" , "/api/returns/**"
//                                , "/api/book-types" ,"/api/book-types/**"
//                                , "/api/accounts" ,"/api/accounts/**" ).permitAll() // Cho phép tất cả truy cập endpoint login
//                        .anyRequest().authenticated() // Tất cả các request khác đều phải được xác thực
//                )
//         .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Thêm filter JWT
//
//        return http.build();
//    }
//}


package com.ThuVien.ThuVienAplication.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter; // Đảm bảo đã inject

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // Chỉ cho phép các API trong /api/auth được truy cập công khai
                        .requestMatchers("/api/auth/**"
                                ,"/dausach/**"
                                , "/actuator/restart").permitAll()
                        // === API CÔNG KHAI CHO ĐỘC GIẢ ===
                        .requestMatchers(HttpMethod.GET, "/api/public/book-titles").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/public/book-titles/**").permitAll()

                        // Bất kỳ request nào khác đều phải được xác thực
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Luôn để session stateless
                // Thêm bộ lọc JWT để kiểm tra token cho mỗi request
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}