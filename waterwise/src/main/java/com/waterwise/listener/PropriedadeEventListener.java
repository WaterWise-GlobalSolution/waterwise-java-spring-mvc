// PropriedadeEventListener.java
package com.waterwise.listener;

import com.waterwise.config.RabbitConfig;
import com.waterwise.dto.message.PropriedadeCreatedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "rabbitmq.enabled", havingValue = "true", matchIfMissing = false)
public class PropriedadeEventListener {

    private static final Logger logger = LoggerFactory.getLogger(PropriedadeEventListener.class);

    @RabbitListener(queues = RabbitConfig.PROPRIEDADE_QUEUE)
    public void processarPropriedadeCriada(PropriedadeCreatedMessage message) {
        try {
            logger.info("🏡 === PROCESSANDO PROPRIEDADE CRIADA ===");
            logger.info("📋 ID: {}", message.getIdPropriedade());
            logger.info("🏠 Nome: {}", message.getNomePropriedade());
            logger.info("📏 Área: {} hectares", message.getAreaHectares());
            logger.info("📍 Coordenadas: {}, {}", message.getLatitude(), message.getLongitude());
            logger.info("👤 Produtor: {}", message.getNomeProdutor());
            logger.info("📧 Email: {}", message.getEmailProdutor());
            logger.info("⏰ Data Criação: {}", message.getDataCriacao());

            // === AÇÕES DE PROCESSAMENTO ===

            // 1. Enviar email de boas-vindas
            enviarEmailBoasVindas(message);

            // 2. Calcular impacto na prevenção de enchentes
            calcularImpactoHidrico(message);

            // 3. Verificar proximidade com outras propriedades
            verificarProximidadePropriedades(message);

            // 4. Gerar relatório automático
            gerarRelatorioAutomatico(message);

            // 5. Registrar em sistema de auditoria
            registrarAuditoria(message);

            logger.info("✅ Propriedade processada com sucesso: {}", message.getNomePropriedade());

        } catch (Exception e) {
            logger.error("❌ Erro ao processar propriedade criada: {}", e.getMessage(), e);
            // Aqui você poderia implementar retry ou enviar para DLQ
        }
    }

    private void enviarEmailBoasVindas(PropriedadeCreatedMessage message) {
        logger.info("📧 Enviando email de boas-vindas para: {}", message.getEmailProdutor());

        // Simular envio de email
        String conteudoEmail = String.format("""
            🌊 Bem-vindo ao WaterWise, %s!
            
            Sua propriedade '%s' foi cadastrada com sucesso no sistema de prevenção a enchentes.
            
            📊 Dados da propriedade:
            • Área: %.2f hectares
            • Localização: %.6f, %.6f
            • Potencial de impacto: Alto
            
            🚀 Próximos passos:
            1. Instalação dos sensores IoT
            2. Configuração do monitoramento
            3. Treinamento da equipe
            
            Obrigado por fazer parte da solução! 💧
            
            Equipe WaterWise Mairiporã
            """,
                message.getNomeProdutor(),
                message.getNomePropriedade(),
                message.getAreaHectares(),
                message.getLatitude(),
                message.getLongitude()
        );

        logger.info("✉️ Email preparado: {}", conteudoEmail.substring(0, 100) + "...");

        // Aqui você integraria com um serviço real de email:
        // - SendGrid
        // - Amazon SES
        // - JavaMailSender
    }

    private void calcularImpactoHidrico(PropriedadeCreatedMessage message) {
        logger.info("💧 Calculando impacto hídrico da propriedade...");

        double area = message.getAreaHectares().doubleValue();

        // Cálculos baseados na área e localização
        double capacidadeRetencao = area * 1000; // L/h estimado
        int familiasProtegidas = (int) (area / 2); // 1 família a cada 2 hectares
        double reducaoEnchentes = Math.min(area * 0.1, 15.0); // Max 15%

        logger.info("📊 IMPACTO CALCULADO:");
        logger.info("   💧 Capacidade de retenção: {} L/h", capacidadeRetencao);
        logger.info("   🏠 Famílias protegidas: {}", familiasProtegidas);
        logger.info("   📉 Redução risco enchentes: {}%", reducaoEnchentes);

        // Salvar métricas em banco de dados de analytics
        // analyticsRepository.save(new ImpactoMetrics(message.getIdPropriedade(), ...))
    }

    private void verificarProximidadePropriedades(PropriedadeCreatedMessage message) {
        logger.info("🗺️ Verificando proximidade com outras propriedades...");

        // Simular verificação de proximidade
        double lat = message.getLatitude().doubleValue();
        double lng = message.getLongitude().doubleValue();

        logger.info("📍 Analisando região: {}, {}", lat, lng);

        // Aqui você faria uma consulta ao banco para encontrar propriedades próximas
        // List<PropriedadeRural> proximasPropriedades = 
        //     propriedadeRepository.findPropriedadesProximas(lat, lng, 5.0); // 5km

        logger.info("🔍 Propriedades próximas encontradas: 2");
        logger.info("🤝 Potencial para cooperação regional identificado");
    }

    private void gerarRelatorioAutomatico(PropriedadeCreatedMessage message) {
        logger.info("📋 Gerando relatório automático de cadastro...");

        String relatorio = String.format("""
            📊 RELATÓRIO DE CADASTRO - WATERWISE
            ═══════════════════════════════════════
            
            🏠 PROPRIEDADE: %s
            👤 PRODUTOR: %s
            📏 ÁREA: %.2f hectares
            📍 LOCALIZAÇÃO: %.6f, %.6f
            ⏰ CADASTRADO EM: %s
            
            🎯 ANÁLISE PRELIMINAR:
            ✅ Propriedade dentro da região de Mairiporã
            ✅ Área adequada para monitoramento
            ✅ Potencial alto de impacto na prevenção
            
            📈 PRÓXIMAS ETAPAS:
            1. Agendamento de visita técnica
            2. Instalação de sensores IoT
            3. Treinamento do produtor
            4. Início do monitoramento
            
            Sistema WaterWise © 2025
            """,
                message.getNomePropriedade(),
                message.getNomeProdutor(),
                message.getAreaHectares(),
                message.getLatitude(),
                message.getLongitude(),
                message.getDataCriacao()
        );

        logger.info("📄 Relatório gerado: {} caracteres", relatorio.length());
        // Salvar relatório ou enviar para gestores
    }

    private void registrarAuditoria(PropriedadeCreatedMessage message) {
        logger.info("📝 Registrando evento de auditoria...");

        // Simular registro de auditoria
        logger.info("🔍 AUDITORIA REGISTRADA:");
        logger.info("   📅 Timestamp: {}", java.time.LocalDateTime.now());
        logger.info("   🏠 Propriedade ID: {}", message.getIdPropriedade());
        logger.info("   🎯 Evento: PROPRIEDADE_CRIADA");
        logger.info("   👤 Usuário: Sistema");
        logger.info("   📊 Status: SUCESSO");

        // Aqui você salvaria no banco de auditoria:
        // auditRepository.save(new AuditEvent("PROPRIEDADE_CRIADA", message.getIdPropriedade(), ...))
    }
}
