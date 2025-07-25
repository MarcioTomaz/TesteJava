package com.testejava.Controller;

import com.testejava.Entity.DTO.DepartamentoDTO;
import com.testejava.Service.DepartamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/departamentos")
public class DepartamentoController {

    @Autowired
    private DepartamentoService departamentoService;


    @GetMapping
    public ResponseEntity<List<DepartamentoDTO>> listar() {

        List<DepartamentoDTO> result = departamentoService.listarQtdDepartamentos();

        return ResponseEntity.ok().body(result);
    }


}
