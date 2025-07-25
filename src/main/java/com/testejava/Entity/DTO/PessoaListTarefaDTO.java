package com.testejava.Entity.DTO;

import com.testejava.Entity.Enums.Departamento;

import java.time.Duration;

public record PessoaListTarefaDTO(String nome, Departamento departamento, Duration horasGastas) {
}
