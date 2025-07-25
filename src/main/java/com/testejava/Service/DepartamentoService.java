package com.testejava.Service;

import com.testejava.Entity.DTO.ContagemPorDepartamento;
import com.testejava.Entity.DTO.DepartamentoDTO;
import com.testejava.Entity.Enums.Departamento;
import com.testejava.Repository.PessoaRepository;
import com.testejava.Repository.TarefaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DepartamentoService {


    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private TarefaRepository tarefaRepository;

    @Transactional
    public List<DepartamentoDTO> listarQtdDepartamentos() {

        Map<Departamento, Long> pessoasPorDepartamento = pessoaRepository.countPessoasByDepartamento().stream()
                .collect(Collectors.toMap(
                        ContagemPorDepartamento::getDepartamento,
                        ContagemPorDepartamento::getTotal
                ));

        Map<Departamento, Long> tarefasPorDepartamento = tarefaRepository.countTarefasByDepartamento().stream()
                .collect(Collectors.toMap(
                        ContagemPorDepartamento::getDepartamento,
                        ContagemPorDepartamento::getTotal
                ));

        return Arrays.stream(Departamento.values())
                .map(departamento -> new DepartamentoDTO(
                        departamento,
                        pessoasPorDepartamento.getOrDefault(departamento, 0L),
                        tarefasPorDepartamento.getOrDefault(departamento, 0L)
                ))
                .collect(Collectors.toList());
    }
}
