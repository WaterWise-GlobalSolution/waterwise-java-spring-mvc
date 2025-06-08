package com.waterwise.controller;

import com.waterwise.service.PropriedadeRuralService;
import com.waterwise.service.DashboardService;
import com.waterwise.repository.SensorIoTRepository;
import com.waterwise.repository.LeituraSensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/relatorios")
public class RelatorioController {

        @Autowired
        private PropriedadeRuralService propriedadeService;

        @Autowired
        private DashboardService dashboardService;

        @Autowired
        private SensorIoTRepository sensorRepository;

        @Autowired
        private LeituraSensorRepository leituraRepository;

        @GetMapping("/capacidade-retencao")
        public String relatorioCapacidadeRetencao(Model model) {
                var propriedades = propriedadeService.findAll();

                // Agrupar por nível de degradação e calcular totais
                Map<String, Map<String, Object>> relatorio = propriedades.stream()
                                .filter(p -> p.getNivelDegradacao() != null)
                                .collect(Collectors.groupingBy(
                                                p -> p.getNivelDegradacao().getCodigoDegradacao(),
                                                Collectors.collectingAndThen(
                                                                Collectors.toList(),
                                                                lista -> {
                                                                        Map<String, Object> dados = new HashMap<>();
                                                                        dados.put("propriedades", lista.size());
                                                                        dados.put("areaTotal", lista.stream()
                                                                                        .mapToDouble(p -> p
                                                                                                        .getAreaHectares()
                                                                                                        .doubleValue())
                                                                                        .sum());
                                                                        dados.put("capacidadeMedia", lista.get(0)
                                                                                        .getNivelDegradacao()
                                                                                        .getCapacidadeAbsorcaoPercentual()
                                                                                        .doubleValue());
                                                                        dados.put("nivel", lista.get(0)
                                                                                        .getNivelDegradacao()
                                                                                        .getNivelNumerico());
                                                                        return dados;
                                                                })));

                model.addAttribute("relatorioCapacidade", relatorio);
                return "relatorios/capacidade-retencao";
        }

        @GetMapping("/eficiencia-regional")
        public String relatorioEficienciaRegional(Model model) {
                var propriedadesMairipora = propriedadeService.findByRegiaoMairipora();
                var estatisticas = dashboardService.obterEstatisticasDashboard();

                model.addAttribute("propriedadesMairipora", propriedadesMairipora);
                model.addAttribute("estatisticas", estatisticas);

                // Calcular eficiência por região
                double eficienciaMedia = propriedadesMairipora.stream()
                                .filter(p -> p.getNivelDegradacao() != null)
                                .mapToDouble(p -> p.getNivelDegradacao().getCapacidadeAbsorcaoPercentual()
                                                .doubleValue())
                                .average()
                                .orElse(50.0);

                model.addAttribute("eficienciaRegional", eficienciaMedia);

                return "relatorios/eficiencia-regional";
        }

        @GetMapping("/api/dados-grafico")
        @ResponseBody
        public ResponseEntity<Map<String, Object>> dadosGrafico(@RequestParam String tipo) {
                Map<String, Object> dados = new HashMap<>();

                switch (tipo) {
                        case "degradacao":
                                var propriedades = propriedadeService.findAll();
                                Map<Integer, Long> distribuicao = propriedades.stream()
                                                .filter(p -> p.getNivelDegradacao() != null)
                                                .collect(Collectors.groupingBy(
                                                                p -> p.getNivelDegradacao().getNivelNumerico(),
                                                                Collectors.counting()));
                                dados.put("distribuicao", distribuicao);
                                break;

                        case "sensores":
                                dados.put("totalSensores", sensorRepository.count());
                                dados.put("sensoresAtivos", sensorRepository.count()); // Todos ativos
                                break;

                        case "leituras":
                                Long sensorId = 1L; // ou a variável correta que representa o ID do sensor
                                LocalDateTime ultima24h = LocalDateTime.now().minusHours(24);
                                dados.put("leituras24h",
                                                leituraRepository.countLeiturasPorPeriodo(sensorId, ultima24h));
                                break;
                }

                return ResponseEntity.ok(dados);
        }
}
