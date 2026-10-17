package br.com.senai.teste.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.senai.teste.model.Aluno;
import br.com.senai.teste.model.Emprestimo;
import br.com.senai.teste.model.Livro;
import br.com.senai.teste.repository.EmprestimoRepository;

@Service 
public class EmprestimoService {
    
    private final EmprestimoRepository emprestimoRepository;
    private final AlunoService alunoService;
    private final LivroService livroService;

    public EmprestimoService(EmprestimoRepository emprestimoRepository, AlunoService alunoService, LivroService livroService) {
        this.emprestimoRepository = emprestimoRepository;
        this.alunoService = alunoService;
        this.livroService = livroService;
    }

    public Optional<Emprestimo> cadastrar(Integer alunoId, Integer livroId, LocalDate dataPrevistaDevolucao) {
        
        Optional<Aluno> aluno = alunoService.buscarPorId(alunoId);
        Optional<Livro> livro = livroService.buscarPorId(livroId);

        if (aluno.isEmpty() || livro.isEmpty()) {
            return Optional.empty();
        }

        boolean livroEmprestado = emprestimoRepository.existsByLivro_IdAndDataDevolucaoIsNull(livroId);
        
        if (livroEmprestado) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "O livro já está emprestado"
            );
        }

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setAluno(aluno.get());
        emprestimo.setLivro(livro.get());
        emprestimo.setDataEmprestimo(java.time.LocalDate.now());
        emprestimo.setDataPrevistaDevolucao(dataPrevistaDevolucao);
        return Optional.of(emprestimoRepository.save(emprestimo));

    }

    public List<Emprestimo> listar() {
        return emprestimoRepository.findAll();
    }

    public Optional<Emprestimo> buscarPorId(Integer id) {
        return emprestimoRepository.findById(id);
    }

    public Optional<Emprestimo> devolver(Integer id){
        Optional<Emprestimo> encontrado = emprestimoRepository.findById(id);

        if(encontrado.isEmpty()){
            return Optional.empty();
        }

        Emprestimo emprestimo = encontrado.get();

        if(emprestimo.getDataDevolucao() == null){
            emprestimo.setDataDevolucao(LocalDate.now());
            emprestimoRepository.save(emprestimo);
        }

        return Optional.of(emprestimo);
    }

    public List<Emprestimo> listarAtivos() {
        return emprestimoRepository.findByDataDevolucaoIsNull();
    }

    public List<Emprestimo> listarPorAluno(Integer alunoId) {
        return emprestimoRepository.findByAlunoId(alunoId);
    }

    public List<Emprestimo> listarAtrasados() {
        return emprestimoRepository.findByDataPrevistaDevolucaoBeforeAndDataDevolucaoIsNull(LocalDate.now());
    }

    public List<Emprestimo> listarAtrasadosPorAluno(LocalDate dataAtual, Integer alunoId) {
        return emprestimoRepository.findByDataPrevistaDevolucaoBeforeAndDataDevolucaoIsNullAndAlunoId(dataAtual, alunoId);
    }

    public Optional<Emprestimo> renovar (Integer id, LocalDate novaDataPrevistaDevolucao) {

        Optional<Emprestimo> emprestimoOptional = emprestimoRepository.findById(id);

        if (emprestimoOptional.isEmpty()) {
            return Optional.empty();
        }

        Emprestimo emprestimo = emprestimoOptional.get();

        if (emprestimo.getDataDevolucao() != null) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Um emprestimo devolvido não pode ser renovado"
            );
        }

        emprestimo.setDataPrevistaDevolucao(novaDataPrevistaDevolucao);
        return Optional.of(emprestimoRepository.save(emprestimo));
    }
    
}
