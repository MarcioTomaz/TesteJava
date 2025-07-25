package com.testejava.Repository;

import com.testejava.Entity.DTO.ContagemPorDepartamento;
import com.testejava.Entity.Enums.Departamento;
import com.testejava.Entity.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {



    @Query("SELECT DISTINCT p FROM Pessoa p JOIN p.tarefas t " +
            "WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%')) " +
            "AND t.prazo = :prazo")
    List<Pessoa> findByNomeContainingIgnoreCaseAndTarefaPrazo(
            @Param("nome") String nome,
            @Param("prazo") LocalDate prazo
    );


    @Query("SELECT p.departamento as departamento, COUNT(p.id) as total " +
            " FROM Pessoa p "+
            " GROUP BY p.departamento ")
    List<ContagemPorDepartamento> countPessoasByDepartamento();
}
