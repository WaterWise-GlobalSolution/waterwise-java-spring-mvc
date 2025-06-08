package com.waterwise.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Controller
@RequestMapping("/admin/notificacoes")
public class NotificacaoController {

    // 📋 LISTA EM MEMÓRIA PARA NOTIFICAÇÕES (simples para teste)
    private static final Queue<Map<String, Object>> notificacoes = new ConcurrentLinkedQueue<>();

    // 🎯 PÁGINA DE NOTIFICAÇÕES
    @GetMapping
    public String paginaNotificacoes(Model model) {
        List<Map<String, Object>> lista = new ArrayList<>(notificacoes);
        Collections.reverse(lista); // Mais recentes primeiro

        model.addAttribute("activeMenu", "notificacoes");
        model.addAttribute("notificacoes", lista);
        model.addAttribute("total", notificacoes.size());

        return "notificacoes/lista";
    }

    // 📡 API PARA BUSCAR NOTIFICAÇÕES (AJAX)
    @GetMapping("/api/lista")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> listarNotificacoes() {
        List<Map<String, Object>> lista = new ArrayList<>(notificacoes);
        Collections.reverse(lista); // Mais recentes primeiro

        Map<String, Object> response = new HashMap<>();
        response.put("notificacoes", lista);
        response.put("total", notificacoes.size());
        response.put("novas", contarNovas());

        // ✨ NOVAS ESTATÍSTICAS PARA .NET
        response.put("totalSensores", contarPorOrigem("DOTNET_SENSOR"));
        response.put("totalCriticas", contarPorTipo("DANGER"));

        return ResponseEntity.ok(response);
    }

    // ➕ ADICIONAR NOTIFICAÇÃO (USADO PELO RABBITMQ)
    public static void adicionarNotificacao(String tipo, String titulo, String mensagem, String origem) {
        Map<String, Object> notificacao = new HashMap<>();
        notificacao.put("id", UUID.randomUUID().toString());
        notificacao.put("tipo", tipo); // SUCCESS, WARNING, DANGER, INFO
        notificacao.put("titulo", titulo);
        notificacao.put("mensagem", mensagem);
        notificacao.put("origem", origem); // SENSOR, ALERTA, SISTEMA, DOTNET_SENSOR, DOTNET_API
        notificacao.put("timestamp", LocalDateTime.now());
        notificacao.put("lida", false);
        notificacao.put("timeFormatted", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM HH:mm")));

        notificacoes.offer(notificacao);

        // 🧹 LIMITAR A 100 NOTIFICAÇÕES
        if (notificacoes.size() > 100) {
            notificacoes.poll();
        }

        System.out.println("🔔 NOVA NOTIFICAÇÃO: " + titulo + " - " + mensagem);
    }

    // ✅ MARCAR COMO LIDA
    @PostMapping("/marcar-lida/{id}")
    @ResponseBody
    public ResponseEntity<String> marcarComoLida(@PathVariable String id) {
        notificacoes.stream()
                .filter(n -> id.equals(n.get("id")))
                .findFirst()
                .ifPresent(n -> n.put("lida", true));

        return ResponseEntity.ok("Marcada como lida");
    }

    // 🧹 LIMPAR TODAS
    @PostMapping("/limpar")
    @ResponseBody
    public ResponseEntity<String> limparTodas() {
        notificacoes.clear();
        return ResponseEntity.ok("Notificações limpas");
    }

    // 🔢 CONTAR NOVAS NOTIFICAÇÕES
    private long contarNovas() {
        return notificacoes.stream()
                .mapToLong(n -> (Boolean) n.get("lida") ? 0 : 1)
                .sum();
    }

    // 🔢 CONTAR POR ORIGEM
    private long contarPorOrigem(String origem) {
        return notificacoes.stream()
                .mapToLong(n -> origem.equals(n.get("origem")) ? 1 : 0)
                .sum();
    }

    // 🔢 CONTAR POR TIPO
    private long contarPorTipo(String tipo) {
        return notificacoes.stream()
                .mapToLong(n -> tipo.equals(n.get("tipo")) ? 1 : 0)
                .sum();
    }

    // 🧪 TESTE MANUAL DE NOTIFICAÇÃO
    @PostMapping("/teste")
    @ResponseBody
    public ResponseEntity<String> testeNotificacao() {
        adicionarNotificacao(
                "INFO",
                "🧪 Teste do Sistema",
                "Notificação de teste gerada manualmente",
                "TESTE");
        return ResponseEntity.ok("Notificação de teste criada!");
    }
}