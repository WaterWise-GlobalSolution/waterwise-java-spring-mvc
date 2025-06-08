package com.waterwise.listener;

import com.waterwise.config.RabbitConfig;
import com.waterwise.dto.message.AlertaGeneratedMessage;
import com.waterwise.controller.NotificacaoController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "rabbitmq.enabled", havingValue = "true", matchIfMissing = false)
public class AlertaEventListener {

    private static final Logger logger = LoggerFactory.getLogger(AlertaEventListener.class);

    @RabbitListener(queues = RabbitConfig.ALERTA_QUEUE)
    public void processarAlertaGerado(AlertaGeneratedMessage message) {
        try {
            logger.warn("🚨 === PROCESSANDO ALERTA VIA RABBITMQ ===");
            logger.warn("🆔 ID Alerta: {}", message.getIdAlerta());
            logger.warn("🏠 Propriedade: {} (ID: {})", message.getNomePropriedade(), message.getIdPropriedade());
            logger.warn("⚠️ Tipo: {}", message.getTipoAlerta());
            logger.warn("🔥 Severidade: {}", message.getSeveridade());
            logger.warn("📝 Descrição: {}", message.getDescricao());
            logger.warn("⏰ Data: {}", message.getDataAlerta());

            // === 🔔 ENVIAR NOTIFICAÇÃO VISUAL ===
            String tipoNotificacao = mapearSeveridadeParaTipo(message.getSeveridade());
            String titulo = gerarTituloNotificacao(message);
            String mensagem = gerarMensagemNotificacao(message);

            NotificacaoController.adicionarNotificacao(
                    tipoNotificacao,
                    titulo,
                    mensagem,
                    "ALERTA"
            );

            // === 🎯 AÇÕES BASEADAS NA SEVERIDADE ===
            switch (message.getSeveridade().toUpperCase()) {
                case "CRITICO":
                    processarAlertaCritico(message);
                    break;
                case "ALTO":
                    processarAlertaAlto(message);
                    break;
                case "MEDIO":
                    processarAlertaMedio(message);
                    break;
                case "BAIXO":
                case "INFO":
                    processarAlertaInfo(message);
                    break;
                default:
                    logger.warn("⚠️ Severidade desconhecida: {}", message.getSeveridade());
            }

            logger.info("✅ Alerta processado e notificação enviada com sucesso");

        } catch (Exception e) {
            logger.error("❌ Erro ao processar alerta: {}", e.getMessage(), e);

            // Notificação de erro
            NotificacaoController.adicionarNotificacao(
                    "DANGER",
                    "🔥 Erro no Sistema",
                    "Falha ao processar alerta: " + e.getMessage(),
                    "SISTEMA"
            );
        }
    }

    // 🎨 MAPEAR SEVERIDADE PARA TIPO DE NOTIFICAÇÃO
    private String mapearSeveridadeParaTipo(String severidade) {
        switch (severidade.toUpperCase()) {
            case "CRITICO": return "DANGER";
            case "ALTO": return "WARNING";
            case "MEDIO": return "INFO";
            case "BAIXO":
            case "INFO": return "SUCCESS";
            default: return "INFO";
        }
    }

    // 📝 GERAR TÍTULO DA NOTIFICAÇÃO
    private String gerarTituloNotificacao(AlertaGeneratedMessage message) {
        String emoji = obterEmojiSeveridade(message.getSeveridade());
        String propriedade = message.getNomePropriedade() != null ?
                message.getNomePropriedade() : "Propriedade #" + message.getIdPropriedade();

        return String.format("%s Alerta %s - %s",
                emoji, message.getSeveridade(), propriedade);
    }

    // 💬 GERAR MENSAGEM DA NOTIFICAÇÃO
    private String gerarMensagemNotificacao(AlertaGeneratedMessage message) {
        StringBuilder msg = new StringBuilder();

        if (message.getDescricao() != null) {
            msg.append(message.getDescricao());
        }

        if (message.getNomeProdutor() != null) {
            msg.append(" | Produtor: ").append(message.getNomeProdutor());
        }

        return msg.toString();
    }

    // 😀 EMOJIS POR SEVERIDADE
    private String obterEmojiSeveridade(String severidade) {
        switch (severidade.toUpperCase()) {
            case "CRITICO": return "🚨";
            case "ALTO": return "⚠️";
            case "MEDIO": return "📊";
            case "BAIXO": return "ℹ️";
            case "INFO": return "💡";
            default: return "🔔";
        }
    }

