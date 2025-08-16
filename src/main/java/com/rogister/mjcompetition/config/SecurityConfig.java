package com.rogister.mjcompetition.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // 公开接口 - 无需认证
                .requestMatchers("/api/player/register").permitAll()      // 玩家注册
                .requestMatchers("/api/player/login").permitAll()         // 玩家登录
                .requestMatchers("/api/admin/login").permitAll()          // 管理员登录
                .requestMatchers("/api/competitions/list").permitAll()    // 比赛列表查看
                .requestMatchers("/api/competition-status/**").permitAll() // 比赛状态查询（无需鉴权）
                
                // 玩家权限 - 需要PLAYER角色
                .requestMatchers("/api/player/**").hasAnyAuthority("PLAYER")
                .requestMatchers("/api/teams/**").hasAnyAuthority("PLAYER")
                .requestMatchers("/api/player-competition-registrations/**").hasAnyAuthority("PLAYER")
                
                // 管理员权限 - 需要ADMIN角色
                .requestMatchers("/api/admin/**").hasAnyAuthority("ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/competition-rules/**").hasAnyAuthority("ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/competitions/create").hasAnyAuthority("ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/competitions/*/update").hasAnyAuthority("ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/competitions/*/delete").hasAnyAuthority("ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/match-results/**").hasAnyAuthority("ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/advancement/**").hasAnyAuthority("ADMIN", "SUPER_ADMIN")
                
                // 其他请求需要认证
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
