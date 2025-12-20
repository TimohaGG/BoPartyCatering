package com.boparty.bopartycatering.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfiguration {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
        http
                .csrf(x->x.disable())
                .authorizeHttpRequests(auth -> {

                    auth
                            .requestMatchers("/login").permitAll()
                            .requestMatchers("/asserts/**").permitAll()
                            .requestMatchers("/oauth2/authorize/google").permitAll()
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
                                .deleteCookies("JSESSIONID","remember-me")
                                .permitAll())
                .rememberMe(rem->
                        rem.userDetailsService(userDetailsService)
                        .key("uniqueAndSecret")
                                .tokenValiditySeconds(604800));

        return http.build();

    }
}
