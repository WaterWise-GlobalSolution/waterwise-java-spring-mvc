package com.waterwise.listener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.waterwise.config.RabbitConfig;
import com.waterwise.controller.NotificacaoController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@ConditionalOnProperty(name = "rabbitmq.enabled", havingValue = "true", matchIfMissing = false)
public class DotNetConsumer {

  private static final Logger logger = LoggerFactory.getLogger(DotNetConsumer.class);

  /**
   * Consumer para dados de sensores vindos do .NET
   */
  @RabbitListener(queues = RabbitConfig.SENSOR_DATA_QUEUE)
  public void receberDadosSensorDotNet(@Payload SensorDataMessage message) {
    try {
      logger.info("🔔 === DADOS DO .NET RECEBIDOS ===");
      logger.info("📊 Sensor ID: {}", message.getSensorId());
      logger.info("🏠 Propriedade: {}", message.getPropertyName());
      logger.info("👤 Produtor: {}", message.getProducerName());

      if (message.getReading() != null) {
        var reading = message.getReading();
        logger.info("📈 Umidade Solo: {}%", reading.getUmidadeSolo());
        logger.info("🌡️ Temperatura: {}°C", reading.getTemperaturaAr());
        logger.info("🌧️ Precipitação: {}mm", reading.getPrecipitacaoMm());
      }

      // Processar e criar notificação visual
      processarDadosSensor(message);

      logger.info("✅ Dados do .NET processados com sucesso");

    } catch (Exception e) {
      logger.error("❌ Erro ao processar dados do .NET: {}", e.getMessage(), e);

      NotificacaoController.adicionarNotificacao(
          "DANGER",
          "🔥 Erro na Integração",
          "Falha ao processar dados do .NET: " + e.getMessage(),
          "SISTEMA");
    }
  }

  /**
   * Consumer para alertas vindos do .NET
   */
  @RabbitListener(queues = RabbitConfig.DOTNET_ALERTS_QUEUE)
  public void receberAlertaDotNet(@Payload String alertMessage) {
    try {
      logger.warn("🚨 === ALERTA DO .NET RECEBIDO ===");
      logger.warn("📄 Conteúdo: {}", alertMessage);

      // Criar notificação de alerta
      NotificacaoController.adicionarNotificacao(
          "WARNING",
          "🚨 Alerta da API .NET",
          alertMessage,
          "DOTNET_API");

      logger.info("✅ Alerta do .NET processado");

    } catch (Exception e) {
      logger.error("❌ Erro ao processar alerta do .NET: {}", e.getMessage(), e);
    }
  }

  private void processarDadosSensor(SensorDataMessage message) {
    try {
      String titulo = String.format("📊 Sensor #%d - %s",
          message.getSensorId(), message.getPropertyName());

      String mensagem = String.format("Produtor: %s", message.getProducerName());

      String tipoNotificacao = "INFO";

      if (message.getReading() != null) {
        var reading = message.getReading();

        // Detectar condições críticas
        if (reading.getUmidadeSolo() != null && reading.getUmidadeSolo().compareTo(new BigDecimal("20")) < 0) {
          tipoNotificacao = "DANGER";
          titulo = "🚨 UMIDADE CRÍTICA - " + message.getPropertyName();
          mensagem = String.format("Umidade do solo em %.1f%% - Ação imediata necessária!",
              reading.getUmidadeSolo());

        } else if (reading.getPrecipitacaoMm() != null
            && reading.getPrecipitacaoMm().compareTo(new BigDecimal("50")) > 0) {
          tipoNotificacao = "WARNING";
          titulo = "⚠️ CHUVA INTENSA - " + message.getPropertyName();
          mensagem = String.format("Precipitação de %.1fmm detectada - Monitorar capacidade de retenção",
              reading.getPrecipitacaoMm());

        } else if (reading.getTemperaturaAr() != null
            && reading.getTemperaturaAr().compareTo(new BigDecimal("35")) > 0) {
          tipoNotificacao = "WARNING";
          titulo = "🌡️ TEMPERATURA ALTA - " + message.getPropertyName();
          mensagem = String.format("Temperatura de %.1f°C - Monitorar impacto no solo",
              reading.getTemperaturaAr());
        } else {
          // Dados normais - mostrar resumo
          mensagem += String.format(" | 💧%.1f%% | 🌡️%.1f°C | 🌧️%.1fmm",
              reading.getUmidadeSolo() != null ? reading.getUmidadeSolo() : 0,
              reading.getTemperaturaAr() != null ? reading.getTemperaturaAr() : 0,
              reading.getPrecipitacaoMm() != null ? reading.getPrecipitacaoMm() : 0);
        }
      }

      // Criar notificação visual
      NotificacaoController.adicionarNotificacao(
          tipoNotificacao,
          titulo,
          mensagem,
          "DOTNET_SENSOR");

      logger.info("🔔 Notificação criada: {} - {}", titulo, tipoNotificacao);

    } catch (Exception e) {
      logger.error("❌ Erro ao processar dados do sensor: {}", e.getMessage(), e);
    }
  }

