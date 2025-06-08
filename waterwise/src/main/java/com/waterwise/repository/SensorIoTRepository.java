package com.waterwise.repository;

import com.waterwise.model.SensorIoT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorIoTRepository extends JpaRepository<SensorIoT, Long> {

    // ✅ ADICIONAR ESTE MÉTODO
    @Query("SELECT DISTINCT s FROM SensorIoT s " +
            "LEFT JOIN FETCH s.tipoSensor " +
            "LEFT JOIN FETCH s.propriedade p " +
            "LEFT JOIN FETCH p.produtor " +
            "ORDER BY s.idSensor DESC")
    List<SensorIoT> findAllWithRelacionamentos();

    List<SensorIoT> findByIdPropriedade(Long idPropriedade);

    @Query("SELECT s FROM SensorIoT s")
    List<SensorIoT> findSensoresAtivos();

    @Query("SELECT s FROM SensorIoT s LEFT JOIN FETCH s.leituras WHERE s.idSensor = :id")
    SensorIoT findByIdWithLeituras(@Param("id") Long id);

    @Query("SELECT COUNT(s) FROM SensorIoT s")
    long countSensoresAtivos();

    @Modifying
    @Query("DELETE FROM SensorIoT s WHERE s.idPropriedade = :propriedadeId")
    void deleteAllByIdPropriedade(@Param("propriedadeId") Long propriedadeId);
}
