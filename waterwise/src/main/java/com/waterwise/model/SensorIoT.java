package com.waterwise.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "GS_WW_SENSOR_IOT")
public class SensorIoT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_SENSOR")
    private Long idSensor;

    @Column(name = "ID_PROPRIEDADE", nullable = false)
    private Long idPropriedade;

    @Column(name = "ID_TIPO_SENSOR", nullable = false)
    private Long idTipoSensor;

    @Column(name = "MODELO_DISPOSITIVO", length = 50)
    private String modeloDispositivo;

    @Column(name = "DATA_INSTALACAO")
    private LocalDateTime dataInstalacao;

    // Relacionamentos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PROPRIEDADE", insertable = false, updatable = false)
    private PropriedadeRural propriedade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_SENSOR", insertable = false, updatable = false)
    private TipoSensor tipoSensor;

    @OneToMany(mappedBy = "sensor", fetch = FetchType.LAZY)
    private List<LeituraSensor> leituras;

    // Construtores
    public SensorIoT() {
        this.dataInstalacao = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getIdSensor() { return idSensor; }
    public void setIdSensor(Long idSensor) { this.idSensor = idSensor; }

    public Long getIdPropriedade() { return idPropriedade; }
    public void setIdPropriedade(Long idPropriedade) { this.idPropriedade = idPropriedade; }

    public Long getIdTipoSensor() { return idTipoSensor; }
    public void setIdTipoSensor(Long idTipoSensor) { this.idTipoSensor = idTipoSensor; }

    public String getModeloDispositivo() { return modeloDispositivo; }
    public void setModeloDispositivo(String modeloDispositivo) { this.modeloDispositivo = modeloDispositivo; }

    public LocalDateTime getDataInstalacao() { return dataInstalacao; }
    public void setDataInstalacao(LocalDateTime dataInstalacao) { this.dataInstalacao = dataInstalacao; }

    public PropriedadeRural getPropriedade() { return propriedade; }
    public void setPropriedade(PropriedadeRural propriedade) { this.propriedade = propriedade; }

    public TipoSensor getTipoSensor() { return tipoSensor; }
    public void setTipoSensor(TipoSensor tipoSensor) { this.tipoSensor = tipoSensor; }

    public List<LeituraSensor> getLeituras() { return leituras; }
    public void setLeituras(List<LeituraSensor> leituras) { this.leituras = leituras; }

    // Métodos auxiliares
    public boolean isAtivo() { return true; }
    public String getStatusOperacional() { return "A"; }
    public String getStatusDescricao() { return "Ativo"; }
    public Integer getBateriaNivel() { return 100; }
    public String getBateriaStatusCss() { return "success"; }
    public String getMacAddress() { return "00:00:00:00:00:00"; }

}