  // ===== DTOs PARA RECEBER DADOS DO .NET =====

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class SensorDataMessage {
    @JsonProperty("SensorId")
    private Long sensorId;

    @JsonProperty("PropertyName")
    private String propertyName;

    @JsonProperty("ProducerName")
    private String producerName;

    @JsonProperty("Reading")
    private SensorReading reading;

    @JsonProperty("Timestamp")
    private String timestamp;

    // Getters e Setters
    public Long getSensorId() {
      return sensorId;
    }

    public void setSensorId(Long sensorId) {
      this.sensorId = sensorId;
    }

    public String getPropertyName() {
      return propertyName;
    }

    public void setPropertyName(String propertyName) {
      this.propertyName = propertyName;
    }

    public String getProducerName() {
      return producerName;
    }

    public void setProducerName(String producerName) {
      this.producerName = producerName;
    }

    public SensorReading getReading() {
      return reading;
    }

    public void setReading(SensorReading reading) {
      this.reading = reading;
    }

    public String getTimestamp() {
      return timestamp;
    }

    public void setTimestamp(String timestamp) {
      this.timestamp = timestamp;
    }
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class SensorReading {
    @JsonProperty("Id")
    private Long id;

    @JsonProperty("IdSensor")
    private Long idSensor;

    @JsonProperty("TimestampLeitura")
    private String timestampLeitura;

    @JsonProperty("UmidadeSolo")
    private BigDecimal umidadeSolo;

    @JsonProperty("TemperaturaAr")
    private BigDecimal temperaturaAr;

    @JsonProperty("PrecipitacaoMm")
    private BigDecimal precipitacaoMm;

    // Getters e Setters
    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public Long getIdSensor() {
      return idSensor;
    }

    public void setIdSensor(Long idSensor) {
      this.idSensor = idSensor;
    }

    public String getTimestampLeitura() {
      return timestampLeitura;
    }

    public void setTimestampLeitura(String timestampLeitura) {
      this.timestampLeitura = timestampLeitura;
    }

    public BigDecimal getUmidadeSolo() {
      return umidadeSolo;
    }

    public void setUmidadeSolo(BigDecimal umidadeSolo) {
      this.umidadeSolo = umidadeSolo;
    }

    public BigDecimal getTemperaturaAr() {
      return temperaturaAr;
    }

    public void setTemperaturaAr(BigDecimal temperaturaAr) {
      this.temperaturaAr = temperaturaAr;
    }

    public BigDecimal getPrecipitacaoMm() {
      return precipitacaoMm;
    }

    public void setPrecipitacaoMm(BigDecimal precipitacaoMm) {
      this.precipitacaoMm = precipitacaoMm;
    }
  }
}