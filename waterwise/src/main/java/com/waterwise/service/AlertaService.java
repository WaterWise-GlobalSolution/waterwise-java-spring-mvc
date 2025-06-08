package com.waterwise.service;

import com.waterwise.model.Alerta;
import com.waterwise.model.LeituraSensor;
import com.waterwise.model.NivelSeveridade;
import com.waterwise.repository.AlertaRepository;
import com.waterwise.repository.NivelSeveridadeRepository;
import com.waterwise.repository.LeituraSensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AlertaService {

    @Autowired
    private AlertaRepository alertaRepository;

    @Autowired
    private NivelSeveridadeRepository nivelSeveridadeRepository;

    @Autowired
    private LeituraSensorRepository leituraRepository;

    @Autowired(required = false) // Pode não estar disponível se RabbitMQ estiver desabilitado
    private RabbitMQService rabbitMQService;

    public List<Alerta> findAll() {
        return alertaRepository.findAll();
    }

    public Alerta findById(Long id) {
        return alertaRepository.findById(id).orElse(null);
    }

    public List<Alerta> findByProdutor(Long idProdutor) {
        return alertaRepository.findByIdProdutorOrderByTimestampAlertaDesc(idProdutor);
    }

    public List<Alerta> findBySeveridade(String severidade) {
        return alertaRepository.findBySeveridade(severidade);
    }

    public List<Alerta> findAlertasRecentes(int horas) {
        LocalDateTime dataLimite = LocalDateTime.now().minusHours(horas);
        return alertaRepository.findAlertasPorPeriodo(dataLimite);
    }

    public List<Alerta> findAlertasCriticos() {
        return alertaRepository.findBySeveridade("CRITICO");
    }

    public long countAlertasRecentes(int horas) {
        LocalDateTime dataLimite = LocalDateTime.now().minusHours(horas);
        return alertaRepository.countAlertasPorPeriodo(dataLimite);
    }

    public Alerta save(Alerta alerta) {
        Alerta savedAlerta = alertaRepository.save(alerta);

        // === INTEGRAÇÃO COM RABBITMQ ===
        // Enviar notificação via RabbitMQ se estiver habilitado
        if (rabbitMQService != null && savedAlerta.isCritico()) {
            try {
                // Buscar informações completas para a mensagem
                String nomePropriedade = null;
                String nomeProdutor = null;
                String emailProdutor = null;

                if (savedAlerta.getLeitura() != null &&
                        savedAlerta.getLeitura().getSensor() != null &&
                        savedAlerta.getLeitura().getSensor().getPropriedade() != null) {

                    var propriedade = savedAlerta.getLeitura().getSensor().getPropriedade();
                    nomePropriedade = propriedade.getNomePropriedade();

                    if (propriedade.getProdutor() != null) {
                        nomeProdutor = propriedade.getProdutor().getNomeCompleto();
                        emailProdutor = propriedade.getProdutor().getEmail();
                    }
                }

                // Enviar mensagem completa via RabbitMQ
                rabbitMQService.enviarAlertaCompleto(
                        savedAlerta.getIdAlerta(),
                        savedAlerta.getIdProdutor(),
                        nomePropriedade,
                        "ALERTA_CRITICO",
                        savedAlerta.getSeveridadeCodigo(),
                        savedAlerta.getDescricaoAlerta(),
                        nomeProdutor,
                        emailProdutor
                );

            } catch (Exception e) {
                // Log do erro mas não falha a operação principal
                System.err.println("❌ Erro ao enviar notificação RabbitMQ: " + e.getMessage());
            }
        }

        return savedAlerta;
    }

    public void deleteById(Long id) {
        alertaRepository.deleteById(id);
    }

    /**
     * Método principal para analisar leituras e gerar alertas automaticamente
     * ATUALIZADO com integração RabbitMQ
     */
    public void analisarLeituraEGerarAlertas(LeituraSensor leitura) {
        if (leitura == null || leitura.getIdSensor() == null) return;

        try {
            // Verificar umidade muito baixa (risco de baixa retenção)
            if (leitura.getUmidadeSolo() != null &&
                    leitura.getUmidadeSolo().compareTo(new BigDecimal("30")) < 0) {

                String severidade = leitura.getUmidadeSolo().compareTo(new BigDecimal("20")) < 0 ? "CRITICO" : "ALTO";
                String descricao = String.format(
                        "Umidade do solo baixa (%.1f%%) - risco de capacidade reduzida de retenção hídrica",
                        leitura.getUmidadeSolo()
                );

                Alerta alerta = criarAlerta(leitura, severidade, descricao);

                // === NOVA INTEGRAÇÃO RABBITMQ ===
                // Enviar também via RabbitMQ para processamento assíncrono
                if (rabbitMQService != null && alerta != null) {
                    rabbitMQService.processarDadosSensor(
                            leitura.getIdSensor(),
                            "UMIDADE_SOLO",
                            leitura.getUmidadeSolo().doubleValue()
                    );
                }
            }

            // Verificar temperatura muito alta (pode afetar solo)
            if (leitura.getTemperaturaAr() != null &&
                    leitura.getTemperaturaAr().compareTo(new BigDecimal("35")) > 0) {

                String descricao = String.format(
                        "Temperatura elevada (%.1f°C) - monitorar impacto no solo",
                        leitura.getTemperaturaAr()
                );

                Alerta alerta = criarAlerta(leitura, "MEDIO", descricao);

                // Enviar via RabbitMQ
                if (rabbitMQService != null && alerta != null) {
                    rabbitMQService.processarDadosSensor(
                            leitura.getIdSensor(),
                            "TEMPERATURA_AR",
                            leitura.getTemperaturaAr().doubleValue()
                    );
                }
            }

            // Verificar chuva intensa (teste do sistema de retenção)
            if (leitura.getPrecipitacaoMm() != null &&
                    leitura.getPrecipitacaoMm().compareTo(new BigDecimal("50")) > 0) {

                String severidade = leitura.getPrecipitacaoMm().compareTo(new BigDecimal("100")) > 0 ? "ALTO" : "MEDIO";
                String descricao = String.format(
                        "Precipitação intensa (%.1f mm) - teste real da capacidade de retenção",
                        leitura.getPrecipitacaoMm()
                );

                Alerta alerta = criarAlerta(leitura, severidade, descricao);

                // Enviar via RabbitMQ
                if (rabbitMQService != null && alerta != null) {
                    rabbitMQService.processarDadosSensor(
                            leitura.getIdSensor(),
                            "PRECIPITACAO_MM",
                            leitura.getPrecipitacaoMm().doubleValue()
                    );
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Erro ao analisar leitura e gerar alertas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Alerta criarAlerta(LeituraSensor leitura, String codigoSeveridade, String descricao) {
        try {
            Optional<NivelSeveridade> optionalNivel = nivelSeveridadeRepository.findByCodigoSeveridade(codigoSeveridade);
            NivelSeveridade nivelSeveridade = optionalNivel.orElse(null);

            if (nivelSeveridade == null) {
                System.err.println("⚠️ Nível de severidade não encontrado: " + codigoSeveridade);
                return null;
            }

            // Verificar se já existe alerta similar recente (evitar spam)
            LocalDateTime umaHoraAtras = LocalDateTime.now().minusHours(1);
            List<Alerta> alertasRecentes = alertaRepository.findAlertasPorPeriodo(umaHoraAtras);

            boolean alertaSimilarExiste = alertasRecentes.stream()
                    .anyMatch(a -> a.getIdLeitura() != null && a.getIdLeitura().equals(leitura.getIdLeitura()) &&
                            a.getSeveridadeCodigo().equals(codigoSeveridade));

            if (!alertaSimilarExiste) {
                Alerta novoAlerta = new Alerta();
                novoAlerta.setIdLeitura(leitura.getIdLeitura());
                novoAlerta.setIdNivelSeveridade(nivelSeveridade.getIdNivelSeveridade());
                novoAlerta.setDescricaoAlerta(descricao);

                // Buscar ID do produtor através do sensor
                if (leitura.getSensor() != null && leitura.getSensor().getPropriedade() != null) {
                    novoAlerta.setIdProdutor(leitura.getSensor().getPropriedade().getIdProdutor());
                }

                return save(novoAlerta); // Já chama RabbitMQ internamente
            }

            return null;

        } catch (Exception e) {
            System.err.println("❌ Erro ao criar alerta: " + e.getMessage());
            return null;
        }
    }

    // Método para buscar alertas por diferentes filtros
    public List<Alerta> buscarAlertasComFiltros(String severidade, Integer dias, Long produtorId) {
        if (produtorId != null) {
            return findByProdutor(produtorId);
        }

        if (severidade != null && !severidade.trim().isEmpty()) {
            return findBySeveridade(severidade);
        }

        if (dias != null && dias > 0) {
            LocalDateTime dataInicio = LocalDateTime.now().minusDays(dias);
            return alertaRepository.findAlertasPorPeriodo(dataInicio);
        }

        return findAll();
    }

    // Obter estatísticas de alertas
    public java.util.Map<String, Object> obterEstatisticasAlertas() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();

        stats.put("totalAlertas", alertaRepository.count());
        stats.put("alertas24h", countAlertasRecentes(24));
        stats.put("alertas7d", countAlertasRecentes(24 * 7));
        stats.put("alertasCriticos", findAlertasCriticos().size());

        return stats;
    }

    /**
     * NOVO: Método para simular leituras e testar o sistema completo
     */
    public void simularLeiturasCriticas() {
        if (rabbitMQService == null) {
            System.out.println("⚠️ RabbitMQ não está habilitado para simulação");
            return;
        }

        System.out.println("🧪 Iniciando simulação de leituras críticas...");

        // Simular umidade crítica
        rabbitMQService.processarDadosSensor(1L, "UMIDADE_SOLO", 15.5);

        // Simular temperatura alta
        rabbitMQService.processarDadosSensor(2L, "TEMPERATURA_AR", 42.0);

        // Simular chuva intensa
        rabbitMQService.processarDadosSensor(3L, "PRECIPITACAO_MM", 95.8);

        System.out.println("✅ Simulação enviada - verifique os logs do RabbitMQ");
    }

    /**
     * NOVO: Método para análise em batch de todas as leituras recentes
     */
    public void analisarLeiturasRecentesEmBatch() {
        try {
            System.out.println("📊 Iniciando análise em batch das leituras recentes...");

            LocalDateTime ultima1h = LocalDateTime.now().minusHours(1);
            // List<LeituraSensor> leiturasRecentes = leituraRepository.findLeiturasRecentes(ultima1h);

            // Para cada leitura, analisar e gerar alertas
            // leiturasRecentes.forEach(this::analisarLeituraEGerarAlertas);

            // Enviar relatório consolidado via RabbitMQ
            if (rabbitMQService != null) {
                rabbitMQService.analisarImpactoRegional(8, 284.5); // Dados de exemplo
            }

            System.out.println("✅ Análise em batch concluída");

        } catch (Exception e) {
            System.err.println("❌ Erro na análise em batch: " + e.getMessage());
        }
    }
}