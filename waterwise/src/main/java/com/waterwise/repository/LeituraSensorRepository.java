package com.waterwise.repository;

import com.waterwise.model.LeituraSensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LeituraSensorRepository extends JpaRepository<LeituraSensor, Long> {

    List<LeituraSensor> findByIdSensorOrderByTimestampLeituraDesc(Long idSensor);

    @Query("SELECT l FROM LeituraSensor l WHERE l.idSensor = :sensorId AND l.timestampLeitura >= :dataInicio ORDER BY l.timestampLeitura DESC")
    List<LeituraSensor> findLeiturasPorPeriodo(@Param("sensorId") Long sensorId, @Param("dataInicio") LocalDateTime dataInicio);

    @Query("SELECT l FROM LeituraSensor l WHERE l.idSensor = :sensorId ORDER BY l.timestampLeitura DESC LIMIT 1")
    LeituraSensor findUltimaLeitura(@Param("sensorId") Long sensorId);

    @Query("SELECT COUNT(l) FROM LeituraSensor l WHERE l.idSensor = :sensorId AND l.timestampLeitura >= :dataInicio")
    Long countLeiturasPorPeriodo(@Param("sensorId") Long sensorId, @Param("dataInicio") LocalDateTime dataInicio);

    @Modifying
    @Query("DELETE FROM LeituraSensor l WHERE l.idSensor IN (SELECT s.idSensor FROM SensorIoT s WHERE s.idPropriedade = :propriedadeId)")
    void deleteAllLeiturasByPropriedadeId(@Param("propriedadeId") Long propriedadeId);
}

