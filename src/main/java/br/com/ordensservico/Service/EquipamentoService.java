package br.com.ordensservico.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.ordensservico.Model.Equipamento;
import br.com.ordensservico.Repository.EquipamentoRepository;

@Service 
public class EquipamentoService {

    private final EquipamentoRepository equipamentoRepository;

    public EquipamentoService(EquipamentoRepository equipamentoRepository) {
        this.equipamentoRepository = equipamentoRepository;
    }

    public Equipamento cadastrar(Equipamento equipamento) {
        return equipamentoRepository.save(equipamento);
    }

    public List<Equipamento> listar() {
        return equipamentoRepository.findAll();
    }

    public Equipamento buscarPorId(Integer id) {
        return equipamentoRepository.findById(id).orElse(null);
    }  

    public Equipamento atualizar(Integer id, Equipamento equipamentoAtualizado) {
        Equipamento equipamentoExistente = equipamentoRepository.findById(id).orElse(null);

        if (equipamentoExistente == null) {
            return null;
        }

        equipamentoExistente.setNome(equipamentoAtualizado.getNome());
        equipamentoExistente.setSetor(equipamentoAtualizado.getSetor());
        equipamentoExistente.setNumeroPatrimonio(equipamentoAtualizado.getNumeroPatrimonio());

        return equipamentoRepository.save(equipamentoExistente);
    }

    public boolean excluir(Integer id) {
        if (!equipamentoRepository.existsById(id)) {
            return false;
        }

        equipamentoRepository.deleteById(id);
        return true;
    }

}
