package com.waterwise.controller;

import com.waterwise.service.DashboardService;
import com.waterwise.service.PropriedadeRuralService;
import com.waterwise.service.RelatorioIAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class DashboardController {

    @Autowired(required = false)
    private DashboardService dashboardService;

    @Autowired(required = false)
    private PropriedadeRuralService propriedadeService;

    @Autowired(required = false)
    private RelatorioIAService relatorioIAService;

    @GetMapping({ "/", "/dashboard" })
    public String dashboard(Model model) {
        System.out.println("🚀 === INICIANDO DASHBOARD === 🚀");

        try {
            model.addAttribute("activeMenu", "dashboard");

            // Obter estatísticas
            Map<String, Object> stats;
            if (dashboardService != null) {
                try {
                    stats = dashboardService.obterEstatisticasDashboard();
                    System.out.println("✅ Estatísticas obtidas com sucesso");
                    stats.forEach((key, value) -> System.out.println("  📊 " + key + ": " + value));
                } catch (Exception e) {
                    System.err.println("❌ Erro ao obter estatísticas: " + e.getMessage());
                    stats = criarEstatisticasPadrao();
                }
            } else {
                System.err.println("⚠️ DashboardService não disponível, usando dados padrão");
                stats = criarEstatisticasPadrao();
            }

            model.addAttribute("estatisticas", stats);

            // Obter propriedades
            if (propriedadeService != null) {
                try {
                    var propriedades = propriedadeService.findTop5ByCapacidadeAbsorcao();
                    model.addAttribute("propriedades", propriedades);
                    System.out.println("✅ " + propriedades.size() + " propriedades carregadas");
                } catch (Exception e) {
                    System.err.println("❌ Erro ao obter propriedades: " + e.getMessage());
                    model.addAttribute("propriedades", Collections.emptyList());
                }
            } else {
                System.err.println("⚠️ PropriedadeRuralService não disponível");
                model.addAttribute("propriedades", Collections.emptyList());
            }

            // Verificar status da IA
            boolean iaDisponivel = relatorioIAService != null;
            model.addAttribute("iaDisponivel", iaDisponivel);

            if (iaDisponivel) {
                System.out.println("🤖 RelatorioIAService disponível");
            } else {
                System.out.println("⚠️ RelatorioIAService não disponível");
            }

            System.out.println("🎉 Dashboard carregado com sucesso!");
            return "dashboard/index";

        } catch (Exception e) {
            System.err.println("💥 ERRO CRÍTICO no DashboardController: " + e.getMessage());
            e.printStackTrace();

            // Dados mínimos para evitar erro no template
            model.addAttribute("estatisticas", criarEstatisticasPadrao());
            model.addAttribute("propriedades", Collections.emptyList());
            model.addAttribute("iaDisponivel", false);
            model.addAttribute("erro", "Erro ao carregar dashboard: " + e.getMessage());

            return "dashboard/index";
        }
    }

    @GetMapping("/relatorio-ia")
    public String relatorioIA(@RequestParam(required = false) Long propriedadeId, Model model) {
        System.out.println("🤖 Carregando relatório IA...");

        try {
            model.addAttribute("activeMenu", "relatorio-ia");

            // Verificar se IA está disponível
            if (relatorioIAService == null) {
                model.addAttribute("erro", "Serviço de IA não está disponível. Verifique se o Ollama está rodando.");
                model.addAttribute("propriedades", Collections.emptyList());
                return "dashboard/relatorio-ia";
            }

            if (propriedadeId != null && propriedadeService != null) {
                System.out.println("📊 Gerando relatório para propriedade ID: " + propriedadeId);

                try {
                    String relatorio = relatorioIAService.gerarRelatorioImpacto(propriedadeId);
                    model.addAttribute("relatorioIA", relatorio);
                    model.addAttribute("propriedade", propriedadeService.findById(propriedadeId));
                    model.addAttribute("propriedadeId", propriedadeId);
                    System.out.println("✅ Relatório IA gerado com sucesso");
                } catch (Exception e) {
                    System.err.println("❌ Erro ao gerar relatório IA: " + e.getMessage());
                    model.addAttribute("erro", "Erro ao gerar relatório de IA: " + e.getMessage() +
                            "\n\nVerifique se o Ollama está rodando com: ollama serve");
                }
            }

            if (propriedadeService != null) {
                model.addAttribute("propriedades", propriedadeService.findAll());
            } else {
                model.addAttribute("propriedades", Collections.emptyList());
            }

            System.out.println("✅ Relatório IA carregado com sucesso");
            return "dashboard/relatorio-ia";

        } catch (Exception e) {
            System.err.println("❌ Erro no relatório IA: " + e.getMessage());
            model.addAttribute("erro", "Erro ao gerar relatório: " + e.getMessage());
            model.addAttribute("propriedades", Collections.emptyList());
            return "dashboard/relatorio-ia";
        }
    }

    // NOVO: Análise Regional com IA
    @GetMapping("/relatorio-regional")
    public String relatorioRegional(Model model) {
        System.out.println("🌍 Carregando análise regional...");

        try {
            if (relatorioIAService == null) {
                model.addAttribute("erro", "Serviço de IA não está disponível. Verifique se o Ollama está rodando.");
                model.addAttribute("estatisticas", criarEstatisticasPadrao());
                return "dashboard/relatorio-regional";
            }
            model.addAttribute("activeMenu", "mairipora");

            // Obter estatísticas regionais
            Map<String, Object> stats = dashboardService != null ? dashboardService.obterEstatisticasDashboard()
                    : criarEstatisticasPadrao();
            model.addAttribute("estatisticas", stats);

            try {
                String analiseRegional = relatorioIAService.analisarTendenciasRegionais();
                model.addAttribute("analiseRegional", analiseRegional);
                System.out.println("✅ Análise regional gerada com sucesso");
            } catch (Exception e) {
                System.err.println("❌ Erro ao gerar análise regional: " + e.getMessage());
                model.addAttribute("erro", "Erro ao gerar análise regional: " + e.getMessage() +
                        "\n\nVerifique se o Ollama está rodando com: ollama serve");
            }

            return "dashboard/relatorio-regional";

        } catch (Exception e) {
            System.err.println("❌ Erro na análise regional: " + e.getMessage());
            model.addAttribute("erro", "Erro ao carregar análise regional: " + e.getMessage());
            model.addAttribute("estatisticas", criarEstatisticasPadrao());
            return "dashboard/relatorio-regional";
        }
    }

    // NOVO: Endpoint de teste para verificar Ollama
    @GetMapping("/test-ollama")
    @ResponseBody
    public String testOllama() {
        System.out.println("🧪 Testando integração com Ollama...");

        try {
            if (relatorioIAService == null) {
                return """
                        ❌ TESTE OLLAMA - FALHOU

                        Motivo: RelatorioIAService não está disponível

                        📋 VERIFICAÇÕES:
                        • Verifique se a dependência spring-ai-ollama-spring-boot-starter está no pom.xml
                        • Verifique as configurações no application.properties
                        • Reinicie a aplicação

                        🔧 CONFIGURAÇÃO NECESSÁRIA:
                        spring.ai.ollama.base-url=http://localhost:11434
                        spring.ai.ollama.chat.options.model=gemma:2b
                        """;
            }

            String resultado = relatorioIAService.gerarRecomendacoesPrioritarias();
            System.out.println("✅ Teste Ollama realizado com sucesso");

            return """
                    🤖 TESTE OLLAMA GEMMA:2B - SUCESSO! ✅

                    Status: Funcionando corretamente
                    Modelo: gemma:2b
                    Endpoint: http://localhost:11434

                    📊 RESULTADO DO TESTE:
                    ────────────────────────────────────
                    """ + resultado;

        } catch (Exception e) {
            System.err.println("❌ Erro no teste Ollama: " + e.getMessage());

            return String.format("""
                    ❌ TESTE OLLAMA - FALHOU

                    Erro: %s
                    Classe: %s

                    📋 VERIFICAÇÕES NECESSÁRIAS:
                    • Ollama instalado e rodando: ollama serve
                    • Modelo baixado: ollama pull gemma:2b
                    • Verificar modelos: ollama list
                    • Testar modelo: ollama run gemma:2b "teste"

                    🔧 COMANDOS ÚTEIS:
                    1. ollama serve (iniciar Ollama)
                    2. ollama pull gemma:2b (baixar modelo)
                    3. curl http://localhost:11434/api/tags (verificar API)

                    📝 LOGS: Verifique os logs da aplicação para mais detalhes
                    """, e.getMessage(), e.getClass().getSimpleName());
        }
    }

    // NOVO: Status da IA para o frontend
    @GetMapping("/status-ia")
    @ResponseBody
    public Map<String, Object> statusIA() {
        Map<String, Object> status = new HashMap<>();

        try {
            boolean iaDisponivel = relatorioIAService != null;
            status.put("disponivel", iaDisponivel);
            status.put("servico", "Ollama Gemma:2b");
            status.put("endpoint", "http://localhost:11434");

            if (iaDisponivel) {
                try {
                    // Teste rápido
                    relatorioIAService.gerarRecomendacoesPrioritarias();
                    status.put("status", "online");
                    status.put("mensagem", "IA funcionando corretamente");
                } catch (Exception e) {
                    status.put("status", "erro");
                    status.put("mensagem", "Erro: " + e.getMessage());
                }
            } else {
                status.put("status", "indisponivel");
                status.put("mensagem", "Serviço de IA não foi carregado");
            }

        } catch (Exception e) {
            status.put("disponivel", false);
            status.put("status", "erro");
            status.put("mensagem", "Erro ao verificar status: " + e.getMessage());
        }

        return status;
    }

    // NOVO: Limpar cache e recarregar IA
    @GetMapping("/reload-ia")
    @ResponseBody
    public String reloadIA() {
        try {
            if (relatorioIAService != null) {
                // Teste de conectividade
                String teste = relatorioIAService.gerarRecomendacoesPrioritarias();
                return "✅ IA recarregada com sucesso!\n\nTeste: " + teste.substring(0, Math.min(200, teste.length()))
                        + "...";
            } else {
                return "❌ Serviço de IA não está disponível. Reinicie a aplicação.";
            }
        } catch (Exception e) {
            return "❌ Erro ao recarregar IA: " + e.getMessage();
        }
    }

    private Map<String, Object> criarEstatisticasPadrao() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPropriedades", 0L);
        stats.put("propriedadesAtivas", 0L);
        stats.put("sensoresAtivos", 0L);
        stats.put("capacidadeAbsorcaoMedia", 0.0);
        stats.put("areaMonitoradaTotal", java.math.BigDecimal.ZERO);
        stats.put("impactoRetencaoTotal", java.math.BigDecimal.ZERO);
        stats.put("sensoresBateriaBaixa", 0L);
        stats.put("regiaoMairipora", 0L);

        System.out.println("⚠️ Usando estatísticas padrão");
        return stats;
    }
}