package com.waterwise.dto.message;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PropriedadeCreatedMessage implements Serializable {
    private Long idPropriedade;
    private String nomePropriedade;
    private BigDecimal areaHectares;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String nomeProdutor;
    private String emailProdutor;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataCriacao;

    // Construtores
    public PropriedadeCreatedMessage() {
        this.dataCriacao = LocalDateTime.now();
    }

    public PropriedadeCreatedMessage(Long idPropriedade, String nomePropriedade,
                                     BigDecimal areaHectares, BigDecimal latitude,
                                     BigDecimal longitude, String nomeProdutor,
                                     String emailProdutor) {
        this();
        this.idPropriedade = idPropriedade;
        this.nomePropriedade = nomePropriedade;
        this.areaHectares = areaHectares;
        this.latitude = latitude;
        this.longitude = longitude;
        this.nomeProdutor = nomeProdutor;
        this.emailProdutor = emailProdutor;
    }

    // Getters e Setters
    public Long getIdPropriedade() {
        return idPropriedade;
    }

    public void setIdPropriedade(Long idPropriedade) {
        this.idPropriedade = idPropriedade;
    }

    public String getNomePropriedade() {
        return nomePropriedade;
    }

    public void setNomePropriedade(String nomePropriedade) {
        this.nomePropriedade = nomePropriedade;
    }

    public BigDecimal getAreaHectares() {
        return areaHectares;
    }

    public void setAreaHectares(BigDecimal areaHectares) {
        this.areaHectares = areaHectares;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getNomeProdutor() {
        return nomeProdutor;
    }

    public void setNomeProdutor(String nomeProdutor) {
        this.nomeProdutor = nomeProdutor;
    }

    public String getEmailProdutor() {
        return emailProdutor;
    }

    public void setEmailProdutor(String emailProdutor) {
        this.emailProdutor = emailProdutor;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    @Override
    public String toString() {
        return "PropriedadeCreatedMessage{" +
                "idPropriedade=" + idPropriedade +
                ", nomePropriedade='" + nomePropriedade + '\'' +
                ", areaHectares=" + areaHectares +
                ", nomeProdutor='" + nomeProdutor + '\'' +
                ", dataCriacao=" + dataCriacao +
                '}';
    }
}