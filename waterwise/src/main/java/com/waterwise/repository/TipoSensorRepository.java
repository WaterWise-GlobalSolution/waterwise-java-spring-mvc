package com.waterwise.repository;

import com.waterwise.model.TipoSensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoSensorRepository extends JpaRepository<TipoSensor, Long> {

    TipoSensor findByNomeTipo(String nomeTipo);

    @Query("SELECT t FROM TipoSensor t ORDER BY t.nomeTipo")
    List<TipoSensor> findTiposAtivos();

}
