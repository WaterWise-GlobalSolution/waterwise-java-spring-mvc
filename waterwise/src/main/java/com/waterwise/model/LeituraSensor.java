package com.waterwise.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "GS_WW_LEITURA_SENSOR")
public class LeituraSensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_LEITURA")
    private Long idLeitura;

    @Column(name = "ID_SENSOR", nullable = false)
    private Long idSensor;

    @Column(name = "TIMESTAMP_LEITURA")
    private LocalDateTime timestampLeitura;

    @Column(name = "UMIDADE_SOLO", precision = 5, scale = 2)
    private BigDecimal umidadeSolo;

    @Column(name = "TEMPERATURA_AR", precision = 4, scale = 1)
    private BigDecimal temperaturaAr;

    @Column(name = "PRECIPITACAO_MM", precision = 6, scale = 2)
    private BigDecimal precipitacaoMm;

    // Relacionamento
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SENSOR", insertable = false, updatable = false)
    private SensorIoT sensor;

    public LeituraSensor() {
        this.timestampLeitura = LocalDateTime.now();
    }

    // Getters e Setters (gerar no IDE)
    public Long getIdLeitura() { return idLeitura; }
    public void setIdLeitura(Long idLeitura) { this.idLeitura = idLeitura; }

    public Long getIdSensor() { return idSensor; }
    public void setIdSensor(Long idSensor) { this.idSensor = idSensor; }

    public LocalDateTime getTimestampLeitura() { return timestampLeitura; }
    public void setTimestampLeitura(LocalDateTime timestampLeitura) { this.timestampLeitura = timestampLeitura; }

    public BigDecimal getUmidadeSolo() { return umidadeSolo; }
    public void setUmidadeSolo(BigDecimal umidadeSolo) { this.umidadeSolo = umidadeSolo; }

    public BigDecimal getTemperaturaAr() { return temperaturaAr; }
    public void setTemperaturaAr(BigDecimal temperaturaAr) { this.temperaturaAr = temperaturaAr; }

    public BigDecimal getPrecipitacaoMm() { return precipitacaoMm; }
    public void setPrecipitacaoMm(BigDecimal precipitacaoMm) { this.precipitacaoMm = precipitacaoMm; }

    public BigDecimal getNivelAguaReservatorio() { return BigDecimal.ZERO; }
    public Integer getQualidadeSinal() { return 100; }

    public SensorIoT getSensor() { return sensor; }
    public void setSensor(SensorIoT sensor) { this.sensor = sensor; }
}
