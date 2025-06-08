package com.waterwise.service;

import com.waterwise.config.RabbitConfig;
import com.waterwise.dto.message.AlertaGeneratedMessage;
import com.waterwise.dto.message.PropriedadeCreatedMessage;
import com.waterwise.model.PropriedadeRural;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@ConditionalOnProperty(name = "rabbitmq.enabled", havingValue = "true", matchIfMissing = false)
public class RabbitMQService {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQService.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * Envia notificação quando uma nova propriedade é criada
     */
    public void enviarNotificacaoPropriedadeCriada(PropriedadeRural propriedade) {
        try {
            logger.info("🌊 Enviando notificação de propriedade criada: {}", propriedade.getNomePropriedade());

            PropriedadeCreatedMessage message = new PropriedadeCreatedMessage(
                    propriedade.getIdPropriedade(),
                    propriedade.getNomePropriedade(),
                    propriedade.getAreaHectares(),
                    propriedade.getLatitude(),
                    propriedade.getLongitude(),
                    propriedade.getProdutor() != null ? propriedade.getProdutor().getNomeCompleto() : null,
                    propriedade.getProdutor() != null ? propriedade.getProdutor().getEmail() : null
            );

            rabbitTemplate.convertAndSend(
                    RabbitConfig.WATERWISE_EXCHANGE,
                    RabbitConfig.PROPRIEDADE_CREATED_KEY,
                    message
            );

            logger.info("✅ Mensagem enviada com sucesso para fila de propriedades");

        } catch (Exception e) {
            logger.error("❌ Erro ao enviar notificação de propriedade criada: {}", e.getMessage(), e);
        }
    }

    /**
     * Envia alerta gerado automaticamente
     */
    public void enviarAlertaGerado(Long propriedadeId, String tipoAlerta, String descricao) {
        try {
            logger.warn("🚨 Enviando alerta: {} para propriedade {}", tipoAlerta, propriedadeId);

            AlertaGeneratedMessage message = new AlertaGeneratedMessage();
            message.setIdPropriedade(propriedadeId);
            message.setTipoAlerta(tipoAlerta);
            message.setDescricao(descricao);
            message.setSeveridade(determinarSeveridade(tipoAlerta));

            rabbitTemplate.convertAndSend(
                    RabbitConfig.WATERWISE_EXCHANGE,
                    RabbitConfig.ALERTA_GENERATED_KEY,
                    message
            );

            logger.info("✅ Alerta enviado com sucesso para fila de alertas");

        } catch (Exception e) {
            logger.error("❌ Erro ao enviar alerta: {}", e.getMessage(), e);
        }
    }

    /**
     * Envia alerta com informações completas
     */
    public void enviarAlertaCompleto(Long idAlerta, Long idPropriedade, String nomePropriedade,
                                     String tipoAlerta, String severidade, String descricao,
                                     String nomeProdutor, String emailProdutor) {
        try {
            logger.warn("🚨 Enviando alerta completo: {} - {}", tipoAlerta, severidade);

            AlertaGeneratedMessage message = new AlertaGeneratedMessage(
                    idAlerta, idPropriedade, nomePropriedade, tipoAlerta,
                    severidade, descricao, nomeProdutor, emailProdutor
            );

            rabbitTemplate.convertAndSend(
                    RabbitConfig.WATERWISE_EXCHANGE,
                    RabbitConfig.ALERTA_GENERATED_KEY,
                    message
            );

            logger.info("✅ Alerta completo enviado com sucesso");

        } catch (Exception e) {
            logger.error("❌ Erro ao enviar alerta completo: {}", e.getMessage(), e);
        }
    }

    /**
     * Método auxiliar para processar dados de sensores
     */
    public void processarDadosSensor(Long sensorId, String tipo, Double valor) {
        logger.info("📊 Processando dados do sensor ID {}: {} = {}", sensorId, tipo, valor);

        // Detectar situações críticas e enviar alertas
        if ("UMIDADE_SOLO".equals(tipo) && valor != null && valor < 20) {
            enviarAlertaGerado(null, "UMIDADE_CRITICA",
                    String.format("Umidade do solo crítica: %.1f%%", valor));
        }

        if ("TEMPERATURA_AR".equals(tipo) && valor != null && valor > 40) {
            enviarAlertaGerado(null, "TEMPERATURA_ALTA",
                    String.format("Temperatura alta detectada: %.1f°C", valor));
        }

        if ("PRECIPITACAO_MM".equals(tipo) && valor != null && valor > 80) {
            enviarAlertaGerado(null, "PRECIPITACAO_INTENSA",
                    String.format("Precipitação intensa: %.1f mm", valor));
        }
    }

    /**
     * Análise de impacto regional
     */
    public void analisarImpactoRegional(int totalPropriedades, double areaTotal) {
        logger.info("🌍 ANÁLISE REGIONAL WaterWise:");
        logger.info("   • Total de propriedades: {}", totalPropriedades);
        logger.info("   • Área total monitorada: {} hectares", areaTotal);
        logger.info("   • Impacto estimado na prevenção de enchentes: ALTO");
        logger.info("   • Famílias protegidas (estimativa): {}", totalPropriedades * 15);

        // Enviar relatório regional via mensagem
        try {
            AlertaGeneratedMessage relatorio = new AlertaGeneratedMessage();
            relatorio.setTipoAlerta("RELATORIO_REGIONAL");
            relatorio.setSeveridade("INFO");
            relatorio.setDescricao(String.format(
                    "Análise Regional - %d propriedades, %.1f hectares, %d famílias protegidas",
                    totalPropriedades, areaTotal, totalPropriedades * 15
            ));

            rabbitTemplate.convertAndSend(
                    RabbitConfig.WATERWISE_EXCHANGE,
                    RabbitConfig.ALERTA_GENERATED_KEY,
                    relatorio
            );

        } catch (Exception e) {
            logger.error("❌ Erro ao enviar relatório regional: {}", e.getMessage());
        }
    }

    /**
     * Determina a severidade baseada no tipo de alerta
     */
    private String determinarSeveridade(String tipoAlerta) {
        switch (tipoAlerta.toUpperCase()) {
            case "UMIDADE_CRITICA":
            case "TEMPERATURA_EXTREMA":
                return "CRITICO";
            case "PRECIPITACAO_INTENSA":
            case "NIVEL_AGUA_ALTO":
                return "ALTO";
            case "TEMPERATURA_ALTA":
            case "UMIDADE_BAIXA":
                return "MEDIO";
            case "RELATORIO_REGIONAL":
                return "INFO";
            default:
                return "BAIXO";
        }
    }

    /**
     * Método para testar o envio de mensagens
     */
    public void enviarMensagemTeste() {
        try {
            logger.info("🧪 Enviando mensagem de teste...");

            AlertaGeneratedMessage teste = new AlertaGeneratedMessage();
            teste.setTipoAlerta("TESTE_SISTEMA");
            teste.setSeveridade("INFO");
            teste.setDescricao("Mensagem de teste do sistema WaterWise - " + LocalDateTime.now());

            rabbitTemplate.convertAndSend(
                    RabbitConfig.WATERWISE_EXCHANGE,
                    RabbitConfig.ALERTA_GENERATED_KEY,
                    teste
            );

            logger.info("✅ Mensagem de teste enviada com sucesso!");

        } catch (Exception e) {
            logger.error("❌ Erro ao enviar mensagem de teste: {}", e.getMessage(), e);
        }
    }
}