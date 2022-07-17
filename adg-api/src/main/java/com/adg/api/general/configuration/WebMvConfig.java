package com.adg.api.general.configuration;

import com.adg.api.general.http.HttpStatsInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.17 14:50
 */
@Configuration
public class WebMvConfig implements WebMvcConfigurer {

    @Autowired
    private HttpStatsInterceptor httpStatsInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.httpStatsInterceptor);
    }
}
