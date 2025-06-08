package com.waterwise.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "GS_WW_ALERTA")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ALERTA")
    private Long idAlerta;

    @NotNull
    @Column(name = "ID_PRODUTOR", nullable = false)
    private Long idProdutor;

    @NotNull
    @Column(name = "ID_LEITURA", nullable = false)
    private Long idLeitura;

    @NotNull
    @Column(name = "ID_NIVEL_SEVERIDADE", nullable = false)
    private Long idNivelSeveridade;

    @Column(name = "TIMESTAMP_ALERTA")
    private LocalDateTime timestampAlerta;

    @Size(max = 500, message = "Descrição do alerta deve ter no máximo 500 caracteres")
    @Column(name = "DESCRICAO_ALERTA", length = 500)
    private String descricaoAlerta;

    // Relacionamentos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PRODUTOR", insertable = false, updatable = false)
    private ProdutorRural produtor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_LEITURA", insertable = false, updatable = false)
    private LeituraSensor leitura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_NIVEL_SEVERIDADE", insertable = false, updatable = false)
    private NivelSeveridade nivelSeveridade;

    // Construtores
    public Alerta() {
        this.timestampAlerta = LocalDateTime.now();
    }

    public Alerta(Long idProdutor, Long idLeitura, Long idNivelSeveridade, String descricaoAlerta) {
        this();
        this.idProdutor = idProdutor;
        this.idLeitura = idLeitura;
        this.idNivelSeveridade = idNivelSeveridade;
        this.descricaoAlerta = descricaoAlerta;
    }

    // Getters e Setters
    public Long getIdAlerta() { return idAlerta; }
    public void setIdAlerta(Long idAlerta) { this.idAlerta = idAlerta; }

    public Long getIdProdutor() { return idProdutor; }
    public void setIdProdutor(Long idProdutor) { this.idProdutor = idProdutor; }

    public Long getIdLeitura() { return idLeitura; }
    public void setIdLeitura(Long idLeitura) { this.idLeitura = idLeitura; }

    public Long getIdNivelSeveridade() { return idNivelSeveridade; }
    public void setIdNivelSeveridade(Long idNivelSeveridade) { this.idNivelSeveridade = idNivelSeveridade; }

    public LocalDateTime getTimestampAlerta() { return timestampAlerta; }
    public void setTimestampAlerta(LocalDateTime timestampAlerta) { this.timestampAlerta = timestampAlerta; }

    public String getDescricaoAlerta() { return descricaoAlerta; }
    public void setDescricaoAlerta(String descricaoAlerta) { this.descricaoAlerta = descricaoAlerta; }

    public ProdutorRural getProdutor() { return produtor; }
    public void setProdutor(ProdutorRural produtor) { this.produtor = produtor; }

    public LeituraSensor getLeitura() { return leitura; }
    public void setLeitura(LeituraSensor leitura) { this.leitura = leitura; }

    public NivelSeveridade getNivelSeveridade() { return nivelSeveridade; }
    public void setNivelSeveridade(NivelSeveridade nivelSeveridade) { this.nivelSeveridade = nivelSeveridade; }

    // Métodos auxiliares
    public String getSeveridadeCodigo() {
        return nivelSeveridade != null ? nivelSeveridade.getCodigoSeveridade() : "INDEFINIDO";
    }

    public String getSeveridadeDescricao() {
        return nivelSeveridade != null ? nivelSeveridade.getDescricaoSeveridade() : "Nível não definido";
    }

    public String getSeveridadeCss() {
        if (nivelSeveridade == null) return "secondary";

        String codigo = nivelSeveridade.getCodigoSeveridade();
        switch (codigo.toUpperCase()) {
            case "BAIXO": return "success";
            case "MEDIO": return "warning";
            case "ALTO": return "danger";
            case "CRITICO": return "dark";
            default: return "secondary";
        }
    }

    public boolean isCritico() {
        return nivelSeveridade != null && "CRITICO".equals(nivelSeveridade.getCodigoSeveridade());
    }

    public boolean isRecente() {
        if (timestampAlerta == null) return false;
        return timestampAlerta.isAfter(LocalDateTime.now().minusHours(24));
    }

    public String getTempoDecorrido() {
        if (timestampAlerta == null) return "Não informado";

        LocalDateTime agora = LocalDateTime.now();
        long horas = java.time.Duration.between(timestampAlerta, agora).toHours();

        if (horas < 1) {
            long minutos = java.time.Duration.between(timestampAlerta, agora).toMinutes();
            return minutos + " minuto(s) atrás";
        } else if (horas < 24) {
            return horas + " hora(s) atrás";
        } else {
            long dias = java.time.Duration.between(timestampAlerta, agora).toDays();
            return dias + " dia(s) atrás";
        }
    }
}
