package com.testejava.Service;

import com.testejava.Entity.DTO.PessoaIdDTO;
import com.testejava.Entity.DTO.TarefaDTO;
import com.testejava.Entity.Enums.StatusTarefa;
import com.testejava.Entity.Pessoa;
import com.testejava.Entity.Tarefa;
import com.testejava.Repository.PessoaRepository;
import com.testejava.Repository.TarefaRepository;
import com.testejava.Service.exceptions.DepartamentoException;
import com.testejava.Service.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TarefaService {

    @Autowired
    private TarefaRepository tarefaRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Transactional
    public TarefaDTO create(TarefaDTO tarefaDTO) {

        validarTarefaDTO(tarefaDTO);

        Tarefa novaTarefa = new Tarefa(tarefaDTO);

        Tarefa result = tarefaRepository.save(novaTarefa);

        return new TarefaDTO(result.getId(), result.getTitulo(), result.getDescricao(), result.getPrazo(), result.getDuracao(),
                result.getDepartamento(), null, result.getStatus());
    }

    public void validarTarefaDTO(TarefaDTO tarefaDTO) {
        if (tarefaDTO.titulo() == null || tarefaDTO.titulo().trim().isEmpty()) {
            throw new IllegalArgumentException("O título da tarefa é obrigatório.");
        }
        if (tarefaDTO.descricao() == null || tarefaDTO.descricao().trim().isEmpty()) {
            throw new IllegalArgumentException("A descrição da tarefa é obrigatória.");
        }
        if (tarefaDTO.prazo() == null) {
            throw new IllegalArgumentException("O prazo da tarefa é obrigatório.");
        }
        if (tarefaDTO.duracao() == null || tarefaDTO.duracao().isNegative() || tarefaDTO.duracao().isZero()) {
            throw new IllegalArgumentException("A duração da tarefa deve ser positiva.");
        }
        if (tarefaDTO.departamento() == null) {
            throw new IllegalArgumentException("O departamento da tarefa é obrigatório.");
        }
        if (tarefaDTO.statusTarefa() == null) {
            throw new IllegalArgumentException("O status da tarefa é obrigatório.");
        }
//        if (tarefaDTO.pessoaAlocada() == null) {
//            throw new IllegalArgumentException("A pessoa alocada é obrigatória.");
//        }
    }

    @Transactional
    public TarefaDTO alocar(Long id, PessoaIdDTO pessoaIdDTO) {

        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));

        Pessoa pessoa = pessoaRepository.findById(pessoaIdDTO.id()).orElseThrow(() ->
                new ResourceNotFoundException("Pessoa não encontrada"));

        if (!pessoa.getDepartamento().equals(tarefa.getDepartamento())) {
            throw new DepartamentoException("O departamento da tarefa e da pessoa devem ser iguais!");
        }

        tarefa.setPessoaAlocada(pessoa);

        Tarefa result = tarefaRepository.save(tarefa);

        return new TarefaDTO(result.getId(), result.getTitulo(), result.getDescricao(), result.getPrazo(), result.getDuracao(),
                result.getDepartamento(), result.getPessoaAlocada(), result.getStatus());
    }

    public void finalizarTarefa(Long id) {
        Tarefa tarefa = tarefaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada!"));

        tarefa.setStatus(StatusTarefa.FINALIZADA);

        tarefaRepository.save(tarefa);
    }

    public Page<TarefaDTO> buscarTarefaSemPessoa(PageRequest pageable) {

        Page<Tarefa> tarefa = tarefaRepository.findByPessoaAlocadaIdIsNull(pageable);

        return tarefa.map(this::convertDTO);
    }


    private TarefaDTO convertDTO(Tarefa tarefa) {
        return new TarefaDTO(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getDescricao(),
                tarefa.getPrazo(),
                tarefa.getDuracao(),
                tarefa.getDepartamento(),
                tarefa.getPessoaAlocada(),
                tarefa.getStatus()
        );
    }
}
