package com.waterwise.repository;

import com.waterwise.model.ProdutorRural;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutorRuralRepository extends JpaRepository<ProdutorRural, Long> {

    Optional<ProdutorRural> findByCpfCnpj(String cpfCnpj);

    Optional<ProdutorRural> findByEmail(String email);

    @Query("SELECT p FROM ProdutorRural p ORDER BY p.nomeCompleto")
    List<ProdutorRural> findProdutoresAtivos();

    @Query("SELECT p FROM ProdutorRural p WHERE UPPER(p.nomeCompleto) LIKE UPPER(CONCAT('%', :nome, '%'))")
    List<ProdutorRural> findByNomeCompletoContainingIgnoreCase(@Param("nome") String nome);

}
