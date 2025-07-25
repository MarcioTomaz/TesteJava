package com.testejava.Service;

import com.testejava.Entity.DTO.PessoaDTO;
import com.testejava.Entity.DTO.PessoaListTarefaDTO;
import com.testejava.Entity.DTO.PessoaMediaTarefaDTO;
import com.testejava.Entity.DTO.FilterByNameDTO;
import com.testejava.Entity.Enums.StatusTarefa;
import com.testejava.Entity.Pessoa;
import com.testejava.Entity.Tarefa;
import com.testejava.Repository.PessoaRepository;
import com.testejava.Service.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PessoaService {


    @Autowired
    private PessoaRepository pessoaRepository;

    @Transactional
    public PessoaDTO create(PessoaDTO pessoaDTO) {

        validarPessoaDTO(pessoaDTO);

        Pessoa newPessoa = new Pessoa(pessoaDTO);

        Pessoa result = pessoaRepository.save(newPessoa);

        return new PessoaDTO(result.getId(), result.getNome(), result.getDepartamento());
    }

    @Transactional
    public PessoaDTO update(Long pessoaId, PessoaDTO pessoaEditDTO) {

        validarPessoaDTO(pessoaEditDTO);

        Pessoa pessoaAntiga = pessoaRepository.findById(pessoaId)
                .orElseThrow(() -> new ResourceNotFoundException("pessoa não encontrada"));

        pessoaAntiga.setNome(pessoaEditDTO.nome());
        pessoaAntiga.setDepartamento(pessoaEditDTO.departamento());

        Pessoa result = pessoaRepository.save(pessoaAntiga);

        return new PessoaDTO(result.getId(), result.getNome(), result.getDepartamento());
    }

    @Transactional
    public void delete(Long pessoaId) {

        Pessoa pessoa = pessoaRepository.findById(pessoaId)
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada!"));


        pessoaRepository.delete(pessoa);
    }

    @Transactional
    public List<PessoaListTarefaDTO> getPessoaTarefa() {

        List<PessoaListTarefaDTO> result = pessoaRepository.findAll().stream()
                .map(pessoa -> {

                    Duration total = pessoa.getTarefas().stream()
                            .filter(t -> t.getStatus() == StatusTarefa.FINALIZADA)
                            .map(Tarefa::getDuracao)
                            .reduce(Duration.ZERO, Duration::plus);

                    return new PessoaListTarefaDTO(
                            pessoa.getNome(),
                            pessoa.getDepartamento(),
                            total
                    );
                })
                .collect(Collectors.toList());

        return result;
    }


    private void validarPessoaDTO(PessoaDTO pessoaDTO) {
        if (pessoaDTO == null) {
            throw new IllegalArgumentException("Os dados da pessoa não podem ser nulos.");
        }
        if (pessoaDTO.nome() == null || pessoaDTO.nome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da pessoa é obrigatório.");
        }
        if (pessoaDTO.departamento() == null) {
            throw new IllegalArgumentException("O departamento da pessoa é obrigatório.");
        }
    }

    @Transactional
    public List<PessoaMediaTarefaDTO> getPessoaPorNome(String nome, LocalDate prazo) {

        List<Pessoa> result = pessoaRepository.findByNomeContainingIgnoreCaseAndTarefaPrazo(nome, prazo);

        List<PessoaMediaTarefaDTO> pessoasList = new ArrayList<>();

        if(!result.isEmpty()){

            pessoasList = result.stream()
                    .map(pessoa -> {

                        Duration totalDuracao = pessoa.getTarefas().stream()
                                .map(Tarefa::getDuracao)
                                .reduce(Duration.ZERO, Duration::plus);

                        int quantidadeTarefas = pessoa.getTarefas().size();

                        Duration mediaDuracao;
                        if (quantidadeTarefas == 0) {
                            mediaDuracao = Duration.ZERO;
                        } else {
                            mediaDuracao = totalDuracao.dividedBy(quantidadeTarefas);
                        }

                        return new PessoaMediaTarefaDTO(
                                pessoa.getId(),
                                pessoa.getNome(),
                                pessoa.getDepartamento(),
                                mediaDuracao
                        );

                    })
                    .toList();
        }

        return pessoasList;
    }
}
