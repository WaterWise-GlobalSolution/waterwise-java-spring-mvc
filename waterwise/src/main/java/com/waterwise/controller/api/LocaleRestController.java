package com.waterwise.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * REST Controller para mudança de idioma via AJAX
 * Permite mudanças dinâmicas sem recarregar a página
 */
@RestController
@RequestMapping("/api/locale")
public class LocaleRestController {

    private final LocaleResolver localeResolver;

    public LocaleRestController(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    /**
     * Endpoint para mudança de idioma via AJAX
     * Uso: POST /api/locale/change com body: {"lang": "en"}
     */
    @PostMapping("/change")
    public ResponseEntity<Map<String, Object>> changeLocale(
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest,
            HttpServletResponse response) {

        String lang = request.get("lang");
        Map<String, Object> result = new HashMap<>();

        try {
            Locale locale;

            // Determinar o locale
            switch (lang.toLowerCase()) {
                case "en":
                    locale = Locale.ENGLISH;
                    break;
                case "pt":
                case "pt-br":
                case "pt_br":
                default:
                    locale = Locale.forLanguageTag("pt-BR");
                    break;
            }

            // Aplicar mudança
            localeResolver.setLocale(httpRequest, response, locale);

            result.put("success", true);
            result.put("message", "Idioma alterado para: " + locale.getDisplayName());
            result.put("locale", locale.toString());
            result.put("language", locale.getLanguage());
            result.put("country", locale.getCountry());

            System.out.println("🌐 [API] Idioma alterado para: " + locale.getDisplayName());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Erro ao alterar idioma: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * Endpoint para obter o idioma atual
     * Uso: GET /api/locale/current
     */
    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentLocale(HttpServletRequest request) {
        Locale currentLocale = localeResolver.resolveLocale(request);

        Map<String, Object> result = new HashMap<>();
        result.put("locale", currentLocale.toString());
        result.put("language", currentLocale.getLanguage());
        result.put("country", currentLocale.getCountry());
        result.put("displayName", currentLocale.getDisplayName());

        return ResponseEntity.ok(result);
    }

    /**
     * Endpoint para listar idiomas suportados
     * Uso: GET /api/locale/supported
     */
    @GetMapping("/supported")
    public ResponseEntity<Map<String, Object>> getSupportedLocales() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> locales = new HashMap<>();

        // Português Brasil
        Map<String, String> ptBR = new HashMap<>();
        ptBR.put("code", "pt_BR");
        ptBR.put("name", "Português (Brasil)");
        ptBR.put("flag", "🇧🇷");
        locales.put("pt", ptBR);

        // English
        Map<String, String> en = new HashMap<>();
        en.put("code", "en");
        en.put("name", "English");
        en.put("flag", "🇺🇸");
        locales.put("en", en);

        result.put("supported", locales);
        result.put("default", "pt_BR");

        return ResponseEntity.ok(result);
    }
}