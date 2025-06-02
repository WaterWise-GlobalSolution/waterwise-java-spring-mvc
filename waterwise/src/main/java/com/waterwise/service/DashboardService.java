package com.waterwise.service;

import com.waterwise.model.PropriedadeRural;
import com.waterwise.repository.PropriedadeRuralRepository;
import com.waterwise.repository.SensorIoTRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private PropriedadeRuralRepository propriedadeRepository;

    @Autowired
    private SensorIoTRepository sensorRepository;

    public Map<String, Object> obterEstatisticasDashboard() {
        Map<String, Object> stats = new HashMap<>();

        try {
            System.out.println("🔄 Iniciando coleta de estatísticas...");

            // Estatísticas básicas com tratamento de erro
            stats.put("totalPropriedades", safeLongQuery(() -> propriedadeRepository.countTotalPropriedades()));
            stats.put("propriedadesAtivas", safeLongQuery(() -> propriedadeRepository.count()));
            stats.put("sensoresAtivos", safeLongQuery(() -> sensorRepository.count()));

            // Capacidade de absorção com tratamento especial
            Double capacidadeMedia = safeDoubleQuery(() -> propriedadeRepository.calcularCapacidadeAbsorcaoMedia());
            stats.put("capacidadeAbsorcaoMedia", capacidadeMedia != null ? capacidadeMedia : 50.0);

            // Estatísticas avançadas
            stats.put("areaMonitoradaTotal", calcularAreaMonitoradaTotal());
            stats.put("impactoRetencaoTotal", calcularImpactoRetencaoTotal());
            stats.put("sensoresBateriaBaixa", 0L); // Sempre zero no sistema atual

            // CORREÇÃO: Adicionar região Mairiporã às estatísticas
            stats.put("regiaoMairipora", calcularPropriedadesMairipora());

            System.out.println("✅ Estatísticas carregadas com sucesso: " + stats);
            return stats;

        } catch (Exception e) {
            System.err.println("❌ Erro ao obter estatísticas do dashboard: " + e.getMessage());
            e.printStackTrace();

            // Retornar estatísticas padrão em caso de erro
            return criarEstatisticasPadrao();
        }
    }

    public List<PropriedadeRural> obterTopPropriedades(int limite) {
        try {
            System.out.println("🔄 Buscando top " + limite + " propriedades...");

            List<PropriedadeRural> propriedades = propriedadeRepository.findAll();

            // Se não tiver dados suficientes, retornar o que tem
            if (propriedades.size() <= limite) {
                System.out.println("📊 Retornando todas as " + propriedades.size() + " propriedades disponíveis");
                return propriedades;
            }

            // Tentar usar a query otimizada
            try {
                List<PropriedadeRural> topPropriedades = propriedadeRepository.findTop10ByCapacidadeAbsorcao();
                int tamanhoRetorno = Math.min(limite, topPropriedades.size());
                System.out.println("📊 Retornando top " + tamanhoRetorno + " propriedades otimizadas");
                return topPropriedades.subList(0, tamanhoRetorno);
            } catch (Exception e) {
                System.err.println("⚠️ Erro na query otimizada, usando lista simples: " + e.getMessage());
                int tamanhoRetorno = Math.min(limite, propriedades.size());
                return propriedades.subList(0, tamanhoRetorno);
            }

        } catch (Exception e) {
            System.err.println("❌ Erro ao obter top propriedades: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    private BigDecimal calcularAreaMonitoradaTotal() {
        try {
            System.out.println("🔄 Calculando área total monitorada...");
            List<PropriedadeRural> propriedades = propriedadeRepository.findAll();
            BigDecimal areaTotal = propriedades.stream()
                    .map(PropriedadeRural::getAreaHectares)
                    .filter(area -> area != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            System.out.println("📊 Área total calculada: " + areaTotal + " hectares");
            return areaTotal;
        } catch (Exception e) {
            System.err.println("❌ Erro ao calcular área total: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal calcularImpactoRetencaoTotal() {
        try {
            System.out.println("🔄 Calculando impacto de retenção...");
            List<PropriedadeRural> propriedades = propriedadeRepository.findAll();
            BigDecimal impactoTotal = propriedades.stream()
                    .filter(p -> p.getNivelDegradacao() != null && p.getAreaHectares() != null)
                    .map(p -> {
                        try {
                            BigDecimal area = p.getAreaHectares();
                            BigDecimal capacidade = p.getNivelDegradacao().getCapacidadeAbsorcaoPercentual();
                            if (area != null && capacidade != null) {
                                return area.multiply(capacidade).multiply(new BigDecimal("1000"));
                            }
                        } catch (Exception e) {
                            System.err.println("⚠️ Erro ao calcular impacto para propriedade " + p.getIdPropriedade() + ": " + e.getMessage());
                        }
                        return BigDecimal.ZERO;
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            System.out.println("📊 Impacto total calculado: " + impactoTotal + " L/h");
            return impactoTotal;
        } catch (Exception e) {
            System.err.println("❌ Erro ao calcular impacto de retenção: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    // NOVO MÉTODO: Calcular propriedades da região de Mairiporã
    private Long calcularPropriedadesMairipora() {
        try {
            System.out.println("🔄 Calculando propriedades da região de Mairiporã...");

            // Coordenadas aproximadas de Mairiporã-SP
            // Latitude: -23.318 a -23.283
            // Longitude: -46.587 a -46.540
            List<PropriedadeRural> propriedades = propriedadeRepository.findAll();

            long countMairipora = propriedades.stream()
                    .filter(p -> p.getLatitude() != null && p.getLongitude() != null)
                    .filter(p -> {
                        double lat = p.getLatitude().doubleValue();
                        double lng = p.getLongitude().doubleValue();
                        // Verifica se está dentro da região aproximada de Mairiporã
                        return lat >= -23.318 && lat <= -23.283 &&
                                lng >= -46.587 && lng <= -46.540;
                    })
                    .count();

            System.out.println("📊 Propriedades em Mairiporã: " + countMairipora);
            return countMairipora;
        } catch (Exception e) {
            System.err.println("❌ Erro ao calcular propriedades de Mairiporã: " + e.getMessage());
            return 0L;
        }
    }

    // Métodos auxiliares para executar queries com segurança
    private Long safeLongQuery(java.util.function.Supplier<Long> query) {
        try {
            Long result = query.get();
            return result != null ? result : 0L;
        } catch (Exception e) {
            System.err.println("❌ Erro em query Long: " + e.getMessage());
            return 0L;
        }
    }

    private Double safeDoubleQuery(java.util.function.Supplier<Double> query) {
        try {
            return query.get();
        } catch (Exception e) {
            System.err.println("❌ Erro em query Double: " + e.getMessage());
            return null;
        }
    }

    private Map<String, Object> criarEstatisticasPadrao() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPropriedades", 0L);
        stats.put("propriedadesAtivas", 0L);
        stats.put("sensoresAtivos", 0L);
        stats.put("capacidadeAbsorcaoMedia", 50.0);
        stats.put("areaMonitoradaTotal", BigDecimal.ZERO);
        stats.put("impactoRetencaoTotal", BigDecimal.ZERO);
        stats.put("sensoresBateriaBaixa", 0L);
        stats.put("regiaoMairipora", 0L); // CORREÇÃO: Adicionado este campo

        System.out.println("⚠️ Usando estatísticas padrão devido a erros");
        return stats;
    }

    // NOVO MÉTODO: Para debug - listar todas as estatísticas
    public void debugEstatisticas() {
        try {
            System.out.println("\n=== DEBUG ESTATÍSTICAS ===");
            Map<String, Object> stats = obterEstatisticasDashboard();
            stats.forEach((key, value) ->
                    System.out.println(key + ": " + value + " (" + (value != null ? value.getClass().getSimpleName() : "null") + ")")
            );
            System.out.println("========================\n");
        } catch (Exception e) {
            System.err.println("Erro no debug: " + e.getMessage());
        }
    }
}