package com.waterwise.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "GS_WW_TIPO_SENSOR")
public class TipoSensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TIPO_SENSOR")
    private Long idTipoSensor;

    @Column(name = "NOME_TIPO", nullable = false, length = 50, unique = true)
    private String nomeTipo;

    @Column(name = "DESCRICAO", length = 200)
    private String descricao;

    @Column(name = "UNIDADE_MEDIDA", length = 20)
    private String unidadeMedida;

    @Column(name = "VALOR_MIN", precision = 10, scale = 2)
    private BigDecimal valorMin;

    @Column(name = "VALOR_MAX", precision = 10, scale = 2)
    private BigDecimal valorMax;

    // Getters e Setters
    public Long getIdTipoSensor() {
        return idTipoSensor;
    }

    public void setIdTipoSensor(Long idTipoSensor) {
        this.idTipoSensor = idTipoSensor;
    }

    public String getNomeTipo() {
        return nomeTipo;
    }

    public void setNomeTipo(String nomeTipo) {
        this.nomeTipo = nomeTipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public BigDecimal getValorMin() {
        return valorMin;
    }

    public void setValorMin(BigDecimal valorMin) {
        this.valorMin = valorMin;
    }

    public BigDecimal getValorMax() {
        return valorMax;
    }

    public void setValorMax(BigDecimal valorMax) {
        this.valorMax = valorMax;
    }

    public String getStatusAtivo() {
        return "S";
    }
}
