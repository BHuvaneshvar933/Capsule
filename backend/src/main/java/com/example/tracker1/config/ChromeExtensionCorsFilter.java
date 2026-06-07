package com.example.tracker1.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Spring's built-in CORS validation can reject non-HTTP origins (e.g. chrome-extension://<id>).
 * For the extension build, allow the extension origin explicitly.
 */
public class ChromeExtensionCorsFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String origin = request.getHeader("Origin");
        if (origin != null && origin.startsWith("chrome-extension://")) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Vary", "Origin");
            response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");

            String reqHeaders = request.getHeader("Access-Control-Request-Headers");
            if (reqHeaders != null && !reqHeaders.isBlank()) {
                response.setHeader("Access-Control-Allow-Headers", reqHeaders);
                response.setHeader("Vary", "Origin, Access-Control-Request-Headers");
            } else {
                response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
            }

            response.setHeader("Access-Control-Expose-Headers", "Authorization");
            response.setHeader("Access-Control-Max-Age", "3600");

            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                response.setStatus(HttpServletResponse.SC_OK);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
