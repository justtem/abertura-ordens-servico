package br.com.ordensservico.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ordensservico.Model.Equipamento;
import br.com.ordensservico.Service.EquipamentoService;
import jakarta.validation.Valid;

@RestController 
@RequestMapping("/equipamentos")
public class EquipamentoController {
    

    private final EquipamentoService equipamentoService;

    public EquipamentoController(EquipamentoService equipamentoService) {
        this.equipamentoService = equipamentoService;
    }

    @PostMapping 
    public Equipamento cadastrarEquipamento(@Valid @RequestBody Equipamento equipamento) {
        return equipamentoService.cadastrar(equipamento);
    }

    @GetMapping
    public List<Equipamento> listarEquipamentos() {
        return equipamentoService.listar();
    }

    @GetMapping("/{id}")
    public Equipamento buscarEquipamentoPorId(@PathVariable Integer id) {
        return equipamentoService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public Equipamento atualizarEquipamento(@PathVariable Integer id, @Valid @RequestBody Equipamento equipamentoAtualizado) {
        return equipamentoService.atualizar(id, equipamentoAtualizado);
    }

    @DeleteMapping("/{id}")
    public boolean excluirEquipamento(@PathVariable Integer id) {
        return equipamentoService.excluir(id);
    }  
}
