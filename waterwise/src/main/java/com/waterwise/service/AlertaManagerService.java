package com.waterwise.service;

import com.waterwise.model.Alerta;
import com.waterwise.model.NivelSeveridade;
import com.waterwise.repository.AlertaRepository;
import com.waterwise.repository.NivelSeveridadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@Transactional
public class AlertaManagerService {

    @Autowired
    private AlertaRepository alertaRepository;

    @Autowired
    private NivelSeveridadeRepository nivelSeveridadeRepository;

    /**
     * Exemplo de uso dos repositórios
     */
    public Map<String, Object> getDashboardAlertas() {
        Map<String, Object> dashboard = new HashMap<>();

        LocalDateTime ultima24h = LocalDateTime.now().minusHours(24);

        // Usar os repositórios
        List<Alerta> alertasCriticos = alertaRepository.findAlertasCriticos();
        List<Alerta> alertasRecentes = alertaRepository.findAlertasRecentes(ultima24h);
        long totalAlertas = alertaRepository.count();

        List<NivelSeveridade> niveisOrdenados = nivelSeveridadeRepository.findAllOrderedByPriority();

        // Estatísticas
        Object[] stats = alertaRepository.getEstatisticasDashboard(ultima24h);

        dashboard.put("alertasCriticos", alertasCriticos);
        dashboard.put("alertasRecentes", alertasRecentes);
        dashboard.put("totalAlertas", totalAlertas);
        dashboard.put("niveisDisponiveis", niveisOrdenados);
        dashboard.put("estatisticas", stats);

        return dashboard;
    }

    /**
     * Criar alerta automaticamente com nível apropriado
     */
    public Alerta criarAlertaAutomatico(Long leituraId, String tipoProblema, Object valor) {
        NivelSeveridade nivel = null;

        switch (tipoProblema) {
            case "UMIDADE_BAIXA":
                nivel = nivelSeveridadeRepository.findNivelParaUmidade((java.math.BigDecimal) valor)
                        .orElse(nivelSeveridadeRepository.findNivelPadrao().orElse(null));
                break;
            case "TEMPERATURA_ALTA":
                nivel = nivelSeveridadeRepository.findNivelParaTemperatura((java.math.BigDecimal) valor)
                        .orElse(nivelSeveridadeRepository.findNivelPadrao().orElse(null));
                break;
            case "PRECIPITACAO_INTENSA":
                nivel = nivelSeveridadeRepository.findNivelParaPrecipitacao((java.math.BigDecimal) valor)
                        .orElse(nivelSeveridadeRepository.findNivelPadrao().orElse(null));
                break;
        }

        if (nivel != null) {
            Alerta alerta = new Alerta();
            alerta.setIdLeitura(leituraId);
            alerta.setIdNivelSeveridade(nivel.getIdNivelSeveridade());
            alerta.setDescricaoAlerta(String.format("%s detectado: %s", tipoProblema, valor));

            return alertaRepository.save(alerta);
        }

        return null;
    }
}
