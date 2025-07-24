package com.testejava.Entity;

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
    private Pessoa pessoaAlocada;

    @Enumerated(EnumType.STRING)
    private StatusTarefa status;


}
