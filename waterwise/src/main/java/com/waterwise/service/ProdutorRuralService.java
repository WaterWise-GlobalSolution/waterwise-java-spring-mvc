package com.waterwise.service;

import com.waterwise.model.ProdutorRural;
import com.waterwise.repository.ProdutorRuralRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProdutorRuralService {

    @Autowired
    private ProdutorRuralRepository produtorRuralRepository;

    public Optional<ProdutorRural> autenticar(String email, String senha) {
        Optional<ProdutorRural> produtorOpt = produtorRuralRepository.findByEmail(email);
        if (produtorOpt.isPresent()) {
            ProdutorRural produtor = produtorOpt.get();
            if (produtor.getSenha().equals(senha)) { // compara texto plano
                return Optional.of(produtor);
            }
        }
        return Optional.empty();
    }
}