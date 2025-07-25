package com.testejava.service;

import com.testejava.Entity.DTO.PessoaIdDTO;
import com.testejava.Entity.DTO.TarefaDTO;
import com.testejava.Entity.Enums.Departamento;
import com.testejava.Entity.Enums.StatusTarefa;
import com.testejava.Entity.Pessoa;
import com.testejava.Entity.Tarefa;
import com.testejava.Repository.PessoaRepository;
import com.testejava.Repository.TarefaRepository;
import com.testejava.Service.TarefaService;
import com.testejava.Service.exceptions.DepartamentoException;
import com.testejava.Service.exceptions.ResourceNotFoundException;
import com.testejava.Service.exceptions.ValidacaoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TarefaServiceTest {

    @InjectMocks
    private TarefaService tarefaService;

    @Mock
    private TarefaRepository tarefaRepository;

    @Mock
    private PessoaRepository pessoaRepository;

    private Tarefa tarefa;
    private Pessoa pessoa;
    private TarefaDTO tarefaDTO;

    @BeforeEach
    void setUp() {
        pessoa = new Pessoa();
        pessoa.setId(1L);
        pessoa.setNome("Ana");
        pessoa.setDepartamento(Departamento.FINANCEIRO);

        tarefa = new Tarefa();
        tarefa.setId(1L);
        tarefa.setTitulo("Declarar Impostos");
        tarefa.setDescricao("Gerar e enviar declarações fiscais.");
        tarefa.setPrazo(LocalDate.now().plusDays(10));
        tarefa.setDuracao(Duration.ofHours(8));
        tarefa.setDepartamento(Departamento.FINANCEIRO);
        tarefa.setStatus(StatusTarefa.PENDENTE);

        tarefaDTO = new TarefaDTO(
                null,
                "Revisar Contratos",
                "Verificar cláusulas dos novos contratos",
                LocalDate.now().plusDays(5),
                Duration.ofHours(4),
                Departamento.FINANCEIRO,
                null,
                StatusTarefa.PENDENTE
        );
    }


    @Test
    @DisplayName("Deve criar uma tarefa com sucesso")
    void deveCriarTarefaComSucesso() {
        when(tarefaRepository.save(any(Tarefa.class))).thenAnswer(invocation -> {
            Tarefa tarefaSalva = invocation.getArgument(0);
            tarefaSalva.setId(1L);
            return tarefaSalva;
        });

        TarefaDTO result = tarefaService.create(tarefaDTO);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(tarefaDTO.titulo(), result.titulo());
        verify(tarefaRepository).save(any(Tarefa.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar tarefa com prazo no passado")
    void deveLancarExcecaoParaPrazoNoPassado() {
        TarefaDTO dtoComPrazoInvalido = new TarefaDTO(
                null, "Título", "Descrição", LocalDate.now().minusDays(1), Duration.ofHours(1), Departamento.TI, null, StatusTarefa.PENDENTE
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> {
            tarefaService.create(dtoComPrazoInvalido);
        });
        assertEquals("O prazo deve ser uma data futura.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar tarefa com duração inválida")
    void deveLancarExcecaoParaDuracaoInvalida() {
        TarefaDTO dtoComDuracaoInvalida = new TarefaDTO(
                null, "Título", "Descrição", LocalDate.now().plusDays(1), Duration.ZERO, Departamento.TI, null, StatusTarefa.PENDENTE
        );

        // Act & Assert
        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> {
            tarefaService.create(dtoComDuracaoInvalida);
        });
        assertEquals("A duração da tarefa deve ser positiva.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve alocar uma pessoa a uma tarefa com sucesso")
    void deveAlocarPessoaComSucesso() {
        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));
        when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));
        when(tarefaRepository.save(any(Tarefa.class))).thenReturn(tarefa);

        PessoaIdDTO pessoaIdDTO = new PessoaIdDTO(1L);

        TarefaDTO result = tarefaService.alocar(1L, pessoaIdDTO);

        assertNotNull(result);
        assertNotNull(result.pessoaAlocada());
        assertEquals(1L, result.pessoaAlocada().getId());
        assertEquals("Ana", result.pessoaAlocada().getNome());

        ArgumentCaptor<Tarefa> tarefaCaptor = ArgumentCaptor.forClass(Tarefa.class);
        verify(tarefaRepository).save(tarefaCaptor.capture());
        assertEquals(pessoa, tarefaCaptor.getValue().getPessoaAlocada());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar alocar para uma tarefa inexistente")
    void deveLancarExcecaoSeTarefaNaoEncontradaAoAlocar() {
        when(tarefaRepository.findById(99L)).thenReturn(Optional.empty());
        PessoaIdDTO pessoaIdDTO = new PessoaIdDTO(1L);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            tarefaService.alocar(99L, pessoaIdDTO);
        });
        assertEquals("Tarefa não encontrada", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar alocar uma pessoa inexistente")
    void deveLancarExcecaoSePessoaNaoEncontradaAoAlocar() {
        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));
        when(pessoaRepository.findById(99L)).thenReturn(Optional.empty());
        PessoaIdDTO pessoaIdDTO = new PessoaIdDTO(99L);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            tarefaService.alocar(1L, pessoaIdDTO);
        });
        assertEquals("Pessoa não encontrada", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção se os departamentos da pessoa e da tarefa forem diferentes")
    void deveLancarExcecaoSeDepartamentosForemDiferentes() {
        pessoa.setDepartamento(Departamento.TI); // Departamento diferente da tarefa
        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));
        when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));
        PessoaIdDTO pessoaIdDTO = new PessoaIdDTO(1L);

        DepartamentoException exception = assertThrows(DepartamentoException.class, () -> {
            tarefaService.alocar(1L, pessoaIdDTO);
        });
        assertEquals("O departamento da tarefa e da pessoa devem ser iguais!", exception.getMessage());
    }


    @Test
    @DisplayName("Deve finalizar uma tarefa com sucesso")
    void deveFinalizarTarefaComSucesso() {
        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));

        tarefaService.finalizarTarefa(1L);

        ArgumentCaptor<Tarefa> tarefaCaptor = ArgumentCaptor.forClass(Tarefa.class);
        verify(tarefaRepository).save(tarefaCaptor.capture());

        Tarefa tarefaSalva = tarefaCaptor.getValue();
        assertEquals(StatusTarefa.FINALIZADA, tarefaSalva.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar finalizar uma tarefa inexistente")
    void deveLancarExcecaoAoFinalizarTarefaInexistente() {
        when(tarefaRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            tarefaService.finalizarTarefa(99L);
        });
        assertEquals("Tarefa não encontrada!", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar uma página de tarefas sem pessoa alocada")
    void deveRetornarPaginaDeTarefasSemPessoa() {
        Pageable pageable = PageRequest.of(0, 5);
        tarefa.setPessoaAlocada(null);
        Page<Tarefa> paginaDeTarefas = new PageImpl<>(Collections.singletonList(tarefa), pageable, 1);

        when(tarefaRepository.findByPessoaAlocadaIdIsNull(pageable)).thenReturn(paginaDeTarefas);

        Page<TarefaDTO> result = tarefaService.buscarTarefaSemPessoa((PageRequest) pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(tarefa.getTitulo(), result.getContent().get(0).titulo());
        assertNull(result.getContent().get(0).pessoaAlocada());
        verify(tarefaRepository).findByPessoaAlocadaIdIsNull(pageable);
    }
}
