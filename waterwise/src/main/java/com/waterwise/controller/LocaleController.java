package com.waterwise.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * Controller para gerenciar mudanças de idioma/locale
 * Permite alternar entre PT-BR e EN via requisições AJAX ou form
 */
@Controller
@RequestMapping("/locale")
public class LocaleController {

    private final LocaleResolver localeResolver;

    public LocaleController(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    /**
     * Endpoint para mudança de idioma via POST
     * Uso: POST /locale/change?lang=en ou POST /locale/change?lang=pt
     */
    @PostMapping("/change")
    public String changeLocale(@RequestParam("lang") String lang,
            HttpServletRequest request,
            HttpServletResponse response) {

        Locale locale;

        // Determinar o locale baseado no parâmetro
        switch (lang.toLowerCase()) {
            case "en":
                locale = Locale.ENGLISH;
                break;
            case "pt":
            case "pt-br":
            case "pt_br":
            default:
                locale = new Locale("pt", "BR");
                break;
        }

        // Definir o novo locale
        localeResolver.setLocale(request, response, locale);

        // Log da mudança
        System.out.println("🌐 Idioma alterado para: " + locale.getDisplayName());

        // Redirecionar para a página anterior ou dashboard
        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains(request.getServerName())) {
            return "redirect:" + referer;
        }

        return "redirect:/admin/dashboard";
    }
}