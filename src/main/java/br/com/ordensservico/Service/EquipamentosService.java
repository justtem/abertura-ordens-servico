package br.com.ordensservico.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.ordensservico.Model.EquipamentoModel;
import br.com.ordensservico.Repository.EquipamentosRepository;

@Service 
public class EquipamentosService {
    
         private final EquipamentosRepository equipamentosRepository;

        public EquipamentosService(EquipamentosRepository equipamentosRepository) {
            this.equipamentosRepository = equipamentosRepository;
        }

        public EquipamentoModel cadastrar(EquipamentoModel equipamento) {
            return equipamentosRepository.save(equipamento);
        }

        public List<EquipamentoModel> listar() {
            return equipamentosRepository.findAll();
        }

        

}
