package com.testejava.service;

import com.testejava.Entity.DTO.PessoaDTO;
import com.testejava.Entity.DTO.PessoaListTarefaDTO;
import com.testejava.Entity.DTO.PessoaMediaTarefaDTO;
import com.testejava.Entity.Enums.Departamento;
import com.testejava.Entity.Enums.StatusTarefa;
import com.testejava.Entity.Pessoa;
import com.testejava.Entity.Tarefa;
import com.testejava.Repository.PessoaRepository;
import com.testejava.Service.PessoaService;
import com.testejava.Service.exceptions.ResourceNotFoundException;
import com.testejava.Service.exceptions.ValidacaoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para PessoaService")
public class PessoaServiceTest {

    @InjectMocks
    private PessoaService pessoaService;

    @Mock
    private PessoaRepository pessoaRepository;

    private Pessoa pessoa;
    private PessoaDTO pessoaDTO;

    @BeforeEach
    void setUp() {
        pessoaDTO = new PessoaDTO(1L, "João Teste", Departamento.TI);
        pessoa = new Pessoa(pessoaDTO);
    }

    @Nested
    @DisplayName("Testes de Criação")
    class CreatePessoaTest {

        @Test
        @DisplayName("Deve criar uma pessoa com sucesso")
        void deveCriarPessoaComSucesso() {
            PessoaDTO dtoParaCriar = new PessoaDTO(null, "João Teste", Departamento.TI);
            when(pessoaRepository.save(any(Pessoa.class))).thenReturn(pessoa);

            PessoaDTO resultado = pessoaService.create(dtoParaCriar);

            assertNotNull(resultado);
            assertEquals(pessoa.getId(), resultado.id());
            assertEquals(pessoa.getNome(), resultado.nome());
            assertEquals(pessoa.getDepartamento(), resultado.departamento());
            verify(pessoaRepository).save(any(Pessoa.class));
        }

        @Test
        @DisplayName("Deve lançar ValidacaoException ao criar pessoa com nome vazio")
        void deveLancarExcecaoAoCriarPessoaComNomeVazio() {
            PessoaDTO dtoInvalido = new PessoaDTO(null, "  ", Departamento.FINANCEIRO);

            assertThrows(ValidacaoException.class, () -> pessoaService.create(dtoInvalido));
            verify(pessoaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar ValidacaoException ao criar pessoa com departamento nulo")
        void deveLancarExcecaoAoCriarPessoaComDepartamentoNulo() {
            PessoaDTO dtoInvalido = new PessoaDTO(null, "Nome Válido", null);

            assertThrows(ValidacaoException.class, () -> pessoaService.create(dtoInvalido));
            verify(pessoaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testes de Atualização")
    class UpdatePessoaTest {

        @Test
        @DisplayName("Deve atualizar uma pessoa com sucesso")
        void deveAtualizarPessoaComSucesso() {
            PessoaDTO dadosParaAtualizar = new PessoaDTO(null, "Maria Silva", Departamento.RH);
            when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));
            when(pessoaRepository.save(any(Pessoa.class))).thenAnswer(invocation -> invocation.getArgument(0));

            PessoaDTO resultado = pessoaService.update(1L, dadosParaAtualizar);

            assertEquals("Maria Silva", resultado.nome());
            assertEquals(Departamento.RH, resultado.departamento());
            verify(pessoaRepository).save(any(Pessoa.class));
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao tentar atualizar pessoa inexistente")
        void deveLancarExcecaoAoAtualizarPessoaInexistente() {
            PessoaDTO dadosParaAtualizar = new PessoaDTO(null, "Teste", Departamento.TI);
            when(pessoaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> pessoaService.update(99L, dadosParaAtualizar));
            verify(pessoaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testes de Exclusão")
    class DeletePessoaTest {
        @Test
        @DisplayName("Deve excluir uma pessoa com sucesso")
        void deveExcluirPessoaComSucesso() {
            when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));
            doNothing().when(pessoaRepository).delete(pessoa);

            assertDoesNotThrow(() -> pessoaService.delete(1L));
            verify(pessoaRepository).delete(pessoa);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao tentar excluir pessoa inexistente")
        void deveLancarExcecaoAoTentarExcluirPessoaInexistente() {
            when(pessoaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> pessoaService.delete(99L));
            verify(pessoaRepository, never()).delete(any());
        }
    }


    @Nested
    @DisplayName("Testes de Listagem")
    class ListagemPessoaTest {
        @Test
        @DisplayName("Deve retornar a lista de pessoas com o total de horas gastas em tarefas finalizadas")
        void deveRetornarListaDePessoaTarefa() {
            Tarefa t1 = new Tarefa();
            t1.setDuracao(Duration.ofHours(2));
            t1.setStatus(StatusTarefa.FINALIZADA);

            Tarefa t2 = new Tarefa();
            t2.setDuracao(Duration.ofHours(5));
            t2.setStatus(StatusTarefa.PENDENTE); // Não deve ser contada

            pessoa.setTarefas(List.of(t1, t2));
            when(pessoaRepository.findAll()).thenReturn(List.of(pessoa));

            List<PessoaListTarefaDTO> resultado = pessoaService.getPessoaTarefa();

            assertEquals(1, resultado.size());
            assertEquals(Duration.ofHours(2), resultado.get(0).horasGastas());
        }

        @Test
        @DisplayName("Deve retornar a média de duração das tarefas de uma pessoa por nome e prazo")
        void deveRetornarMediaDeDuracaoDasTarefas() {
            Tarefa t1 = new Tarefa();
            t1.setDuracao(Duration.ofHours(1));
            Tarefa t2 = new Tarefa();
            t2.setDuracao(Duration.ofHours(3));
            pessoa.setTarefas(List.of(t1, t2));
            LocalDate prazoBusca = LocalDate.of(2025, 7, 25);

            when(pessoaRepository.findByNomeContainingIgnoreCaseAndTarefaPrazo("João", prazoBusca))
                    .thenReturn(List.of(pessoa));

            List<PessoaMediaTarefaDTO> resultado = pessoaService.getPessoaPorNome("João", prazoBusca);

            assertEquals(1, resultado.size());
            assertEquals(Duration.ofHours(2), resultado.get(0).mediaHrTarefa()); // (1+3)/2 = 2
        }

        @Test
        @DisplayName("Deve retornar média de duração zero se a pessoa não tiver tarefas")
        void deveRetornarMediaZeroSePessoaNaoTiverTarefas() {
            pessoa.setTarefas(Collections.emptyList());
            LocalDate prazoBusca = LocalDate.of(2025, 7, 25);
            when(pessoaRepository.findByNomeContainingIgnoreCaseAndTarefaPrazo("João", prazoBusca))
                    .thenReturn(List.of(pessoa));

            List<PessoaMediaTarefaDTO> resultado = pessoaService.getPessoaPorNome("João", prazoBusca);

            assertEquals(1, resultado.size());
            assertEquals(Duration.ZERO, resultado.get(0).mediaHrTarefa());
        }
    }
}
