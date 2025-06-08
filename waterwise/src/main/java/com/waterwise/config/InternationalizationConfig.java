package com.waterwise.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
public class InternationalizationConfig implements WebMvcConfigurer {

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        // Português brasileiro como padrão (contexto do projeto em Mairiporã)
        slr.setDefaultLocale(Locale.forLanguageTag("pt-BR"));
        return slr;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang"); // Parâmetro que será usado para mudar o idioma
        return lci;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // ✅ IMPORTANTE: Registrar o interceptor para funcionar
        registry.addInterceptor(localeChangeInterceptor());
    }
}