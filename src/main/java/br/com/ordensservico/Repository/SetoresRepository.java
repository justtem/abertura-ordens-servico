package br.com.ordensservico.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ordensservico.Model.SetoresModel;

public interface SetoresRepository extends JpaRepository<SetoresModel, Integer> {

}