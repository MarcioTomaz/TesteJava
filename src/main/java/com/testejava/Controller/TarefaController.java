package com.testejava.Controller;


import com.testejava.Entity.DTO.PessoaIdDTO;
import com.testejava.Entity.DTO.TarefaDTO;
import com.testejava.Service.TarefaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/tarefas")
public class TarefaController {

    @Autowired
    private TarefaService tarefaService;

    @PostMapping
    public ResponseEntity<TarefaDTO> createTarefa(@RequestBody TarefaDTO tarefaDTO) {

        TarefaDTO result = tarefaService.create(tarefaDTO);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/alocar/{id}")
    public ResponseEntity<TarefaDTO> alocarTarefa(@PathVariable Long id,
                                                  @RequestBody PessoaIdDTO pessoaIdDTO) {

        TarefaDTO result = tarefaService.alocar(id, pessoaIdDTO);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/finalizar/{id}")
    public ResponseEntity<TarefaDTO> finalizarTarefa(@PathVariable Long id) {

        tarefaService.finalizarTarefa(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pendentes")
    public ResponseEntity<Page<TarefaDTO>> getPendentes() {
        PageRequest pageable = PageRequest.of(0, 3, Sort.by("prazo").ascending());

        Page<TarefaDTO> result = tarefaService.buscarTarefaSemPessoa(pageable);

        return ResponseEntity.ok(result);
    }


}
