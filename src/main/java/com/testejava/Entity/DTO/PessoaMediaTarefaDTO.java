package com.testejava.Entity.DTO;

import com.testejava.Entity.Enums.Departamento;

import java.time.Duration;

public record PessoaMediaTarefaDTO(Long id, String nome, Departamento departamento, Duration mediaHrTarefa) {
}
