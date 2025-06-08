package com.waterwise.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "GS_WW_PROPRIEDADE_RURAL")
public class PropriedadeRural {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PROPRIEDADE")
    private Long idPropriedade;

    @NotBlank(message = "{propriedade.nome.required}")
    @Size(max = 100, message = "{propriedade.nome.size}")
    @Column(name = "NOME_PROPRIEDADE", nullable = false, length = 100)
    private String nomePropriedade;

    @NotNull(message = "{propriedade.latitude.required}")
    @DecimalMin(value = "-90.0", message = "{propriedade.latitude.min}")
    @DecimalMax(value = "90.0", message = "{propriedade.latitude.max}")
    @Column(name = "LATITUDE", precision = 10, scale = 7, nullable = false)
    private BigDecimal latitude;

    @NotNull(message = "{propriedade.longitude.required}")
    @DecimalMin(value = "-180.0", message = "{propriedade.longitude.min}")
    @DecimalMax(value = "180.0", message = "{propriedade.longitude.max}")
    @Column(name = "LONGITUDE", precision = 10, scale = 7, nullable = false)
    private BigDecimal longitude;

    @NotNull(message = "{propriedade.area.required}")
    @DecimalMin(value = "0.1", message = "{propriedade.area.min}")
    @Column(name = "AREA_HECTARES", precision = 10, scale = 2, nullable = false)
    private BigDecimal areaHectares;

    // CORREÇÃO: Remover insertable = false, updatable = false
    @Column(name = "ID_PRODUTOR", nullable = false)
    private Long idProdutor;

    @Column(name = "ID_NIVEL_DEGRADACAO", nullable = false)
    private Long idNivelDegradacao;

    @Column(name = "DATA_CADASTRO")
    private LocalDateTime dataCadastro;

    // Se você quiser manter os relacionamentos para consultas, use LAZY e remova o nullable = false do @JoinColumn
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PRODUTOR", insertable = false, updatable = false)
    private ProdutorRural produtor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_NIVEL_DEGRADACAO", insertable = false, updatable = false)
    private NivelDegradacaoSolo nivelDegradacao;

    @OneToMany(mappedBy = "propriedade", fetch = FetchType.LAZY)
    private List<SensorIoT> sensores;

    // Construtores
    public PropriedadeRural() {
        this.dataCadastro = LocalDateTime.now();
    }

    public PropriedadeRural(String nomePropriedade, BigDecimal latitude, BigDecimal longitude, BigDecimal areaHectares) {
        this();
        this.nomePropriedade = nomePropriedade;
        this.latitude = latitude;
        this.longitude = longitude;
        this.areaHectares = areaHectares;
    }

    // Getters e Setters (mantém todos os originais)
    public Long getIdPropriedade() { return idPropriedade; }
    public void setIdPropriedade(Long idPropriedade) { this.idPropriedade = idPropriedade; }

    public String getNomePropriedade() { return nomePropriedade; }
    public void setNomePropriedade(String nomePropriedade) { this.nomePropriedade = nomePropriedade; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public BigDecimal getAreaHectares() { return areaHectares; }
    public void setAreaHectares(BigDecimal areaHectares) { this.areaHectares = areaHectares; }

    public String getTipoSolo() {
        return "Não informado"; // Campo removido do novo schema
    }

    public Long getIdProdutor() { return idProdutor; }
    public void setIdProdutor(Long idProdutor) { this.idProdutor = idProdutor; }

    public Long getIdNivelDegradacao() { return idNivelDegradacao; }
    public void setIdNivelDegradacao(Long idNivelDegradacao) { this.idNivelDegradacao = idNivelDegradacao; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    public ProdutorRural getProdutor() { return produtor; }
    public void setProdutor(ProdutorRural produtor) { this.produtor = produtor; }

    public NivelDegradacaoSolo getNivelDegradacao() { return nivelDegradacao; }
    public void setNivelDegradacao(NivelDegradacaoSolo nivelDegradacao) { this.nivelDegradacao = nivelDegradacao; }

    public List<SensorIoT> getSensores() { return sensores; }
    public void setSensores(List<SensorIoT> sensores) { this.sensores = sensores; }

    // Métodos auxiliares
    public int getTotalSensores() {
        return sensores != null ? sensores.size() : 0;
    }

    public int getSensoresAtivos() {
        return sensores != null ?
                (int) sensores.stream().filter(s -> "A".equals(s.getStatusOperacional())).count() : 0;
    }

    public String getCapacidadeAbsorcaoDescricao() {
        if (nivelDegradacao != null && nivelDegradacao.getCapacidadeAbsorcaoPercentual() != null) {
            return String.format("%.1f%%", nivelDegradacao.getCapacidadeAbsorcaoPercentual());
        }
        return "N/A";
    }
}