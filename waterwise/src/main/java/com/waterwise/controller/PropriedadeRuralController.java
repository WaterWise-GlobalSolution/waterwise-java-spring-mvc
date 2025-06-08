package com.waterwise.controller;

import com.waterwise.model.PropriedadeRural;
import com.waterwise.service.PropriedadeRuralService;
import com.waterwise.service.RabbitMQService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Controller
@RequestMapping("/admin/propriedades")
public class PropriedadeRuralController {

    @Autowired
    private PropriedadeRuralService propriedadeService;

    @Autowired
    private RabbitMQService rabbitMQService;

    @GetMapping
    public String listar(Model model,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) Integer nivelDegradacao) {

        if (busca != null && !busca.trim().isEmpty()) {
            model.addAttribute("propriedades", propriedadeService.findByNome(busca));
            model.addAttribute("busca", busca);
        } else {
            model.addAttribute("propriedades", propriedadeService.findAll());
        }

        model.addAttribute("activeMenu", "propriedades");
        // Dados para filtros
        model.addAttribute("niveisDegradacao", propriedadeService.findAllNiveisDegradacao());

        return "propriedades/lista";
    }

    @GetMapping("/novo")
    public String novoForm(Model model) {
        model.addAttribute("activeMenu", "propriedades");
        model.addAttribute("propriedade", new PropriedadeRural());
        model.addAttribute("nivelDegradacao", propriedadeService.findAllNiveisDegradacao());
        model.addAttribute("produtores", propriedadeService.findAllProdutores());
        return "propriedades/form";
    }

    @GetMapping("/{id}")
    public String detalhes(@PathVariable Long id, Model model) {
        PropriedadeRural propriedade = propriedadeService.findByIdWithSensores(id);
        if (propriedade == null) {
            return "redirect:/admin/propriedades?erro=nao-encontrada";
        }

        model.addAttribute("activeMenu", "propriedades");
        model.addAttribute("propriedade", propriedade);

        BigDecimal impactoRetencao = propriedadeService.calcularImpactoRetencaoHidrica(id);
        model.addAttribute("impactoRetencao", impactoRetencao);

        // Calcular famílias protegidas no controller
        int familiasProtegidas = 0;
        if (impactoRetencao != null) {
            familiasProtegidas = impactoRetencao.divide(BigDecimal.valueOf(10000))
                    .setScale(0, RoundingMode.HALF_UP)
                    .intValue();
        }
        model.addAttribute("familiasProtegidas", familiasProtegidas);

        if (propriedade.getNivelDegradacao() != null) {
            BigDecimal capacidadeAbsorcao = propriedade.getNivelDegradacao().getCapacidadeAbsorcaoPercentual();
            model.addAttribute("capacidadeAbsorcao", capacidadeAbsorcao);

            String statusCapacidade;
            int nivel = propriedade.getNivelDegradacao().getNivelNumerico();
            switch (nivel) {
                case 1:
                    statusCapacidade = "Excelente";
                    break;
                case 2:
                    statusCapacidade = "Bom";
                    break;
                case 3:
                    statusCapacidade = "Moderado";
                    break;
                case 4:
                    statusCapacidade = "Necessita Atenção";
                    break;
                case 5:
                    statusCapacidade = "Crítico";
                    break;
                default:
                    statusCapacidade = "Indefinido";
            }
            model.addAttribute("statusCapacidade", statusCapacidade);
        }

        return "propriedades/detalhes";
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model) {
        PropriedadeRural propriedade = propriedadeService.findById(id);
        if (propriedade == null) {
            return "redirect:/admin/propriedades?erro=nao-encontrada";
        }

        model.addAttribute("activeMenu", "propriedades");
        model.addAttribute("propriedade", propriedade);
        model.addAttribute("nivelDegradacao", propriedadeService.findAllNiveisDegradacao());
        model.addAttribute("produtores", propriedadeService.findAllProdutores());
        return "propriedades/form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute PropriedadeRural propriedade,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("nivelDegradacao", propriedadeService.findAllNiveisDegradacao());
            model.addAttribute("produtores", propriedadeService.findAllProdutores());
            return "propriedades/form";
        }

        System.out.println("ID Produtor recebido: " + propriedade.getIdProdutor());
        System.out.println("ID Nível Degradação recebido: " + propriedade.getIdNivelDegradacao());

        try {
            PropriedadeRural salva = propriedadeService.save(propriedade);

            // Enviar notificação via RabbitMQ
            rabbitMQService.enviarNotificacaoPropriedadeCriada(salva);

            redirectAttributes.addFlashAttribute("sucesso",
                    "Propriedade '" + salva.getNomePropriedade() + "' salva com sucesso!");

            return "redirect:/admin/propriedades/" + salva.getIdPropriedade();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro",
                    "Erro ao salvar propriedade: " + e.getMessage());
            return "redirect:/admin/propriedades/novo";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            PropriedadeRural propriedade = propriedadeService.findByIdWithSensores(id);

            if (propriedade == null) {
                redirectAttributes.addFlashAttribute("erro", "Propriedade não encontrada!");
                return "redirect:/admin/propriedades";
            }

            String nomePropriedade = propriedade.getNomePropriedade();
            int totalSensores = propriedade.getSensores() != null ? propriedade.getSensores().size() : 0;

            // Excluir a propriedade (e sensores em cascata através do service)
            propriedadeService.excluirPropriedadeComSensores(id);

            // Mensagem de sucesso personalizada
            String mensagem = "Propriedade '" + nomePropriedade + "' excluída com sucesso!";
            if (totalSensores > 0) {
                mensagem += " (" + totalSensores + " sensor(es) IoT também foram removidos)";
            }

            redirectAttributes.addFlashAttribute("sucesso", mensagem);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro",
                    "Erro ao excluir propriedade: " + e.getMessage());
        }

        return "redirect:/admin/propriedades";
    }

    @GetMapping("/regiao/mairipora")
    public String propriedadesMairipora(Model model) {
        model.addAttribute("propriedades", propriedadeService.findByRegiaoMairipora());
        model.addAttribute("titulo", "Propriedades - Região de Mairiporã");
        return "propriedades/lista";
    }

    // NOVO: Endpoint para filtrar por nível de degradação
    @GetMapping("/nivel/{nivel}")
    public String propriedadesPorNivel(@PathVariable Integer nivel, Model model) {
        var propriedades = propriedadeService.findAll().stream()
                .filter(p -> p.getNivelDegradacao() != null && p.getNivelDegradacao().getNivelNumerico().equals(nivel))
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("propriedades", propriedades);
        model.addAttribute("titulo", "Propriedades - Nível de Degradação " + nivel);
        model.addAttribute("niveisDegrad acao", propriedadeService.findAllNiveisDegradacao());

        return "propriedades/lista";
    }
}
