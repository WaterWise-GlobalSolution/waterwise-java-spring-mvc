package com.waterwise.repository;

import com.waterwise.model.NivelDegradacaoSolo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NivelDegradacaoSoloRepository extends JpaRepository<NivelDegradacaoSolo, Long> {

    NivelDegradacaoSolo findByCodigoDegradacao(String codigoDegradacao);

    @Query("SELECT n FROM NivelDegradacaoSolo n ORDER BY n.nivelNumerico")
    List<NivelDegradacaoSolo> findNiveisAtivosOrdenados();
}
