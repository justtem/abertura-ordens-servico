package br.com.ordensservico.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity 
@Table(name = "equipamentos")
public class Equipamento {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;

    private String numeroPatrimonio;

    @ManyToOne 
    @JoinColumn (name = "setor_id", nullable = false)
    private SetoresModel setor;

    public Equipamento() {
    }

    public Equipamento(Integer id, String nome, String numeroPatrimonio, SetoresModel setor) {
        this.id = id;
        this.nome = nome;
        this.numeroPatrimonio = numeroPatrimonio;
        this.setor = setor;
    }

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public SetoresModel getSetor() {
        return setor;
    }

    public void setSetor(SetoresModel setor) {
        this.setor = setor;
    }
   
    public String getNumeroPatrimonio() {
        return numeroPatrimonio;
    }

    public void setNumeroPatrimonio(String numeroPatrimonio) {
        this.numeroPatrimonio = numeroPatrimonio;
    }
}
