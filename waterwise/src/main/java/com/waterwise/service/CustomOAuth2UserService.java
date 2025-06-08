package com.waterwise.service;

import com.waterwise.model.ProdutorRural;
import com.waterwise.repository.ProdutorRuralRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private ProdutorRuralRepository produtorRuralRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String nome = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");

        System.out.println("🔐 Login OAuth2: " + email);

        // Buscar ou criar usuário
        ProdutorRural produtor = produtorRuralRepository.findByEmail(email)
                .orElseGet(() -> criarNovoProdutor(email, nome, picture));

        // ✅ ADICIONAR ATRIBUTOS CUSTOMIZADOS
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("produtorId", produtor.getIdProdutor());
        attributes.put("isNewUser", produtor.getDataCadastro().isAfter(LocalDateTime.now().minusMinutes(1)));
        attributes.put("profileComplete", isProfileComplete(produtor));

        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                attributes,
                "email"
        );
    }

    private ProdutorRural criarNovoProdutor(String email, String nome, String picture) {
        System.out.println("👤 Criando novo usuário: " + email);

        ProdutorRural novoProdutor = new ProdutorRural();
        novoProdutor.setEmail(email);
        novoProdutor.setNomeCompleto(nome != null ? nome : "Usuário Google");
        novoProdutor.setCpfCnpj(""); // Será preenchido depois
        novoProdutor.setTelefone(""); // Será preenchido depois
        novoProdutor.setDataCadastro(LocalDateTime.now());

        ProdutorRural saved = produtorRuralRepository.save(novoProdutor);
        System.out.println("✅ Usuário criado com ID: " + saved.getIdProdutor());

        return saved;
    }

    private boolean isProfileComplete(ProdutorRural produtor) {
        return produtor.getCpfCnpj() != null && !produtor.getCpfCnpj().trim().isEmpty() &&
                produtor.getTelefone() != null && !produtor.getTelefone().trim().isEmpty();
    }
}