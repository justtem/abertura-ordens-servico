package br.com.ordensservico.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ordensservico.Model.Equipamento;

public interface EquipamentoRepository extends JpaRepository<Equipamento, Integer> {
    
}
