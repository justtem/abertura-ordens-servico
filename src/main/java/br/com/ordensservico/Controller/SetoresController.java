package br.com.ordensservico.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ordensservico.Model.SetoresModel;
import br.com.ordensservico.Service.SetoresService;
import jakarta.validation.Valid;



@RestController
@RequestMapping("/setores")
public class SetoresController {
    
    private final SetoresService setoresService;

    public SetoresController(SetoresService setoresService) {
        this.setoresService = setoresService;
    }

    @PostMapping 
    public ResponseEntity<SetoresModel> cadastrarSetor(@Valid @RequestBody SetoresModel setor) {

        SetoresModel novoSetor = setoresService.cadastrar(setor);

        return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(novoSetor);
    }
    
    @GetMapping 
    public ResponseEntity<List<SetoresModel>> listar() {

        List<SetoresModel> setores = setoresService.listar();

        return ResponseEntity
        .status(HttpStatus.OK)
        .body(setores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SetoresModel> buscarPorId(@PathVariable Integer id) {

        Optional<SetoresModel> setor = setoresService.buscarPorId(id);

        if (setor.isPresent()) {
            return ResponseEntity.ok(setor.get());
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<SetoresModel> atualizar(@PathVariable Integer id, @Valid  @RequestBody SetoresModel novosDadosSetor) {

        Optional<SetoresModel> setorAtualizado = setoresService.atualizar(id, novosDadosSetor);

        if (setorAtualizado.isPresent()) {
            return ResponseEntity.ok(setorAtualizado.get());
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@Valid @PathVariable Integer id) {

        boolean excluido = setoresService.excluir(id);

        if (excluido) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
