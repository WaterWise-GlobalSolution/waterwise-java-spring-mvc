package com.waterwise.config;

import com.waterwise.model.ProdutorRural;
import com.waterwise.repository.ProdutorRuralRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private ProdutorRuralRepository produtorRuralRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");

        System.out.println("🔐 Login OAuth2 realizado com sucesso: " + email);

        Optional<ProdutorRural> optionalProdutor = produtorRuralRepository.findByEmail(email);

        if (optionalProdutor.isPresent()) {
            ProdutorRural produtor = optionalProdutor.get();

            // Verificar se o perfil está completo
            if (isProfileComplete(produtor)) {
                System.out.println("✅ Perfil completo - Redirecionando para dashboard");
                response.sendRedirect("/admin/dashboard");
            } else {
                System.out.println("⚠️ Perfil incompleto - Redirecionando para completar perfil");
                response.sendRedirect("/complete-profile");
            }
        } else {
            // Usuário não encontrado (não deveria acontecer com o CustomOAuth2UserService)
            System.out.println("❌ Usuário não encontrado - Redirecionando para login");
            response.sendRedirect("/login?error=user-not-found");
        }
    }

    private boolean isProfileComplete(ProdutorRural produtor) {
        return produtor.getCpfCnpj() != null && !produtor.getCpfCnpj().trim().isEmpty() &&
                produtor.getTelefone() != null && !produtor.getTelefone().trim().isEmpty();
    }
}