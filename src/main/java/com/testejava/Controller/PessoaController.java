package com.testejava.Controller;

import com.testejava.Entity.DTO.PessoaDTO;
import com.testejava.Entity.DTO.PessoaListTarefaDTO;
import com.testejava.Entity.DTO.PessoaMediaTarefaDTO;
import com.testejava.Entity.DTO.FilterByNameDTO;
import com.testejava.Service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/pessoas")
public class PessoaController {

    @Autowired
    private PessoaService pessoaService;

    @PostMapping
    public ResponseEntity<PessoaDTO> createPessoa(@RequestBody PessoaDTO pessoaDTO) {

        PessoaDTO result = pessoaService.create(pessoaDTO);

        return ResponseEntity.ok(result);
    }

    @PutMapping("{pessoaId}")
    public ResponseEntity<PessoaDTO> editPessoa(@PathVariable Long pessoaId,
                                                      @RequestBody PessoaDTO pessoaEditDTO ){

        PessoaDTO result = pessoaService.update(pessoaId, pessoaEditDTO);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("{pessoaId}")
    public ResponseEntity<PessoaDTO> deletePessoa(@PathVariable Long pessoaId) {

        pessoaService.delete(pessoaId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    public ResponseEntity<List<PessoaListTarefaDTO>> getPessoas(){

        List<PessoaListTarefaDTO> result = pessoaService.getPessoaTarefa();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/gastos")
    public ResponseEntity<List<PessoaMediaTarefaDTO>> getPessoasGastos(@RequestParam String nome,
                                                                       @RequestParam("prazo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate prazo) {

        List<PessoaMediaTarefaDTO> result = pessoaService.getPessoaPorNome(nome, prazo);

        return ResponseEntity.ok(result);
    }

}
