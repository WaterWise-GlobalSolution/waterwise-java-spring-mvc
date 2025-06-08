package com.waterwise.service;

import com.waterwise.model.*;
import com.waterwise.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PropriedadeRuralService {

    @Autowired
    private PropriedadeRuralRepository propriedadeRepository;

    @Autowired
    private SensorIoTRepository sensorIoTRepository;

    @Autowired
    private NivelDegradacaoSoloRepository nivelDegradacaoRepository;

    @Autowired
    private ProdutorRuralRepository produtorRepository;
    @Autowired
    private LeituraSensorRepository leituraSensorRepository;

    // ✅ SUBSTITUIR O MÉTODO EXISTENTE
    public List<PropriedadeRural> findAll() {
        return propriedadeRepository.findAllWithProdutor(); // ← Mudança aqui
    }

    // ✅ OPCIONAL: Adicionar método específico também
    public List<PropriedadeRural> findAllWithProdutor() {
        return propriedadeRepository.findAllWithProdutor();
    }

    public PropriedadeRural findById(Long id) {
        return propriedadeRepository.findById(id).orElse(null);
    }

    public PropriedadeRural findByIdWithSensores(Long id) {
        Optional<PropriedadeRural> propriedade = propriedadeRepository.findByIdWithSensores(id);
        return propriedade.orElse(null);
    }

    public List<PropriedadeRural> findByNome(String nome) {
        return propriedadeRepository.findByNomePropriedadeContainingIgnoreCase(nome);
    }

    public List<PropriedadeRural> findByProdutor(Long idProdutor) {
        return propriedadeRepository.findByIdProdutor(idProdutor);
    }

    public PropriedadeRural save(PropriedadeRural propriedade) {
        // Debug para verificar os valores recebidos
        System.out.println("Salvando propriedade:");
        System.out.println("ID Produtor: " + propriedade.getIdProdutor());
        System.out.println("ID Nível Degradação: " + propriedade.getIdNivelDegradacao());

        // Validar se o produtor existe
        if (propriedade.getIdProdutor() == null) {
            throw new IllegalArgumentException("ID do produtor é obrigatório");
        }

        // Verificar se o produtor existe no banco
        if (!produtorRepository.existsById(propriedade.getIdProdutor())) {
            throw new IllegalArgumentException("Produtor não encontrado com ID: " + propriedade.getIdProdutor());
        }

        // Validar e definir nível de degradação padrão se necessário
        if (propriedade.getIdNivelDegradacao() == null) {
            // Buscar nível de degradação padrão (exemplo: nível 3 - moderado)
            NivelDegradacaoSolo nivelPadrao = nivelDegradacaoRepository.findByCodigoDegradacao("MODERADO");
            if (nivelPadrao != null) {
                propriedade.setIdNivelDegradacao(nivelPadrao.getIdNivelDegradacao());
            } else {
                // Se não encontrar o padrão, usar o primeiro disponível
                List<NivelDegradacaoSolo> niveis = nivelDegradacaoRepository.findAll();
                if (!niveis.isEmpty()) {
                    propriedade.setIdNivelDegradacao(niveis.get(0).getIdNivelDegradacao());
                } else {
                    throw new IllegalArgumentException("Nenhum nível de degradação encontrado no sistema");
                }
            }
        } else {
            // Verificar se o nível de degradação existe
            if (!nivelDegradacaoRepository.existsById(propriedade.getIdNivelDegradacao())) {
                throw new IllegalArgumentException(
                        "Nível de degradação não encontrado com ID: " + propriedade.getIdNivelDegradacao());
            }
        }

        // Definir data de cadastro se for uma nova propriedade
        if (propriedade.getIdPropriedade() == null) {
            propriedade.setDataCadastro(LocalDateTime.now());
        }

        System.out.println("Antes de salvar:");
        System.out.println("ID Produtor final: " + propriedade.getIdProdutor());
        System.out.println("ID Nível Degradação final: " + propriedade.getIdNivelDegradacao());

        // Salvar a propriedade
        PropriedadeRural salva = propriedadeRepository.save(propriedade);

        System.out.println("Propriedade salva com ID: " + salva.getIdPropriedade());

        return salva;
    }

    /**
     * Exclui uma propriedade e todos os sensores e leituras vinculados a ela
     * Versão segura - carrega as entidades antes de excluir
     */
    public void excluirPropriedadeComSensores(Long propriedadeId) {
        try {
            System.out.println("Iniciando exclusão da propriedade ID: " + propriedadeId);

            // 1. Buscar a propriedade com sensores
            PropriedadeRural propriedade = propriedadeRepository.findByIdWithSensores(propriedadeId).orElse(null);

            if (propriedade == null) {
                throw new IllegalArgumentException("Propriedade não encontrada com ID: " + propriedadeId);
            }

            // 2. Se tem sensores, excluir leituras e sensores
            if (propriedade.getSensores() != null && !propriedade.getSensores().isEmpty()) {

                for (SensorIoT sensor : propriedade.getSensores()) {
                    Long sensorId = sensor.getIdSensor();

                    // 2.1. Buscar e excluir todas as leituras deste sensor
                    List<LeituraSensor> leituras = leituraSensorRepository
                            .findByIdSensorOrderByTimestampLeituraDesc(sensorId);
                    if (!leituras.isEmpty()) {
                        System.out.println("Excluindo " + leituras.size() + " leitura(s) do sensor ID: " + sensorId);
                        leituraSensorRepository.deleteAll(leituras);
                    }

                    // 2.2. Excluir o sensor
                    System.out.println("Excluindo sensor ID: " + sensorId);
                    sensorIoTRepository.deleteById(sensorId);
                }

                // 2.3. Limpar a cache do Hibernate
                propriedadeRepository.flush();
            }

            // 3. Excluir a propriedade
            System.out.println("Excluindo propriedade ID: " + propriedadeId);
            propriedadeRepository.deleteById(propriedadeId);
            propriedadeRepository.flush();

            System.out.println("Propriedade, sensores e leituras excluídos com sucesso!");

        } catch (Exception e) {
            System.err.println("Erro ao excluir propriedade: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao excluir propriedade: " + e.getMessage(), e);
        }
    }

    public void deleteById(Long id) {
        propriedadeRepository.deleteById(id);
    }

    // Métodos para Dashboard
    public long count() {
        return propriedadeRepository.countTotalPropriedades();
    }

    public Double calcularCapacidadeAbsorcaoMedia() {
        Double media = propriedadeRepository.calcularCapacidadeAbsorcaoMedia();
        return media != null ? media : 0.0;
    }

    public List<PropriedadeRural> findTop5ByCapacidadeAbsorcao() {
        List<PropriedadeRural> propriedades = propriedadeRepository.findTop10ByCapacidadeAbsorcao();
        return propriedades.size() > 5 ? propriedades.subList(0, 5) : propriedades;
    }

    // Busca por região (exemplo: Mairiporã)
    public List<PropriedadeRural> findByRegiaoMairipora() {
        // Coordenadas aproximadas de Mairiporã
        BigDecimal latMin = new BigDecimal("-23.4");
        BigDecimal latMax = new BigDecimal("-23.2");
        BigDecimal lonMin = new BigDecimal("-46.7");
        BigDecimal lonMax = new BigDecimal("-46.5");

        return propriedadeRepository.findByRegiao(latMin, latMax, lonMin, lonMax);
    }

    // Calcular impacto de retenção hídrica
    public BigDecimal calcularImpactoRetencaoHidrica(Long propriedadeId) {
        PropriedadeRural propriedade = findByIdWithSensores(propriedadeId);
        if (propriedade == null || propriedade.getNivelDegradacao() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal areaHectares = propriedade.getAreaHectares();
        // USAR método da entidade que calcula baseado no nível numérico
        BigDecimal capacidadeAbsorcao = propriedade.getNivelDegradacao().getCapacidadeAbsorcaoPercentual();

        if (areaHectares != null && capacidadeAbsorcao != null) {
            BigDecimal fatorConversao = new BigDecimal("1000");
            return areaHectares.multiply(capacidadeAbsorcao).multiply(fatorConversao);
        }

        return BigDecimal.ZERO;
    }

    // ✅ MÉTODOS FALTANTES ADICIONADOS
    public List<NivelDegradacaoSolo> findAllNiveisDegradacao() {
        return nivelDegradacaoRepository.findAll();
    }

    public List<ProdutorRural> findAllProdutores() {
        return produtorRepository.findAll(); // ou findProdutoresAtivos() se você criar essa query
    }
}