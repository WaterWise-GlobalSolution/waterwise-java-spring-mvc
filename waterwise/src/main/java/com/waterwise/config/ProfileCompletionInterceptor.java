package com.waterwise.config;

import com.waterwise.model.ProdutorRural;
import com.waterwise.repository.ProdutorRuralRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Component
public class ProfileCompletionInterceptor implements HandlerInterceptor {

    @Autowired
    private ProdutorRuralRepository produtorRuralRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        // Verificar apenas rotas administrativas
        if (!requestURI.startsWith("/admin/")) {
            return true;
        }

        // Verificar se o usuário está autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof OAuth2User)) {
            return true;
        }

        OAuth2User oauth2User = (OAuth2User) auth.getPrincipal();
        String email = oauth2User.getAttribute("email");

        if (email == null) {
            return true;
        }

        // Buscar o produtor no banco
        Optional<ProdutorRural> optionalProdutor = produtorRuralRepository.findByEmail(email);
        if (optionalProdutor.isEmpty()) {
            response.sendRedirect("/login?error=user-not-found");
            return false;
        }

        ProdutorRural produtor = optionalProdutor.get();

        // Verificar se o perfil está completo
        if (!isProfileComplete(produtor)) {
            System.out.println("⚠️ Tentativa de acesso a área administrativa com perfil incompleto: " + email);
            response.sendRedirect("/complete-profile");
            return false;
        }

        return true;
    }

    private boolean isProfileComplete(ProdutorRural produtor) {
        return produtor.getCpfCnpj() != null && !produtor.getCpfCnpj().trim().isEmpty() &&
                produtor.getTelefone() != null && !produtor.getTelefone().trim().isEmpty();
    }
}