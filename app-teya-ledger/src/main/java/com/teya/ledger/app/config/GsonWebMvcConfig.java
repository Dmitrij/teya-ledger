package com.teya.ledger.app.config;


import com.google.gson.Gson;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverters;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GsonWebMvcConfig implements WebMvcConfigurer {

    private final Gson gson;

    // Внедряем наш кастомный Gson, созданный на Шаге 1
    public GsonWebMvcConfig(Gson gson) {
        this.gson = gson;
    }

    /**
     * СОВРЕМЕННЫЙ ВАРИАНТ (Spring 7+ / Spring Boot 4+)
     * Вместо List используем строго типизированный ServerBuilder
     */
    @Override
    public void configureMessageConverters(HttpMessageConverters.ServerBuilder builder) {
        // Создаем конвертер и передаем ему наш бин Gson
        GsonHttpMessageConverter gsonConverter = new GsonHttpMessageConverter(gson);

        // Регистрируем кастомный конвертер на самый первый индекс (High Priority)
        builder.addCustomConverter(gsonConverter);
    }

}
