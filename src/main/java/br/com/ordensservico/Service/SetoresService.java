package br.com.ordensservico.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.ordensservico.Model.SetoresModel;
import br.com.ordensservico.Repository.SetoresRepository;

@Service
public class SetoresService {
    
    private final SetoresRepository setoresRepository;

    public SetoresService(SetoresRepository setoresRepository) {
        this.setoresRepository = setoresRepository;
    }

    public SetoresModel cadastrar(SetoresModel setor) {
        return setoresRepository.save(setor);
    }

    public List<SetoresModel> listar() {
        return setoresRepository.findAll();
    }

    public Optional<SetoresModel> buscarPorId(Integer id) {
        return setoresRepository.findById(id);
    }

    public Optional<SetoresModel> atualizar(Integer id, SetoresModel setorAtualizado) {
        Optional<SetoresModel> setorExistente = setoresRepository.findById(id);

        if (setorExistente.isEmpty()) {
            return Optional.empty();
        }

        SetoresModel setor = setorExistente.get();

        setor.setNome(setorAtualizado.getNome());

        return Optional.of(setoresRepository.save(setor));

    }

    public boolean excluir(Integer id) {

        if (!setoresRepository.existsById(id)) {
            return false;
        }

        setoresRepository.deleteById(id);
        return true;
    }
}
