package com.waterwise.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/integracao")
@ConditionalOnProperty(name = "rabbitmq.enabled", havingValue = "true", matchIfMissing = false)
public class IntegracaoController {

  @Autowired(required = false)
  private ConnectionFactory connectionFactory;

  /**
   * Status da integração com .NET API
   */
  @GetMapping("/status")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> statusIntegracao() {
    Map<String, Object> status = new HashMap<>();

    try {
      boolean conectado = false;
      if (connectionFactory != null) {
        Connection connection = connectionFactory.createConnection();
        conectado = connection.isOpen();
        connection.close();
      }

      status.put("rabbitmqConectado", conectado);
      status.put("integracaoAtiva", conectado);
      status.put("timestamp", LocalDateTime.now());
      status.put("servico", "WaterWise Spring MVC Consumer");
      status.put("filas", new String[] { "waterwise.sensor.data", "waterwise.dotnet.alerts" });

      if (conectado) {
        status.put("mensagem", "✅ Integração ativa - Recebendo dados da API .NET");
        status.put("status", "ONLINE");
      } else {
        status.put("mensagem", "❌ RabbitMQ desconectado - Verificar serviço");
        status.put("status", "OFFLINE");
      }

      return ResponseEntity.ok(status);

    } catch (Exception e) {
      status.put("rabbitmqConectado", false);
      status.put("integracaoAtiva", false);
      status.put("status", "ERROR");
      status.put("mensagem", "Erro: " + e.getMessage());
      return ResponseEntity.ok(status);
    }
  }

  /**
   * Teste manual da integração
   */
  @PostMapping("/teste")
  @ResponseBody
  public ResponseEntity<String> testeIntegracao() {
    NotificacaoController.adicionarNotificacao(
        "INFO",
        "🧪 Teste de Integração .NET",
        "Teste manual do consumer Spring MVC - Sistema pronto para receber dados do .NET!",
        "TESTE");

    return ResponseEntity.ok("✅ Teste executado - Verifique as notificações");
  }

  /**
   * Simular recebimento de dados do .NET
   */
  @PostMapping("/simular-sensor")
  @ResponseBody
  public ResponseEntity<String> simularDadosSensor() {
    // Simular dados críticos
    NotificacaoController.adicionarNotificacao(
        "DANGER",
        "🚨 SIMULAÇÃO - Umidade Crítica",
        "Simulação: Umidade do solo em 15% na Fazenda São João - Dados vindos da API .NET",
        "DOTNET_SENSOR");

    // Simular dados normais
    NotificacaoController.adicionarNotificacao(
        "SUCCESS",
        "📊 SIMULAÇÃO - Dados Recebidos",
        "Simulação: Sensor #123 - Fazenda Boa Vista | 💧45% | 🌡️25°C | 🌧️2mm",
        "DOTNET_SENSOR");

    return ResponseEntity.ok("✅ Simulação executada - Dados de exemplo criados");
  }
}