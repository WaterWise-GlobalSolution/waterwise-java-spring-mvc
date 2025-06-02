package com.waterwise.controller;

import com.waterwise.service.RabbitMQService;
import com.waterwise.controller.NotificacaoController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/teste-rabbitmq")
@ConditionalOnProperty(name = "rabbitmq.enabled", havingValue = "true", matchIfMissing = false)
public class TesteRabbitMQController {

    @Autowired(required = false)
    private RabbitMQService rabbitMQService;

    // 🎯 PÁGINA DE TESTES
    @GetMapping
    public String paginaTestes(Model model) {
        boolean disponivel = rabbitMQService != null;
        model.addAttribute("rabbitmqDisponivel", disponivel);
        model.addAttribute("status", disponivel ? "🟢 Ativo" : "🔴 Inativo");

        return "teste-rabbitmq/index";
    }

    // 🧪 TESTE BÁSICO
    @PostMapping("/teste-basico")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> testeBasico() {
        Map<String, Object> response = new HashMap<>();

        try {
            if (rabbitMQService == null) {
                response.put("sucesso", false);
                response.put("mensagem", "RabbitMQ não está habilitado");
                return ResponseEntity.ok(response);
            }

            rabbitMQService.enviarMensagemTeste();

            // Adicionar notificação direta também
            NotificacaoController.adicionarNotificacao(
                    "SUCCESS",
                    "🧪 Teste RabbitMQ",
                    "Mensagem de teste enviada com sucesso!",
                    "TESTE"
            );

            response.put("sucesso", true);
            response.put("mensagem", "✅ Teste básico realizado com sucesso!");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", "❌ Erro: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 🚨 TESTE ALERTA CRÍTICO
    @PostMapping("/teste-alerta-critico")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> testeAlertaCritico() {
        Map<String, Object> response = new HashMap<>();

        try {
            if (rabbitMQService == null) {
                response.put("sucesso", false);
                response.put("mensagem", "RabbitMQ não está habilitado");
                return ResponseEntity.ok(response);
            }

            rabbitMQService.enviarAlertaCompleto(
                    999L,
                    1L,
                    "Fazenda Teste",
                    "UMIDADE_CRITICA",
                    "CRITICO",
                    "🧪 TESTE: Umidade do solo em nível crítico (12%) - teste do sistema de alertas",
                    "João Teste",
                    "joao.teste@waterwise.com"
            );

            response.put("sucesso", true);
            response.put("mensagem", "🚨 Alerta crítico de teste enviado!");
            response.put("tipo", "CRITICO");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", "❌ Erro ao enviar alerta: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 🌧️ TESTE CENÁRIO CHUVA INTENSA
    @PostMapping("/teste-chuva-intensa")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> testeChuvaIntensa() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Simular vários sensores detectando chuva intensa
            rabbitMQService.processarDadosSensor(1L, "PRECIPITACAO_MM", 95.5);
            rabbitMQService.processarDadosSensor(2L, "PRECIPITACAO_MM", 102.3);
            rabbitMQService.processarDadosSensor(3L, "UMIDADE_SOLO", 85.2);

            // Alerta regional
            rabbitMQService.enviarAlertaCompleto(
                    null, null, "Região Mairiporã", "PRECIPITACAO_INTENSA", "ALTO",
                    "🌧️ TESTE: Chuva intensa detectada em múltiplos sensores - possível risco de enchentes",
                    "Sistema Automático", "sistema@waterwise.com"
            );

            response.put("sucesso", true);
            response.put("mensagem", "🌧️ Cenário de chuva intensa simulado!");
            response.put("sensores", 3);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", "❌ Erro no cenário: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 🌵 TESTE CENÁRIO SECA
    @PostMapping("/teste-seca")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> testeSeca() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Simular sensores detectando baixa umidade
            rabbitMQService.processarDadosSensor(1L, "UMIDADE_SOLO", 15.2);
            rabbitMQService.processarDadosSensor(2L, "UMIDADE_SOLO", 18.7);
            rabbitMQService.processarDadosSensor(3L, "UMIDADE_SOLO", 12.1);

            rabbitMQService.enviarAlertaCompleto(
                    null, null, "Região Mairiporã", "SECA_PROLONGADA", "CRITICO",
                    "🌵 TESTE: Níveis críticos de umidade detectados - capacidade de retenção comprometida",
                    "Sistema Automático", "sistema@waterwise.com"
            );

            response.put("sucesso", true);
            response.put("mensagem", "🌵 Cenário de seca prolongada simulado!");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", "❌ Erro no cenário: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 📊 STATUS DO SISTEMA
    @GetMapping("/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> response = new HashMap<>();

        response.put("rabbitmqHabilitado", rabbitMQService != null);
        response.put("servicoDisponivel", rabbitMQService != null);
        response.put("timestamp", java.time.LocalDateTime.now());

        if (rabbitMQService != null) {
            response.put("status", "🟢 Online");
            response.put("mensagem", "RabbitMQ está funcionando!");
            response.put("filas", new String[]{"waterwise.propriedade.queue", "waterwise.alerta.queue"});
        } else {
            response.put("status", "🔴 Offline");
            response.put("mensagem", "RabbitMQ não está configurado. Configure rabbitmq.enabled=true");
        }

        return ResponseEntity.ok(response);
    }

    // 🎭 SIMULAÇÃO COMPLETA
    @PostMapping("/simulacao-completa")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> simulacaoCompleta() {
        Map<String, Object> response = new HashMap<>();

        try {
            // 1. Notificação de início
            NotificacaoController.adicionarNotificacao(
                    "INFO",
                    "🎭 Simulação Iniciada",
                    "Iniciando simulação completa do sistema WaterWise...",
                    "TESTE"
            );

            Thread.sleep(1000);

            // 2. Simular dados de sensores
            rabbitMQService.processarDadosSensor(1L, "UMIDADE_SOLO", 25.5);
            rabbitMQService.processarDadosSensor(2L, "TEMPERATURA_AR", 38.2);
            rabbitMQService.processarDadosSensor(3L, "PRECIPITACAO_MM", 85.1);

            Thread.sleep(1000);

            // 3. Simular alertas de diferentes severidades
            rabbitMQService.enviarAlertaCompleto(101L, 1L, "Fazenda Norte", "TEMPERATURA_ALTA", "MEDIO",
                    "Temperatura elevada detectada", "João Silva", "joao@fazenda.com");

            Thread.sleep(1000);

            rabbitMQService.enviarAlertaCompleto(102L, 2L, "Fazenda Sul", "UMIDADE_BAIXA", "ALTO",
                    "Umidade do solo abaixo do ideal", "Maria Santos", "maria@fazenda.com");

            Thread.sleep(1000);

            // 4. Alerta crítico
            rabbitMQService.enviarAlertaCompleto(103L, 3L, "Fazenda Central", "SISTEMA_CRITICO", "CRITICO",
                    "Sistema de irrigação falhou - intervenção imediata necessária", "Pedro Costa", "pedro@fazenda.com");

            // 5. Análise regional
            rabbitMQService.analisarImpactoRegional(8, 284.5);

            // 6. Notificação de conclusão
            NotificacaoController.adicionarNotificacao(
                    "SUCCESS",
                    "🎉 Simulação Concluída",
                    "Simulação completa finalizada. Verifique as notificações geradas.",
                    "TESTE"
            );

            response.put("sucesso", true);
            response.put("mensagem", "🎭 Simulação completa executada com sucesso!");
            response.put("alertasGerados", 4);
            response.put("sensoresSimulados", 3);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", "❌ Erro na simulação: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}