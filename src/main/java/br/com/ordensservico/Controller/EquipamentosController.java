package br.com.ordensservico.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ordensservico.Model.EquipamentoModel;
import br.com.ordensservico.Service.EquipamentosService;

@RestController 
@RequestMapping("/equipamentos")
public class EquipamentosController {
    
    private final EquipamentosService equipamentosService;

    public EquipamentosController(EquipamentosService equipamentosService) {
        this.equipamentosService = equipamentosService;
    }

    @PostMapping
    public ResponseEntity<EquipamentoModel> cadastrarEquipamento(@RequestBody EquipamentoModel equipamento) {

        EquipamentoModel novoEquipamento = equipamentosService.cadastrar(equipamento);

        return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(novoEquipamento);
    }

    
    @GetMapping
    public ResponseEntity<List<EquipamentoModel>> listar() {

        List<EquipamentoModel> equipamentos = equipamentosService.listar();

        return ResponseEntity
        .status(HttpStatus.OK)
        .body(equipamentos);
    }


}
