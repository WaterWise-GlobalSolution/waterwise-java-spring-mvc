package com.waterwise.controller;

import com.waterwise.service.ProdutorRuralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @Autowired
    private ProdutorRuralService produtorRuralService;

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String logout,
            @RequestParam(required = false) String error,
            org.springframework.ui.Model model) {

        // Tratar mensagens de logout
        if ("success".equals(logout)) {
            model.addAttribute("logoutMessage", "Logout realizado com sucesso!");
        } else if ("forced".equals(logout)) {
            model.addAttribute("logoutMessage", "Logout forçado concluído. Todos os dados foram limpos.");
        }

        // Tratar erros de logout
        if ("logout-failed".equals(error)) {
            model.addAttribute("errorMessage", "Erro durante logout. Tente novamente.");
        } else if ("force-logout-failed".equals(error)) {
            model.addAttribute("errorMessage", "Erro no logout forçado.");
        }

        return "login";
    }
}