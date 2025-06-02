package com.waterwise.repository;

import com.waterwise.model.PropriedadeRural;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PropriedadeRuralRepository extends JpaRepository<PropriedadeRural, Long> {
    // ✅ MÉTODO SEGURO - com LEFT JOIN para evitar EntityNotFoundException
    @Query("SELECT DISTINCT p FROM PropriedadeRural p " +
            "LEFT JOIN FETCH p.produtor " +
            "LEFT JOIN FETCH p.nivelDegradacao " +
            "ORDER BY p.nomePropriedade ASC")

    List<PropriedadeRural> findAllWithProdutor();

    List<PropriedadeRural> findByIdProdutor(Long idProdutor);

    @Query("SELECT p FROM PropriedadeRural p LEFT JOIN FETCH p.sensores WHERE p.idPropriedade = :id")
    Optional<PropriedadeRural> findByIdWithSensores(@Param("id") Long id);

    @Query("SELECT p FROM PropriedadeRural p WHERE UPPER(p.nomePropriedade) LIKE UPPER(CONCAT('%', :nome, '%'))")
    List<PropriedadeRural> findByNomePropriedadeContainingIgnoreCase(@Param("nome") String nome);

    @Query("SELECT p FROM PropriedadeRural p WHERE p.areaHectares BETWEEN :areaMin AND :areaMax")
    List<PropriedadeRural> findByAreaHectaresBetween(@Param("areaMin") BigDecimal areaMin, @Param("areaMax") BigDecimal areaMax);

    // Queries específicas para dashboard
    @Query("SELECT COUNT(p) FROM PropriedadeRural p")
    long countTotalPropriedades();

    @Query("""
    SELECT AVG(
        CASE nd.nivelNumerico 
            WHEN 1 THEN 85.0 
            WHEN 2 THEN 70.0 
            WHEN 3 THEN 50.0 
            WHEN 4 THEN 30.0 
            WHEN 5 THEN 15.0 
            ELSE 50.0 
        END
    ) 
    FROM PropriedadeRural p 
    JOIN p.nivelDegradacao nd
    """)
    Double calcularCapacidadeAbsorcaoMedia();

    // Top propriedades por área e capacidade de absorção
    @Query("""
    SELECT p FROM PropriedadeRural p 
    JOIN p.nivelDegradacao nd 
    ORDER BY nd.nivelNumerico ASC, p.areaHectares DESC
    """)
    List<PropriedadeRural> findTop10ByCapacidadeAbsorcao();

    // Busca por região (baseado em coordenadas)
    @Query("""
        SELECT p FROM PropriedadeRural p 
        WHERE p.latitude BETWEEN :latMin AND :latMax 
        AND p.longitude BETWEEN :lonMin AND :lonMax
        """)
    List<PropriedadeRural> findByRegiao(
            @Param("latMin") BigDecimal latMin,
            @Param("latMax") BigDecimal latMax,
            @Param("lonMin") BigDecimal lonMin,
            @Param("lonMax") BigDecimal lonMax
    );

    @Query("SELECT COUNT(p) FROM PropriedadeRural p")
    long countPropriedadesAtivas();


}
