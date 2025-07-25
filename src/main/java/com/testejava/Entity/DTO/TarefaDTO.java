package com.testejava.Entity.DTO;

import com.testejava.Entity.Enums.Departamento;
import com.testejava.Entity.Enums.StatusTarefa;
import com.testejava.Entity.Pessoa;

import java.time.Duration;
import java.time.LocalDate;

public record TarefaDTO(Long id, String titulo, String descricao, LocalDate prazo,
                        Duration duracao, Departamento departamento, Pessoa pessoaAlocada,
                        StatusTarefa statusTarefa) {
}
