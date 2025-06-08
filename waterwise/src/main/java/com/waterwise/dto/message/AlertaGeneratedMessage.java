package com.waterwise.dto.message;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AlertaGeneratedMessage implements Serializable {
    private Long idAlerta;
    private Long idPropriedade;
    private String nomePropriedade;
    private String tipoAlerta;
    private String severidade;
    private String descricao;
    private String nomeProdutor;
    private String emailProdutor;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataAlerta;

    // Construtores
    public AlertaGeneratedMessage() {
        this.dataAlerta = LocalDateTime.now();
    }

    public AlertaGeneratedMessage(Long idAlerta, Long idPropriedade,
                                  String nomePropriedade, String tipoAlerta,
                                  String severidade, String descricao,
                                  String nomeProdutor, String emailProdutor) {
        this();
        this.idAlerta = idAlerta;
        this.idPropriedade = idPropriedade;
        this.nomePropriedade = nomePropriedade;
        this.tipoAlerta = tipoAlerta;
        this.severidade = severidade;
        this.descricao = descricao;
        this.nomeProdutor = nomeProdutor;
        this.emailProdutor = emailProdutor;
    }

    // Getters e Setters
    public Long getIdAlerta() { return idAlerta; }
    public void setIdAlerta(Long idAlerta) { this.idAlerta = idAlerta; }

    public Long getIdPropriedade() { return idPropriedade; }
    public void setIdPropriedade(Long idPropriedade) { this.idPropriedade = idPropriedade; }

    public String getNomePropriedade() { return nomePropriedade; }
    public void setNomePropriedade(String nomePropriedade) { this.nomePropriedade = nomePropriedade; }

    public String getTipoAlerta() { return tipoAlerta; }
    public void setTipoAlerta(String tipoAlerta) { this.tipoAlerta = tipoAlerta; }

    public String getSeveridade() { return severidade; }
    public void setSeveridade(String severidade) { this.severidade = severidade; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getNomeProdutor() { return nomeProdutor; }
    public void setNomeProdutor(String nomeProdutor) { this.nomeProdutor = nomeProdutor; }

    public String getEmailProdutor() { return emailProdutor; }
    public void setEmailProdutor(String emailProdutor) { this.emailProdutor = emailProdutor; }

    public LocalDateTime getDataAlerta() { return dataAlerta; }
    public void setDataAlerta(LocalDateTime dataAlerta) { this.dataAlerta = dataAlerta; }

    @Override
    public String toString() {
        return "AlertaGeneratedMessage{" +
                "idAlerta=" + idAlerta +
                ", nomePropriedade='" + nomePropriedade + '\'' +
                ", tipoAlerta='" + tipoAlerta + '\'' +
                ", severidade='" + severidade + '\'' +
                ", dataAlerta=" + dataAlerta +
                '}';
    }
}