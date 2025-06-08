package com.waterwise.controller;

import com.waterwise.service.RabbitMQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/rabbitmq")
@ConditionalOnProperty(name = "rabbitmq.enabled", havingValue = "true", matchIfMissing = false)
public class RabbitMQTestController {

    @Autowired(required = false)
    private RabbitMQService rabbitMQService;

    /**
     * Página de testes do RabbitMQ
     */
    @GetMapping("/test")
    public String testPage() {
        return "rabbitmq-test"; // Você criaria este template
    }

    /**
     * Testar envio de mensagem simples
     */
    @PostMapping("/test/message")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> testMessage() {
        Map<String, Object> response = new HashMap<>();

        try {
            if (rabbitMQService == null) {
                response.put("success", false);
                response.put("message", "RabbitMQ Service não está disponível. Verifique se rabbitmq.enabled=true");
                return ResponseEntity.ok(response);
            }

            rabbitMQService.enviarMensagemTeste();

            response.put("success", true);
            response.put("message", "Mensagem de teste enviada com sucesso!");
            response.put("timestamp", java.time.LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erro ao enviar mensagem: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Testar alerta crítico
     */
    @PostMapping("/test/alerta-critico")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> testAlertaCritico() {
        Map<String, Object> response = new HashMap<>();

        try {
            rabbitMQService.enviarAlertaCompleto(
                    999L, // ID do alerta
                    1L,   // ID da propriedade
                    "Fazenda Teste",
                    "UMIDADE_CRITICA",
                    "CRITICO",
                    "Teste de alerta crítico - umidade do solo em 15%",
                    "João Teste",
                    "joao@teste.com"
            );

            response.put("success", true);
            response.put("message", "Alerta crítico de teste enviado!");
            response.put("type", "CRITICO");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erro ao enviar alerta: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Testar processamento de dados de sensor
     */
    @PostMapping("/test/sensor-data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> testSensorData(
            @RequestParam Long sensorId,
            @RequestParam String tipo,
            @RequestParam Double valor) {

        Map<String, Object> response = new HashMap<>();

        try {
            rabbitMQService.processarDadosSensor(sensorId, tipo, valor);

            response.put("success", true);
            response.put("message", "Dados do sensor processados com sucesso!");
            response.put("sensorId", sensorId);
            response.put("tipo", tipo);
            response.put("valor", valor);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erro ao processar dados: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Testar análise regional
     */
    @PostMapping("/test/analise-regional")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> testAnaliseRegional() {
        Map<String, Object> response = new HashMap<>();

        try {
            rabbitMQService.analisarImpactoRegional(8, 284.5);

            response.put("success", true);
            response.put("message", "Análise regional enviada com sucesso!");
            response.put("propriedades", 8);
            response.put("area", 284.5);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erro ao enviar análise: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Status do RabbitMQ
     */
    @GetMapping("/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> response = new HashMap<>();

        response.put("rabbitmqEnabled", rabbitMQService != null);
        response.put("serviceAvailable", rabbitMQService != null);
        response.put("timestamp", java.time.LocalDateTime.now());

        if (rabbitMQService != null) {
            response.put("message", "RabbitMQ está funcionando!");
            response.put("exchanges", "waterwise.exchange");
            response.put("queues", new String[]{"waterwise.propriedade.queue", "waterwise.alerta.queue"});
        } else {
            response.put("message", "RabbitMQ não está configurado. Configure rabbitmq.enabled=true");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Simulação de cenários completos
     */
    @PostMapping("/test/cenario/{tipo}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> testCenario(@PathVariable String tipo) {
        Map<String, Object> response = new HashMap<>();

        try {
            switch (tipo.toLowerCase()) {
                case "chuva-intensa":
                    simularChuvaIntensa();
                    response.put("message", "Cenário de chuva intensa simulado");
                    break;

                case "seca-prolongada":
                    simularSecaProlongada();
                    response.put("message", "Cenário de seca prolongada simulado");
                    break;

                case "temperatura-extrema":
                    simularTemperaturaExtrema();
                    response.put("message", "Cenário de temperatura extrema simulado");
                    break;

                default:
                    response.put("success", false);
                    response.put("message", "Cenário desconhecido: " + tipo);
                    return ResponseEntity.ok(response);
            }

            response.put("success", true);
            response.put("cenario", tipo);
            response.put("timestamp", java.time.LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erro ao simular cenário: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    private void simularChuvaIntensa() {
        // Simular vários sensores reportando chuva intensa
        rabbitMQService.processarDadosSensor(1L, "PRECIPITACAO_MM", 95.5);
        rabbitMQService.processarDadosSensor(2L, "PRECIPITACAO_MM", 102.3);
        rabbitMQService.processarDadosSensor(3L, "UMIDADE_SOLO", 85.2);

        // Enviar alerta regional
        rabbitMQService.enviarAlertaCompleto(
                null, null, "Região Mairiporã", "PRECIPITACAO_INTENSA", "ALTO",
                "Chuva intensa detectada em múltiplos sensores - possível risco de enchentes",
                "Sistema Automático", "sistema@waterwise.com"
        );
    }

    private void simularSecaProlongada() {
        // Simular sensores reportando baixa umidade
        rabbitMQService.processarDadosSensor(1L, "UMIDADE_SOLO", 15.2);
        rabbitMQService.processarDadosSensor(2L, "UMIDADE_SOLO", 18.7);
        rabbitMQService.processarDadosSensor(3L, "UMIDADE_SOLO", 12.1);

        rabbitMQService.enviarAlertaCompleto(
                null, null, "Região Mairiporã", "SECA_PROLONGADA", "CRITICO",
                "Níveis críticos de umidade detectados - capacidade de retenção comprometida",
                "Sistema Automático", "sistema@waterwise.com"
        );
    }

    private void simularTemperaturaExtrema() {
        // Simular temperaturas altas
        rabbitMQService.processarDadosSensor(1L, "TEMPERATURA_AR", 42.5);
        rabbitMQService.processarDadosSensor(2L, "TEMPERATURA_AR", 41.8);
        rabbitMQService.processarDadosSensor(3L, "TEMPERATURA_AR", 43.2);

        rabbitMQService.enviarAlertaCompleto(
                null, null, "Região Mairiporã", "TEMPERATURA_EXTREMA", "ALTO",
                "Temperaturas extremas detectadas - monitorar impacto no solo",
                "Sistema Automático", "sistema@waterwise.com"
        );
    }
}