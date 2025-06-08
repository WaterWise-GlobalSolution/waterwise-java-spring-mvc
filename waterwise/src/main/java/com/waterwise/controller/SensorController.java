package com.waterwise.controller;

import com.waterwise.model.SensorIoT;
import com.waterwise.model.LeituraSensor;
import com.waterwise.repository.SensorIoTRepository;
import com.waterwise.repository.LeituraSensorRepository;
import com.waterwise.repository.TipoSensorRepository;
import com.waterwise.service.PropriedadeRuralService;
import com.waterwise.service.TipoSensorService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/sensores")
public class SensorController {

    @Autowired
    private SensorIoTRepository sensorRepository;

    @Autowired
    private LeituraSensorRepository leituraRepository;

    @Autowired
    private TipoSensorRepository tipoSensorRepository;

    @Autowired
    private TipoSensorService tipoSensorService;

    @Autowired
    private PropriedadeRuralService propriedadeService;

    @GetMapping
    public String listar(Model model) {
        try {
            List<SensorIoT> sensores = sensorRepository.findAllWithRelacionamentos();
            model.addAttribute("activeMenu", "sensores");
            model.addAttribute("sensores", sensores);
            model.addAttribute("totalSensores", sensores.size());
        } catch (Exception e) {
            System.err.println("Erro ao carregar sensores: " + e.getMessage());
            model.addAttribute("erro", "Erro ao carregar lista de sensores");
            model.addAttribute("sensores", new ArrayList<>());
            model.addAttribute("totalSensores", 0);
        }
        return "sensores/lista";
    }

    @GetMapping("/{id}")
    public String detalhes(@PathVariable Long id, Model model) {
        SensorIoT sensor = sensorRepository.findByIdWithLeituras(id);
        if (sensor == null) {
            return "redirect:/admin/sensores?erro=nao-encontrado";
        }

        // Buscar leituras recentes (últimos 7 dias)
        LocalDateTime dataInicio = LocalDateTime.now().minusDays(7);
        List<LeituraSensor> leiturasRecentes = leituraRepository.findLeiturasPorPeriodo(id, dataInicio);

        model.addAttribute("activeMenu", "sensores");
        model.addAttribute("sensor", sensor);
        model.addAttribute("leiturasRecentes", leiturasRecentes);
        model.addAttribute("ultimaLeitura", leituraRepository.findUltimaLeitura(id));

        return "sensores/detalhes";
    }

    @GetMapping("/propriedade/{propriedadeId}")
    public String sensoresPorPropriedade(@PathVariable Long propriedadeId, Model model) {
        var propriedade = propriedadeService.findById(propriedadeId);
        if (propriedade == null) {
            return "redirect:/admin/propriedades?erro=nao-encontrada";
        }

        List<SensorIoT> sensores = sensorRepository.findByIdPropriedade(propriedadeId);
        model.addAttribute("sensores", sensores);
        model.addAttribute("propriedade", propriedade);

        return "sensores/por-propriedade";
    }

    @GetMapping("/novo")
    public String novoForm(Model model, @RequestParam(required = false) Long propriedadeId) {
        SensorIoT sensor = new SensorIoT();

        // Se veio de uma propriedade específica, já preenche
        if (propriedadeId != null) {
            sensor.setIdPropriedade(propriedadeId);
        }

        model.addAttribute("activeMenu", "sensores");
        model.addAttribute("sensor", sensor);
        model.addAttribute("propriedades", propriedadeService.findAll());
        model.addAttribute("tiposSensores", tipoSensorService.findAll());
        model.addAttribute("produtores", propriedadeService.findAllProdutores());
        model.addAttribute("propriedadeId", propriedadeId);

        return "sensores/form";
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model) {
        SensorIoT sensor = sensorRepository.findById(id).orElse(null);
        if (sensor == null) {
            return "redirect:/admin/sensores?erro=nao-encontrado";
        }

        model.addAttribute("activeMenu", "sensores");
        model.addAttribute("sensor", sensor);
        model.addAttribute("propriedades", propriedadeService.findAll());
        model.addAttribute("tiposSensores", tipoSensorService.findAll());
        model.addAttribute("produtores", propriedadeService.findAllProdutores());
        model.addAttribute("propriedadeId", sensor.getIdPropriedade());

        return "sensores/form"; // Reutiliza o mesmo formulário
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute SensorIoT sensor,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("propriedades", propriedadeService.findAll());
            model.addAttribute("tiposSensores", tipoSensorService.findAll());
            model.addAttribute("produtores", propriedadeService.findAllProdutores());
            return "sensores/form";
        }

        System.out.println("ID Propriedade recebido: " + sensor.getIdPropriedade());
        System.out.println("ID Tipo Sensor recebido: " + sensor.getIdTipoSensor());

        try {
            // Definir data de instalação se não informada
            if (sensor.getDataInstalacao() == null) {
                sensor.setDataInstalacao(LocalDateTime.now());
            }

            SensorIoT sensorSalvo = sensorRepository.save(sensor);

            String acao = sensor.getIdSensor() != null ? "atualizado" : "cadastrado";
            redirectAttributes.addFlashAttribute("sucesso",
                    "Sensor IoT #" + sensorSalvo.getIdSensor() + " " + acao + " com sucesso!");

            return "redirect:/admin/sensores/" + sensorSalvo.getIdSensor();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro",
                    "Erro ao salvar sensor: " + e.getMessage());
            return "redirect:/admin/sensores/novo";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            sensorRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("sucesso", "Sensor excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro",
                    "Erro ao excluir sensor: " + e.getMessage());
        }
        return "redirect:/admin/sensores";
    }
}