package com.waterwise.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "GS_WW_NIVEL_SEVERIDADE")
public class NivelSeveridade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_NIVEL_SEVERIDADE")
    private Long idNivelSeveridade;

    @NotBlank(message = "Código de severidade é obrigatório")
    @Size(max = 20, message = "Código deve ter no máximo 20 caracteres")
    @Column(name = "CODIGO_SEVERIDADE", nullable = false, length = 20, unique = true)
    private String codigoSeveridade;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 100, message = "Descrição deve ter no máximo 100 caracteres")
    @Column(name = "DESCRICAO_SEVERIDADE", nullable = false, length = 100)
    private String descricaoSeveridade;

    @Size(max = 500, message = "Ações recomendadas devem ter no máximo 500 caracteres")
    @Column(name = "ACOES_RECOMENDADAS", length = 500)
    private String acoesRecomendadas;

    // Construtores
    public NivelSeveridade() {}

    public NivelSeveridade(String codigoSeveridade, String descricaoSeveridade, String acoesRecomendadas) {
        this.codigoSeveridade = codigoSeveridade;
        this.descricaoSeveridade = descricaoSeveridade;
        this.acoesRecomendadas = acoesRecomendadas;
    }

    // Getters e Setters
    public Long getIdNivelSeveridade() { return idNivelSeveridade; }
    public void setIdNivelSeveridade(Long idNivelSeveridade) { this.idNivelSeveridade = idNivelSeveridade; }

    public String getCodigoSeveridade() { return codigoSeveridade; }
    public void setCodigoSeveridade(String codigoSeveridade) { this.codigoSeveridade = codigoSeveridade; }

    public String getDescricaoSeveridade() { return descricaoSeveridade; }
    public void setDescricaoSeveridade(String descricaoSeveridade) { this.descricaoSeveridade = descricaoSeveridade; }

    public String getAcoesRecomendadas() { return acoesRecomendadas; }
    public void setAcoesRecomendadas(String acoesRecomendadas) { this.acoesRecomendadas = acoesRecomendadas; }

    // Métodos auxiliares
    public String getCorIndicadora() {
        switch (codigoSeveridade != null ? codigoSeveridade.toUpperCase() : "") {
            case "BAIXO": return "#28a745"; // Verde
            case "MEDIO": return "#ffc107"; // Amarelo
            case "ALTO": return "#dc3545"; // Vermelho
            case "CRITICO": return "#6c757d"; // Cinza escuro
            default: return "#6c757d";
        }
    }

    public String getCssClass() {
        switch (codigoSeveridade != null ? codigoSeveridade.toUpperCase() : "") {
            case "BAIXO": return "success";
            case "MEDIO": return "warning";
            case "ALTO": return "danger";
            case "CRITICO": return "dark";
            default: return "secondary";
        }
    }

    public String getIcone() {
        switch (codigoSeveridade != null ? codigoSeveridade.toUpperCase() : "") {
            case "BAIXO": return "bi-check-circle";
            case "MEDIO": return "bi-exclamation-triangle";
            case "ALTO": return "bi-exclamation-circle";
            case "CRITICO": return "bi-x-circle";
            default: return "bi-question-circle";
        }
    }

    public int getPrioridade() {
        switch (codigoSeveridade != null ? codigoSeveridade.toUpperCase() : "") {
            case "BAIXO": return 1;
            case "MEDIO": return 2;
            case "ALTO": return 3;
            case "CRITICO": return 4;
            default: return 0;
        }
    }

    public boolean requiresImmediateAction() {
        return "CRITICO".equals(codigoSeveridade) || "ALTO".equals(codigoSeveridade);
    }

    @Override
    public String toString() {
        return codigoSeveridade + " - " + descricaoSeveridade;
    }
}
