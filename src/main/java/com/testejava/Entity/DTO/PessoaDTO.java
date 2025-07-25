package com.testejava.Entity.DTO;

import com.testejava.Entity.Enums.Departamento;

public record PessoaDTO(Long id, String nome, Departamento departamento) {
}
