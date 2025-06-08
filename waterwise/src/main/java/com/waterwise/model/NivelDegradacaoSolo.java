package com.waterwise.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "GS_WW_NIVEL_DEGRADACAO_SOLO")
public class NivelDegradacaoSolo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_NIVEL_DEGRADACAO")
    private Long idNivelDegradacao;

    @Column(name = "CODIGO_DEGRADACAO", nullable = false, length = 20, unique = true)
    private String codigoDegradacao;

    @Column(name = "DESCRICAO_DEGRADACAO", nullable = false, length = 150)
    private String descricaoDegradacao;

    @Column(name = "NIVEL_NUMERICO", nullable = false)
    private Integer nivelNumerico;

    @Column(name = "ACOES_CORRETIVAS", length = 500)
    private String acoesCorretivas;


    // Getters e Setters
    public Long getIdNivelDegradacao() { return idNivelDegradacao; }
    public void setIdNivelDegradacao(Long idNivelDegradacao) { this.idNivelDegradacao = idNivelDegradacao; }

    public String getCodigoDegradacao() { return codigoDegradacao; }
    public void setCodigoDegradacao(String codigoDegradacao) { this.codigoDegradacao = codigoDegradacao; }

    public String getDescricaoDegradacao() { return descricaoDegradacao; }
    public void setDescricaoDegradacao(String descricaoDegradacao) { this.descricaoDegradacao = descricaoDegradacao; }

    public Integer getNivelNumerico() { return nivelNumerico; }
    public void setNivelNumerico(Integer nivelNumerico) { this.nivelNumerico = nivelNumerico; }

    public String getAcoesCorretivas() { return acoesCorretivas; }
    public void setAcoesCorretivas(String acoesCorretivas) { this.acoesCorretivas = acoesCorretivas; }

    // Método para calcular capacidade baseada no nível
    public BigDecimal getCapacidadeAbsorcaoPercentual() {
        switch (nivelNumerico != null ? nivelNumerico : 3) {
            case 1: return new BigDecimal("85.0"); // Excelente
            case 2: return new BigDecimal("70.0"); // Bom
            case 3: return new BigDecimal("50.0"); // Moderado
            case 4: return new BigDecimal("30.0"); // Degradado
            case 5: return new BigDecimal("15.0"); // Crítico
            default: return new BigDecimal("50.0");
        }
    }
}