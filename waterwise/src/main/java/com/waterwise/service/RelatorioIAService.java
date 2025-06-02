package com.waterwise.service;

import com.waterwise.model.PropriedadeRural;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class RelatorioIAService {

    private final ChatClient chatClient;

    @Autowired
    private PropriedadeRuralService propriedadeService;

    // Constructor injection - Spring Boot auto-configura o ChatClient baseado nas properties
    public RelatorioIAService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String gerarRelatorioImpacto(Long propriedadeId) {
        try {
            PropriedadeRural propriedade = propriedadeService.findByIdWithSensores(propriedadeId);
            if (propriedade == null) {
                return "❌ Propriedade não encontrada no sistema.";
            }

            BigDecimal impactoRetencao = propriedadeService.calcularImpactoRetencaoHidrica(propriedadeId);

            String prompt = String.format("""
                🌊 ANÁLISE WATERWISE - PREVENÇÃO DE ENCHENTES MAIRIPORÃ-SP
                
                DADOS DA PROPRIEDADE RURAL:
                ═══════════════════════════════
                • Nome: %s
                • Área Total: %.2f hectares
                • Localização GPS: %.6f°, %.6f°
                • Sensores IoT Ativos: %d de %d
                • Nível de Degradação: %s
                • Capacidade de Absorção: %s
                • Impacto Estimado: %.0f litros/hora de retenção
                
                CONTEXTO REGIONAL MAIRIPORÃ:
                ═══════════════════════════════
                • 26 áreas oficiais de risco de enchentes urbanas
                • Topografia montanhosa com escoamento rápido
                • Urbanização crescente = menos áreas de absorção natural
                • Solo saudável absorve 20x mais água que solo degradado
                • Cada hectare bem manejado = proteção para famílias urbanas
                
                MISSÃO DA ANÁLISE:
                ═══════════════════════════════
                Como especialista em hidrologia urbana e prevenção de desastres, 
                gere um RELATÓRIO EXECUTIVO (máximo 500 palavras) com:
                
                📊 1. CAPACIDADE ATUAL DE RETENÇÃO
                   - Volume de água que esta propriedade pode absorver
                   - Comparativo com propriedades similares da região
                
                🏙️ 2. CONTRIBUIÇÃO PARA PREVENÇÃO URBANA
                   - Quantas residências urbanas são protegidas
                   - Redução estimada do risco de enchentes em %%
                
                🔧 3. RECOMENDAÇÕES TÉCNICAS PRIORITÁRIAS
                   - Otimizações específicas para esta propriedade
                   - ROI das melhorias propostas
                
                📈 4. CLASSIFICAÇÃO DE EFICIÊNCIA
                   - EXCELENTE (>80%% eficiência)
                   - BOM (60-80%% eficiência)  
                   - REGULAR (40-60%% eficiência)
                   - CRÍTICO (<40%% eficiência)
                
                💡 5. PRÓXIMOS PASSOS
                   - Ações imediatas para maximizar impacto
                   - Cronograma de implementação
                
                Use linguagem técnica mas acessível para gestores públicos e produtores rurais.
                Inclua dados quantitativos sempre que possível.
                """,
                    propriedade.getNomePropriedade(),
                    propriedade.getAreaHectares(),
                    propriedade.getLatitude(),
                    propriedade.getLongitude(),
                    propriedade.getSensoresAtivos(),
                    propriedade.getTotalSensores(),
                    propriedade.getNivelDegradacao() != null ?
                            propriedade.getNivelDegradacao().getDescricaoDegradacao() : "Não avaliado",
                    propriedade.getCapacidadeAbsorcaoDescricao(),
                    impactoRetencao
            );

            return chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

        } catch (Exception e) {
            return String.format("""
                ❌ ERRO AO GERAR RELATÓRIO DE IA
                
                Motivo: %s
                
                📋 VERIFICAÇÕES NECESSÁRIAS:
                • Ollama rodando em http://localhost:11434
                • Modelo gemma:2b instalado
                • Conexão de rede ativa
                • Dados da propriedade completos no banco
                
                💡 SOLUÇÃO: 
                1. ollama serve
                2. ollama pull gemma:2b
                3. ollama list (verificar se modelo está disponível)
                
                🔧 DEBUG: Verifique os logs da aplicação para mais detalhes
                """, e.getMessage());
        }
    }

    public String analisarTendenciasRegionais() {
        try {
            var propriedades = propriedadeService.findAll();
            var propriedadesMairipora = propriedadeService.findByRegiaoMairipora();

            String prompt = String.format("""
                🌍 ANÁLISE REGIONAL WATERWISE - MAIRIPORÃ SP
                
                DADOS DO SISTEMA:
                ════════════════════════════════════
                • Total de Propriedades Monitoradas: %d
                • Propriedades na Região de Mairiporã: %d
                • Área Total Monitorada: Calculada pelo sistema
                • Sensores IoT Ativos: Conectados ao sistema central
                
                DESAFIO REGIONAL:
                ════════════════════════════════════
                Mairiporã enfrenta crescimento urbano acelerado com:
                • 26 áreas oficiais de risco de enchentes
                • Impermeabilização crescente do solo urbano
                • Chuvas intensas concentradas (mudanças climáticas)
                • Necessidade de soluções baseadas na natureza
                
                OBJETIVO DA ANÁLISE:
                ════════════════════════════════════
                Como especialista em gestão hídrica regional, gere uma 
                ANÁLISE ESTRATÉGICA (máximo 400 palavras) sobre:
                
                📊 1. PADRÕES REGIONAIS IDENTIFICADOS
                   - Distribuição da capacidade de absorção
                   - Áreas com maior potencial de otimização
                
                🎯 2. ESTRATÉGIAS REGIONAIS RECOMENDADAS
                   - Priorização geográfica de investimentos
                   - Sinergias entre propriedades próximas
                
                💧 3. IMPACTO COLETIVO QUANTIFICADO
                   - Volume total de retenção da região
                   - Número estimado de famílias protegidas
                
                🚀 4. ROADMAP DE EXPANSÃO
                   - Próximas propriedades a incluir no sistema
                   - Metas para os próximos 12 meses
                
                Use abordagem data-driven com foco em tomada de decisão estratégica.
                """,
                    propriedades.size(),
                    propriedadesMairipora.size()
            );

            return chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

        } catch (Exception e) {
            return "❌ Erro ao gerar análise regional: " + e.getMessage();
        }
    }

    public String gerarRecomendacoesPrioritarias() {
        try {
            String prompt = """
                🎯 RECOMENDAÇÕES ESTRATÉGICAS WATERWISE 2025
                
                CONTEXTO DO SISTEMA:
                ═══════════════════════════════════════════
                O WaterWise é um sistema IoT integrado que monitora propriedades 
                rurais para prevenir enchentes urbanas em Mairiporã-SP através de:
                • Sensores de umidade do solo em tempo real
                • Análise de capacidade de retenção hídrica  
                • Alertas automáticos para gestores públicos
                • Relatórios de IA para otimização contínua
                
                MISSÃO:
                ═══════════════════════════════════════════
                Como consultor especialista em tecnologia para prevenção de desastres,
                gere 5 RECOMENDAÇÕES PRIORITÁRIAS (máximo 400 palavras) para 
                otimizar o sistema WaterWise:
                
                🔧 FOQUE EM:
                • Tecnologias IoT para expansão do monitoramento
                • Algoritmos de IA para predição mais precisa  
                • Estratégias de engajamento dos produtores rurais
                • Integração com sistemas de alerta urbano de Mairiporã
                • ROI e sustentabilidade financeira do projeto
                
                📋 FORMATO PARA CADA RECOMENDAÇÃO:
                • Título da recomendação
                • Justificativa técnica (2-3 linhas)
                • Impacto esperado quantificado
                • Prazo de implementação sugerido
                
                Priorize soluções práticas e implementáveis em 2025.
                """;

            return chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

        } catch (Exception e) {
            return "❌ Erro ao gerar recomendações: " + e.getMessage();
        }
    }
}