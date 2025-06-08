package com.waterwise.repository;

import com.waterwise.model.NivelSeveridade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NivelSeveridadeRepository extends JpaRepository<NivelSeveridade, Long> {

    // ========== QUERIES BÁSICAS ==========

    /**
     * Buscar nível por código de severidade
     */
    Optional<NivelSeveridade> findByCodigoSeveridade(String codigoSeveridade);

    /**
     * Verificar se código de severidade existe
     */
    boolean existsByCodigoSeveridade(String codigoSeveridade);

    /**
     * Buscar por descrição (busca parcial)
     */
    @Query("SELECT ns FROM NivelSeveridade ns WHERE UPPER(ns.descricaoSeveridade) LIKE UPPER(CONCAT('%', :descricao, '%'))")
    List<NivelSeveridade> findByDescricaoContaining(@Param("descricao") String descricao);

    // ========== QUERIES ORDENADAS ==========

    /**
     * Buscar todos ordenados por código
     */
    @Query("SELECT ns FROM NivelSeveridade ns ORDER BY ns.codigoSeveridade")
    List<NivelSeveridade> findAllOrdered();

    /**
     * Buscar todos ordenados por prioridade (CRITICO primeiro)
     */
    @Query("""
        SELECT ns FROM NivelSeveridade ns 
        ORDER BY 
            CASE ns.codigoSeveridade 
                WHEN 'CRITICO' THEN 1 
                WHEN 'ALTO' THEN 2 
                WHEN 'MEDIO' THEN 3 
                WHEN 'BAIXO' THEN 4 
                ELSE 5 
            END
        """)
    List<NivelSeveridade> findAllOrderedByPriority();

    // ========== QUERIES DE VALIDAÇÃO ==========

    /**
     * Validar se código é único (para criar novo)
     */
    @Query("SELECT COUNT(ns) > 0 FROM NivelSeveridade ns WHERE UPPER(ns.codigoSeveridade) = UPPER(:codigo)")
    boolean isCodigoSeveridadeExists(@Param("codigo") String codigo);

    /**
     * Buscar códigos de severidade disponíveis
     */
    @Query("SELECT ns.codigoSeveridade FROM NivelSeveridade ns ORDER BY ns.codigoSeveridade")
    List<String> findAllCodigosSeveridade();

    // ========== QUERIES PARA DROPDOWN/SELECT ==========

    /**
     * Buscar níveis para dropdown (ID e descrição)
     */
    @Query("SELECT ns.idNivelSeveridade, ns.codigoSeveridade, ns.descricaoSeveridade FROM NivelSeveridade ns ORDER BY ns.codigoSeveridade")
    List<Object[]> findNiveisParaDropdown();

    // ========== QUERIES DE ESTATÍSTICAS ==========

    /**
     * Contar alertas por nível de severidade
     */
    @Query("""
        SELECT ns.codigoSeveridade, ns.descricaoSeveridade, COUNT(a) 
        FROM NivelSeveridade ns 
        LEFT JOIN Alerta a ON ns.idNivelSeveridade = a.idNivelSeveridade 
        GROUP BY ns.codigoSeveridade, ns.descricaoSeveridade 
        ORDER BY ns.codigoSeveridade
        """)
    List<Object[]> getEstatisticasUso();

    /**
     * Buscar níveis mais utilizados
     */
    @Query("""
        SELECT ns, COUNT(a) as total 
        FROM NivelSeveridade ns 
        LEFT JOIN Alerta a ON ns.idNivelSeveridade = a.idNivelSeveridade 
        GROUP BY ns 
        ORDER BY COUNT(a) DESC
        """)
    List<Object[]> findNiveisMaisUtilizados();

    // ========== QUERIES ESPECÍFICAS DO SISTEMA ==========

    /**
     * Buscar níveis críticos (ALTO e CRITICO)
     */
    @Query("SELECT ns FROM NivelSeveridade ns WHERE ns.codigoSeveridade IN ('ALTO', 'CRITICO')")
    List<NivelSeveridade> findNiveisCriticos();

    /**
     * Buscar nível padrão (MEDIO)
     */
    @Query("SELECT ns FROM NivelSeveridade ns WHERE ns.codigoSeveridade = 'MEDIO'")
    Optional<NivelSeveridade> findNivelPadrao();

    // ========== QUERIES PARA AUTOMAÇÃO ==========

    /**
     * Buscar nível apropriado para umidade baixa
     */
    @Query("""
        SELECT ns FROM NivelSeveridade ns 
        WHERE ns.codigoSeveridade = 
            CASE 
                WHEN :umidade < 20 THEN 'CRITICO'
                WHEN :umidade < 30 THEN 'ALTO'
                WHEN :umidade < 40 THEN 'MEDIO'
                ELSE 'BAIXO'
            END
        """)
    Optional<NivelSeveridade> findNivelParaUmidade(@Param("umidade") java.math.BigDecimal umidade);

    /**
     * Buscar nível apropriado para temperatura
     */
    @Query("""
        SELECT ns FROM NivelSeveridade ns 
        WHERE ns.codigoSeveridade = 
            CASE 
                WHEN :temperatura > 40 THEN 'CRITICO'
                WHEN :temperatura > 35 THEN 'ALTO'
                WHEN :temperatura > 30 THEN 'MEDIO'
                ELSE 'BAIXO'
            END
        """)
    Optional<NivelSeveridade> findNivelParaTemperatura(@Param("temperatura") java.math.BigDecimal temperatura);

    /**
     * Buscar nível apropriado para precipitação
     */
    @Query("""
        SELECT ns FROM NivelSeveridade ns 
        WHERE ns.codigoSeveridade = 
            CASE 
                WHEN :precipitacao > 100 THEN 'CRITICO'
                WHEN :precipitacao > 50 THEN 'ALTO'
                WHEN :precipitacao > 20 THEN 'MEDIO'
                ELSE 'BAIXO'
            END
        """)
    Optional<NivelSeveridade> findNivelParaPrecipitacao(@Param("precipitacao") java.math.BigDecimal precipitacao);

    // ========== MÉTODOS CUSTOMIZADOS ==========

    /**
     * Buscar nível com maior prioridade para uma lista de códigos
     */
    @Query("""
        SELECT ns FROM NivelSeveridade ns 
        WHERE ns.codigoSeveridade IN :codigos
        ORDER BY 
            CASE ns.codigoSeveridade 
                WHEN 'CRITICO' THEN 1 
                WHEN 'ALTO' THEN 2 
                WHEN 'MEDIO' THEN 3 
                WHEN 'BAIXO' THEN 4 
                ELSE 5 
            END 
        LIMIT 1
        """)
    Optional<NivelSeveridade> findMaiorPrioridade(@Param("codigos") List<String> codigos);
}
