package com.khpt.projectkim.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@SpringBootApplication
@RestController
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        System.out.println(principal);
        return Collections.singletonMap("name", principal.getAttribute("name"));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .authorizeRequests(a -> a
                        .antMatchers("/", "/error", "/webjars/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .logout(l -> l
                        .logoutSuccessUrl("/").permitAll()
                )
                .csrf(c -> c
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                .oauth2Login()
        ;

        // @formatter:on
    }

}