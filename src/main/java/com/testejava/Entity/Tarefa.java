package com.testejava.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.testejava.Entity.DTO.TarefaDTO;
import com.testejava.Entity.Enums.Departamento;
import com.testejava.Entity.Enums.StatusTarefa;
import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDate;

@Entity
@Table(name = "_tarefa")
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descricao;

    private LocalDate prazo;
    private Duration duracao;

    @Enumerated(EnumType.STRING)
    private Departamento departamento;

    @ManyToOne
    @JoinColumn(name = "pessoa_id")
    @JsonIgnore
    private Pessoa pessoaAlocada;

    @Enumerated(EnumType.STRING)
    private StatusTarefa status;

    public Tarefa() {}

    public Tarefa(TarefaDTO tarefaDTO) {
        this.titulo = tarefaDTO.titulo();
        this.descricao = tarefaDTO.descricao();
        this.prazo = tarefaDTO.prazo();
        this.duracao = tarefaDTO.duracao();
        this.departamento = tarefaDTO.departamento();
        this.pessoaAlocada = tarefaDTO.pessoaAlocada();
        this.status = tarefaDTO.statusTarefa();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getPrazo() {
        return prazo;
    }

    public void setPrazo(LocalDate prazo) {
        this.prazo = prazo;
    }

    public Duration getDuracao() {
        return duracao;
    }

    public void setDuracao(Duration duracao) {
        this.duracao = duracao;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public Pessoa getPessoaAlocada() {
        return pessoaAlocada;
    }

    public void setPessoaAlocada(Pessoa pessoaAlocada) {
        this.pessoaAlocada = pessoaAlocada;
    }

    public StatusTarefa getStatus() {
        return status;
    }

    public void setStatus(StatusTarefa status) {
        this.status = status;
    }
}
