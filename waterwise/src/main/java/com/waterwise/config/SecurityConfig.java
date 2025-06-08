package com.waterwise.config;

import com.waterwise.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final CustomOAuth2UserService customOAuth2UserService;
        private final OAuth2SuccessHandler oauth2SuccessHandler;

        public SecurityConfig(CustomOAuth2UserService customOAuth2UserService,
                        OAuth2SuccessHandler oauth2SuccessHandler) {
                this.customOAuth2UserService = customOAuth2UserService;
                this.oauth2SuccessHandler = oauth2SuccessHandler;
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(authz -> authz
                                                // Páginas públicas - ADICIONAR os novos endpoints de logout
                                                .requestMatchers("/", "/login", "/logout", "/logout-complete",
                                                                "/logout-simple", "/logout-select-account",
                                                                "/force-logout", "/logout-status",
                                                                "/error", "/css/**", "/js/**", "/images/**",
                                                                "/assets/**")
                                                .permitAll()

                                                // ✅ IMPORTANTE: Permitir acesso aos endpoints de internacionalização
                                                .requestMatchers("/locale/**", "/api/locale/**").permitAll()

                                                // Página de completar perfil
                                                .requestMatchers("/complete-profile").authenticated()

                                                // Páginas administrativas
                                                .requestMatchers("/admin/**").authenticated()

                                                // Outras páginas
                                                .anyRequest().permitAll())
                                .oauth2Login(oauth -> oauth
                                                .loginPage("/login")
                                                .successHandler(oauth2SuccessHandler)
                                                .failureUrl("/login?error=oauth2-failed")
                                                .userInfoEndpoint(userInfo -> userInfo
                                                                .userService(customOAuth2UserService)))
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout=success")
                                                .invalidateHttpSession(true)
                                                .clearAuthentication(true)
                                                .deleteCookies("JSESSIONID", "OAUTH2_AUTHORIZATION_REQUEST",
                                                                "OAUTH2_AUTHORIZATION_REQUEST_STATE")
                                                .permitAll())
                                .csrf(csrf -> csrf.disable());

                return http.build();
        }
}