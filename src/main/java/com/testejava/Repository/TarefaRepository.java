package com.testejava.Repository;

import com.testejava.Entity.DTO.ContagemPorDepartamento;
import com.testejava.Entity.DTO.DepartamentoDTO;
import com.testejava.Entity.DTO.TarefaDTO;
import com.testejava.Entity.Tarefa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {


    Page<Tarefa> findByPessoaAlocadaIdIsNull(Pageable pageable);

    @Query("SELECT new com.testejava.Entity.DTO.DepartamentoDTO(t.departamento, COUNT(DISTINCT( t.pessoaAlocada)) ,COUNT(t) )" +
            " FROM Tarefa t " +
            " GROUP BY t.departamento " )
    List<DepartamentoDTO> listarQtdDepartamentos();


    @Query("SELECT t.departamento as departamento, COUNT(t.id) as total " +
            "FROM Tarefa t " +
            "GROUP BY t.departamento")
    List<ContagemPorDepartamento> countTarefasByDepartamento();
}