    // === 🎯 PROCESSAMENTO POR SEVERIDADE ===

    private void processarAlertaCritico(AlertaGeneratedMessage message) {
        logger.error("🔴 ALERTA CRÍTICO DETECTADO!");

        // 📢 Notificação de emergência adicional
        NotificacaoController.adicionarNotificacao(
                "DANGER",
                "🚨 EMERGÊNCIA WATERWISE",
                "Alerta crítico detectado na " + message.getNomePropriedade() +
                        ". Ação imediata necessária!",
                "ALERTA"
        );

        // Simular ações de emergência
        enviarNotificacaoEmergencia(message);
        acionarEquipeResposta(message);
        registrarIncidente(message);
    }

    private void processarAlertaAlto(AlertaGeneratedMessage message) {
        logger.warn("🟠 ALERTA DE ALTA PRIORIDADE");

        NotificacaoController.adicionarNotificacao(
                "WARNING",
                "⚠️ Atenção Requerida",
                "Propriedade " + message.getNomePropriedade() + " necessita atenção. " +
                        "Verifique as condições do solo.",
                "ALERTA"
        );

        enviarNotificacaoGestores(message);
        agendarInspecao(message);
    }

    private void processarAlertaMedio(AlertaGeneratedMessage message) {
        logger.info("🟡 ALERTA DE PRIORIDADE MÉDIA");

        NotificacaoController.adicionarNotificacao(
                "INFO",
                "📊 Monitoramento",
                "Condições moderadas detectadas na " + message.getNomePropriedade(),
                "ALERTA"
        );
    }

    private void processarAlertaInfo(AlertaGeneratedMessage message) {
        logger.info("🔵 INFORMAÇÃO REGISTRADA");

        NotificacaoController.adicionarNotificacao(
                "SUCCESS",
                "💡 Informação",
                "Dados atualizados para " + message.getNomePropriedade(),
                "ALERTA"
        );
    }

    // === 🚀 MÉTODOS DE AÇÃO ===

    private void enviarNotificacaoEmergencia(AlertaGeneratedMessage message) {
        logger.error("📱 ENVIANDO NOTIFICAÇÃO DE EMERGÊNCIA!");

        // Notificação detalhada de emergência
        NotificacaoController.adicionarNotificacao(
                "DANGER",
                "🚨 Central de Emergência",
                String.format("EMERGÊNCIA: %s | Propriedade: %s | Contato: %s",
                        message.getDescricao(),
                        message.getNomePropriedade(),
                        message.getEmailProdutor()
                ),
                "SISTEMA"
        );
    }

    private void acionarEquipeResposta(AlertaGeneratedMessage message) {
        logger.error("👥 ACIONANDO EQUIPE DE RESPOSTA RÁPIDA");

        NotificacaoController.adicionarNotificacao(
                "WARNING",
                "🚗 Equipe Despachada",
                "Equipe técnica despachada para " + message.getNomePropriedade() +
                        ". ETA: 30 minutos.",
                "SISTEMA"
        );
    }

    private void registrarIncidente(AlertaGeneratedMessage message) {
        logger.error("📋 REGISTRANDO INCIDENTE NO SISTEMA");
        String numeroIncidente = "INC-" + System.currentTimeMillis();

        NotificacaoController.adicionarNotificacao(
                "INFO",
                "📋 Incidente Registrado",
                "Incidente " + numeroIncidente + " criado para " +
                        message.getNomePropriedade() + ". Status: ABERTO",
                "SISTEMA"
        );
    }

    private void enviarNotificacaoGestores(AlertaGeneratedMessage message) {
        logger.warn("📧 Enviando notificação para gestores...");

        NotificacaoController.adicionarNotificacao(
                "INFO",
                "📧 Gestores Notificados",
                "Gestores foram notificados sobre o alerta na " + message.getNomePropriedade(),
                "SISTEMA"
        );
    }

    private void agendarInspecao(AlertaGeneratedMessage message) {
        logger.warn("📅 Agendando inspeção técnica para propriedade ID: {}", message.getIdPropriedade());

        NotificacaoController.adicionarNotificacao(
                "INFO",
                "📅 Inspeção Agendada",
                "Inspeção técnica agendada para " + message.getNomePropriedade() +
                        " nas próximas 48 horas.",
                "SISTEMA"
        );
    }
}