package com.waterwise.controller;

import com.waterwise.dto.CompleteProfileDTO;
import com.waterwise.model.ProdutorRural;
import com.waterwise.repository.ProdutorRuralRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/complete-profile")
public class FirstAccessController {

    @Autowired
    private ProdutorRuralRepository produtorRuralRepository;

    @GetMapping
    public String showCompleteProfileForm(@AuthenticationPrincipal OAuth2User oauth2User, Model model) {
        // Verificar se o usuário está autenticado
        if (oauth2User == null) {
            return "redirect:/login?error=not-authenticated";
        }

        String email = oauth2User.getAttribute("email");
        String userName = oauth2User.getAttribute("name");

        System.out.println("📝 Acessando formulário de completar perfil: " + email);

        Optional<ProdutorRural> optionalProdutor = produtorRuralRepository.findByEmail(email);

        if (optionalProdutor.isEmpty()) {
            System.out.println("❌ Produtor não encontrado no banco: " + email);
            return "redirect:/login?error=user-not-found";
        }

        ProdutorRural produtor = optionalProdutor.get();

        // Se o perfil já está completo, redirecionar para dashboard
        if (isProfileComplete(produtor)) {
            System.out.println("✅ Perfil já completo - Redirecionando para dashboard");
            return "redirect:/admin/dashboard";
        }

        // Criar DTO com dados existentes (se houver)
        CompleteProfileDTO dto = new CompleteProfileDTO();
        dto.setNomeCompleto(produtor.getNomeCompleto());
        dto.setCpfCnpj(produtor.getCpfCnpj());
        dto.setTelefone(produtor.getTelefone());

        // Preparar modelo para o formulário
        model.addAttribute("produtor", dto); // Usando DTO ao invés do modelo
        model.addAttribute("userEmail", email);
        model.addAttribute("userName", userName != null ? userName : "Usuário");

        System.out.println("📄 Exibindo formulário de completar perfil");
        return "auth/complete-profile";
    }

    @PostMapping
    public String completeProfile(@Valid @ModelAttribute("produtor") CompleteProfileDTO dto,
                                  BindingResult result,
                                  @AuthenticationPrincipal OAuth2User oauth2User,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        if (oauth2User == null) {
            return "redirect:/login?error=session-expired";
        }

        String email = oauth2User.getAttribute("email");
        String userName = oauth2User.getAttribute("name");

        System.out.println("💾 Processando completar perfil: " + email);
        System.out.println("📝 Dados recebidos: " + dto.toString());

        Optional<ProdutorRural> optionalExistingProdutor = produtorRuralRepository.findByEmail(email);

        if (optionalExistingProdutor.isEmpty()) {
            return "redirect:/login?error=user-not-found";
        }

        // Se há erros de validação, retornar ao formulário
        if (result.hasErrors()) {
            System.out.println("❌ Erros de validação encontrados:");
            result.getAllErrors().forEach(error ->
                    System.out.println("   - " + error.getDefaultMessage())
            );

            model.addAttribute("produtor", dto);
            model.addAttribute("userEmail", email);
            model.addAttribute("userName", userName != null ? userName : "Usuário");
            return "auth/complete-profile";
        }

        // Validar CPF/CNPJ único (excluindo o próprio usuário)
        Optional<ProdutorRural> produtorComMesmoCpf = produtorRuralRepository.findByCpfCnpj(dto.getCpfCnpj());
        if (produtorComMesmoCpf.isPresent() &&
                !produtorComMesmoCpf.get().getIdProdutor().equals(optionalExistingProdutor.get().getIdProdutor())) {

            result.rejectValue("cpfCnpj", "error.cpfCnpj", "CPF/CNPJ já está em uso por outro usuário");
            model.addAttribute("produtor", dto);
            model.addAttribute("userEmail", email);
            model.addAttribute("userName", userName != null ? userName : "Usuário");
            return "auth/complete-profile";
        }

        try {
            // Atualizar dados do produtor existente
            ProdutorRural existingProdutor = optionalExistingProdutor.get();
            existingProdutor.setNomeCompleto(dto.getNomeCompleto());
            existingProdutor.setCpfCnpj(dto.getCpfCnpj());
            existingProdutor.setTelefone(dto.getTelefone());

            ProdutorRural savedProdutor = produtorRuralRepository.save(existingProdutor);

            System.out.println("✅ Perfil completado com sucesso para: " + email);
            System.out.println("📋 Dados salvos: Nome=" + savedProdutor.getNomeCompleto() +
                    ", CPF/CNPJ=" + savedProdutor.getCpfCnpj() +
                    ", Telefone=" + savedProdutor.getTelefone());

            redirectAttributes.addFlashAttribute("sucesso",
                    "Perfil completado com sucesso! Bem-vindo ao WaterWise, " + existingProdutor.getNomeCompleto() + "!");

            return "redirect:/admin/dashboard";

        } catch (Exception e) {
            System.out.println("❌ Erro ao salvar dados: " + e.getMessage());
            e.printStackTrace();

            result.rejectValue("cpfCnpj", "error.general", "Erro ao salvar dados: " + e.getMessage());
            model.addAttribute("produtor", dto);
            model.addAttribute("userEmail", email);
            model.addAttribute("userName", userName != null ? userName : "Usuário");
            return "auth/complete-profile";
        }
    }

    private boolean isProfileComplete(ProdutorRural produtor) {
        return produtor.getCpfCnpj() != null && !produtor.getCpfCnpj().trim().isEmpty() &&
                produtor.getTelefone() != null && !produtor.getTelefone().trim().isEmpty();
    }
}