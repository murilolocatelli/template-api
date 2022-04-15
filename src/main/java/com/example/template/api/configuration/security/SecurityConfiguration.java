package com.example.template.api.configuration.security;

import com.example.commons.api.handler.AuthenticationHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationHandler authenticationHandler;

    private static final String[] PERMITED_MATCHERS =
        {"/v2/api-docs", "/swagger-resources/**", "/swagger-ui/**", "/actuator/health/**"};

    protected void configure(HttpSecurity http) throws Exception {

        http
            .csrf().disable() //TODO: disable crsf for POST, DELETE, PUT work. But it cause security bug in Sonarqube.
            .authorizeRequests()
            .antMatchers(PERMITED_MATCHERS).permitAll()
            .anyRequest().authenticated()
            .and().httpBasic()
            .authenticationEntryPoint(this.authenticationHandler);
    }

    /*@Configuration
    @Order(2)
    public static class ApiTokenSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        @Autowired
        private AuthenticationHandler customAuthenticationEntryPoint;

        @Value("${jwt.secretKey}")
        private String secretKey;

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http
                .exceptionHandling()
                .authenticationEntryPoint(this.customAuthenticationEntryPoint)
                .and()
                .authorizeRequests()
                .antMatchers("/**")
                .authenticated()
                .and()
                .addFilter(new JwtAuthenticationFilter(authenticationManager(), secretKey))
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }
    }*/

}
