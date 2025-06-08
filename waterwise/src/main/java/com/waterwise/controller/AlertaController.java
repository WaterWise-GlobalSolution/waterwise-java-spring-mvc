package com.waterwise.controller;

import com.waterwise.model.Alerta;
import com.waterwise.repository.AlertaRepository;
import com.waterwise.repository.NivelSeveridadeRepository;
import com.waterwise.service.AlertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/alertas")
public class AlertaController {

    @Autowired
    private AlertaRepository alertaRepository;

    @Autowired
    private NivelSeveridadeRepository nivelSeveridadeRepository;

    @Autowired
    private AlertaService alertaService;

    @GetMapping
    public String listar(Model model,
            @RequestParam(required = false) String severidade,
            @RequestParam(required = false) Integer dias) {

        List<Alerta> alertas = alertaService.buscarAlertasComFiltros(severidade, dias, null);

        model.addAttribute("activeMenu", "alertas");
        model.addAttribute("alertas", alertas);
        model.addAttribute("niveisSeveridade", nivelSeveridadeRepository.findAllOrdered());
        model.addAttribute("severidadeSelecionada", severidade);
        model.addAttribute("diasSelecionados", dias);

        return "alertas/lista";
    }

    @GetMapping("/{id}")
    public String detalhes(@PathVariable Long id, Model model) {
        Alerta alerta = alertaRepository.findById(id).orElse(null);
        if (alerta == null) {
            return "redirect:/admin/alertas?erro=nao-encontrado";
        }

        model.addAttribute("alerta", alerta);
        return "alertas/detalhes";
    }

    @GetMapping("/dashboard")
    public String dashboardAlertas(Model model) {
        Map<String, Object> estatisticas = alertaService.obterEstatisticasAlertas();

        LocalDateTime ultima24h = LocalDateTime.now().minusHours(24);
        List<Alerta> alertasCriticos = alertaService.findAlertasCriticos();
        List<Alerta> alertasRecentes = alertaService.findAlertasRecentes(24);

        model.addAttribute("estatisticas", estatisticas);
        model.addAttribute("alertasCriticos", alertasCriticos);
        model.addAttribute("alertasRecentes", alertasRecentes);

        return "alertas/dashboard";
    }

    @GetMapping("/api/criticos")
    @ResponseBody
    public ResponseEntity<List<Alerta>> alertasCriticos() {
        List<Alerta> alertas = alertaService.findAlertasCriticos();
        return ResponseEntity.ok(alertas);
    }
}
