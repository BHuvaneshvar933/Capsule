package com.example.tracker1.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<ChromeExtensionCorsFilter> chromeExtensionCorsFilter() {
        FilterRegistrationBean<ChromeExtensionCorsFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new ChromeExtensionCorsFilter());
        bean.addUrlPatterns("/*");
        // Run before Spring Security / CorsFilter.
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}
