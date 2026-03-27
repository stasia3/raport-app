package com.raport.app.config;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();

            if ("ROLE_Dispatcher".equals(role)) {
                response.sendRedirect("/dispatcher/posts");
                return;
            }

            if ("ROLE_GlobalAdmin".equals(role)) {
                response.sendRedirect("/admin");
                return;
            }
            if ("ROLE_Institution".equals(role)) {
                response.sendRedirect("/institution/dashboard");
                return;
            }

            if ("ROLE_Citizen".equals(role)) {
                response.sendRedirect("/feed");
                return;
            }
        }

        response.sendRedirect("/feed");
    }
}