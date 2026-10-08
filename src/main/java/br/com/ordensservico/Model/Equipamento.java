package br.com.ordensservico.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


@Entity 
@Table(name = "equipamentos")
public class Equipamento {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "O nome do equipamento não pode ser vazio")
    private String nome;

    @NotBlank(message = "O número de patrimônio não pode ser vazio")
    private String numeroPatrimonio;

    @ManyToOne 
    @JoinColumn (name = "setor_id", nullable = false)
    @NotNull(message = "O setor do equipamento não pode ser nulo")
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

    public String getNomeSetor() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
