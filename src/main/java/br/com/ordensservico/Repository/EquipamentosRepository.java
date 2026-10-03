package br.com.ordensservico.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ordensservico.Model.EquipamentoModel;

public interface EquipamentosRepository extends JpaRepository<EquipamentoModel, Integer> {

}