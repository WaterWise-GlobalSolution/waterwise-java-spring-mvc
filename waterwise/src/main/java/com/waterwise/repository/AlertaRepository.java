package com.waterwise.repository;

import com.waterwise.model.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    // ========== QUERIES BÁSICAS ==========

    /**
     * Buscar alertas por produtor ordenados por data decrescente
     */
    List<Alerta> findByIdProdutorOrderByTimestampAlertaDesc(Long idProdutor);

    /**
     * Buscar alertas por leitura específica
     */
    List<Alerta> findByIdLeitura(Long idLeitura);

    /**
     * Buscar alertas por nível de severidade
     */
    @Query("""
        SELECT a FROM Alerta a 
        JOIN a.nivelSeveridade ns 
        WHERE ns.codigoSeveridade = :severidade 
        ORDER BY a.timestampAlerta DESC
        """)
    List<Alerta> findBySeveridade(@Param("severidade") String severidade);

    // ========== QUERIES POR PERÍODO ==========

    /**
     * Buscar alertas a partir de uma data específica
     */
    @Query("SELECT a FROM Alerta a WHERE a.timestampAlerta >= :dataInicio ORDER BY a.timestampAlerta DESC")
    List<Alerta> findAlertasPorPeriodo(@Param("dataInicio") LocalDateTime dataInicio);

    /**
     * Buscar alertas entre duas datas
     */
    @Query("""
        SELECT a FROM Alerta a 
        WHERE a.timestampAlerta BETWEEN :dataInicio AND :dataFim 
        ORDER BY a.timestampAlerta DESC
        """)
    List<Alerta> findAlertasPorPeriodo(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );

    /**
     * Contar alertas a partir de uma data
     */
    @Query("SELECT COUNT(a) FROM Alerta a WHERE a.timestampAlerta >= :dataInicio")
    long countAlertasPorPeriodo(@Param("dataInicio") LocalDateTime dataInicio);

    // ========== QUERIES DE ESTATÍSTICAS ==========

    /**
     * Contar alertas por nível de severidade
     */
    @Query("""
        SELECT ns.codigoSeveridade, COUNT(a) 
        FROM Alerta a 
        JOIN a.nivelSeveridade ns 
        GROUP BY ns.codigoSeveridade 
        ORDER BY ns.codigoSeveridade
        """)
    List<Object[]> countAlertasPorSeveridade();

    /**
     * Contar alertas por produtor
     */
    @Query("""
        SELECT p.nomeCompleto, COUNT(a) 
        FROM Alerta a 
        JOIN a.produtor p 
        GROUP BY p.nomeCompleto 
        ORDER BY COUNT(a) DESC
        """)
    List<Object[]> countAlertasPorProdutor();

    /**
     * Alertas mais recentes (últimas 24 horas)
     */
    @Query("""
        SELECT a FROM Alerta a 
        WHERE a.timestampAlerta >= :dataLimite 
        ORDER BY a.timestampAlerta DESC
        """)
    List<Alerta> findAlertasRecentes(@Param("dataLimite") LocalDateTime dataLimite);

    // ========== QUERIES CRÍTICAS ==========

    /**
     * Buscar alertas críticos não resolvidos
     */
    @Query("""
        SELECT a FROM Alerta a 
        JOIN a.nivelSeveridade ns 
        WHERE ns.codigoSeveridade = 'CRITICO' 
        ORDER BY a.timestampAlerta DESC
        """)
    List<Alerta> findAlertasCriticos();

    /**
     * Buscar alertas de alta prioridade (ALTO e CRITICO)
     */
    @Query("""
        SELECT a FROM Alerta a 
        JOIN a.nivelSeveridade ns 
        WHERE ns.codigoSeveridade IN ('ALTO', 'CRITICO') 
        ORDER BY ns.codigoSeveridade DESC, a.timestampAlerta DESC
        """)
    List<Alerta> findAlertasAltaPrioridade();

    // ========== QUERIES POR PROPRIEDADE ==========

    /**
     * Buscar alertas por propriedade específica
     */
    @Query("""
        SELECT a FROM Alerta a 
        JOIN a.leitura l 
        JOIN l.sensor s 
        WHERE s.idPropriedade = :propriedadeId 
        ORDER BY a.timestampAlerta DESC
        """)
    List<Alerta> findAlertasPorPropriedade(@Param("propriedadeId") Long propriedadeId);

    /**
     * Contar alertas por propriedade
     */
    @Query("""
        SELECT s.idPropriedade, COUNT(a) 
        FROM Alerta a 
        JOIN a.leitura l 
        JOIN l.sensor s 
        GROUP BY s.idPropriedade 
        ORDER BY COUNT(a) DESC
        """)
    List<Object[]> countAlertasPorPropriedade();

    // ========== QUERIES AVANÇADAS ==========

    /**
     * Buscar alertas com informações completas (JOIN FETCH)
     */
    @Query("""
        SELECT DISTINCT a FROM Alerta a 
        LEFT JOIN FETCH a.produtor 
        LEFT JOIN FETCH a.leitura l 
        LEFT JOIN FETCH l.sensor s 
        LEFT JOIN FETCH s.propriedade 
        LEFT JOIN FETCH a.nivelSeveridade 
        WHERE a.timestampAlerta >= :dataInicio 
        ORDER BY a.timestampAlerta DESC
        """)
    List<Alerta> findAlertasComDetalhes(@Param("dataInicio") LocalDateTime dataInicio);

    /**
     * Buscar alertas por região de Mairiporã
     */
    @Query("""
        SELECT a FROM Alerta a 
        JOIN a.leitura l 
        JOIN l.sensor s 
        JOIN s.propriedade p 
        WHERE p.latitude BETWEEN :latMin AND :latMax 
        AND p.longitude BETWEEN :lonMin AND :lonMax 
        ORDER BY a.timestampAlerta DESC
        """)
    List<Alerta> findAlertasPorRegiao(
            @Param("latMin") java.math.BigDecimal latMin,
            @Param("latMax") java.math.BigDecimal latMax,
            @Param("lonMin") java.math.BigDecimal lonMin,
            @Param("lonMax") java.math.BigDecimal lonMax
    );

    /**
     * Buscar alertas por tipo de sensor
     */
    @Query("""
        SELECT a FROM Alerta a 
        JOIN a.leitura l 
        JOIN l.sensor s 
        JOIN s.tipoSensor ts 
        WHERE ts.nomeTipo = :tipoSensor 
        ORDER BY a.timestampAlerta DESC
        """)
    List<Alerta> findAlertasPorTipoSensor(@Param("tipoSensor") String tipoSensor);

    // ========== QUERIES DE DASHBOARD ==========

    /**
     * Estatísticas gerais para dashboard
     */
    @Query("""
        SELECT 
            COUNT(a) as total,
            COUNT(CASE WHEN ns.codigoSeveridade = 'CRITICO' THEN 1 END) as criticos,
            COUNT(CASE WHEN a.timestampAlerta >= :ultima24h THEN 1 END) as recentes
        FROM Alerta a 
        JOIN a.nivelSeveridade ns
        """)
    Object[] getEstatisticasDashboard(@Param("ultima24h") LocalDateTime ultima24h);

    /**
     * Top 5 produtores com mais alertas
     */
    @Query("""
        SELECT p.nomeCompleto, COUNT(a) as total 
        FROM Alerta a 
        JOIN a.produtor p 
        GROUP BY p.nomeCompleto 
        ORDER BY COUNT(a) DESC 
        LIMIT 5
        """)
    List<Object[]> findTop5ProdutoresComMaisAlertas();

    // ========== MÉTODOS DE LIMPEZA ==========

    /**
     * Deletar alertas antigos (para limpeza periódica)
     */
    @Query("DELETE FROM Alerta a WHERE a.timestampAlerta < :dataLimite")
    void deleteAlertasAntigos(@Param("dataLimite") LocalDateTime dataLimite);

    /**
     * Buscar alertas órfãos (sem leitura válida)
     */
    @Query("""
        SELECT a FROM Alerta a 
        WHERE a.idLeitura NOT IN (SELECT l.idLeitura FROM LeituraSensor l)
        """)
    List<Alerta> findAlertasOrfaos();
}
