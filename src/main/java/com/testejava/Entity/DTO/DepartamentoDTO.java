package com.testejava.Entity.DTO;

import com.testejava.Entity.Enums.Departamento;

public record DepartamentoDTO(Departamento departamento, Long totalPessoas, Long totalTarefas) {
}
