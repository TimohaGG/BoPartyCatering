package com.boparty.bopartycatering.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfiguration {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(x->x.disable())
                .authorizeHttpRequests(auth -> {

                    auth
                            .requestMatchers("/login","/registration").permitAll()
                            .requestMatchers("/asserts/**").permitAll()
                            .anyRequest().authenticated();
                })

                .formLogin(login -> {

                    login
                            .loginPage("/login")
                            .defaultSuccessUrl("/", true)
                            .permitAll();
                })
                .logout(logout ->
                        logout.logoutUrl("/logout")
                                .logoutSuccessUrl("/login")
                                .invalidateHttpSession(true)
                                .permitAll());

        return http.build();

    }
}
