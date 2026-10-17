package br.com.senai.teste.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.nio.file.Path;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;


import br.com.senai.teste.model.Livro;
import br.com.senai.teste.repository.LivroRepository;

@Service 
public class LivroService {
    
    public final LivroRepository livroRepository;

    public LivroService(LivroRepository livroRepository){
        this.livroRepository = livroRepository;
    }


    public Livro cadastrar(Livro livro) {
        return livroRepository.save(livro);
    }

    public List<Livro> listar(){
        return livroRepository.findAll();
    }

    public Optional<Livro> buscarPorId(Integer id){
        return livroRepository.findById(id);
    }

    public Optional<Livro> atualizar(Integer id, Livro novosDados){

        Optional<Livro> livroEncontrado = livroRepository.findById(id);

        if (livroEncontrado.isEmpty()){
            return Optional.empty();
        }

        Livro livro = livroEncontrado.get();

        livro.setTitulo(novosDados.getTitulo());
        livro.setAutor(novosDados.getAutor());
        livro.setAnoPublicacao(novosDados.getAnoPublicacao());

        return Optional.of(livroRepository.save(livro));
    }

    public boolean excluir(Integer id){
        
        if (!livroRepository.existsById(id)){
            return false;
        }

        livroRepository.deleteById(id);
        return true;
    }

    public Livro salvarCapa(Integer id, MultipartFile arquivo) {

        Optional<Livro> livroEncontrado = livroRepository.findById(id);

        if (livroEncontrado.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro não encontrado");
        }

        Livro livro = livroEncontrado.get();

        String nomArquivo = System.currentTimeMillis() + "-" + arquivo.getOriginalFilename();

        File pasta = new File("uploads/capas");

        if (!pasta.exists()) {
            pasta.mkdirs();
        }

        File destino = new File(pasta, nomArquivo).getAbsoluteFile();

        try{
            arquivo.transferTo(destino);
        } catch (IOException erro) {
            erro.printStackTrace();

            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                 "Não foi possível salvar a capa");
        }

        livro.setNomeArquivoCapa(nomArquivo);

        return livroRepository.save(livro);
    }

public Optional<byte[]> buscarCapa(Integer id) {
    Optional<Livro> resultado = livroRepository.findById(id);

    if (resultado.isEmpty()) {
        return Optional.empty();
    }

    Livro livro = resultado.get();

    String nomeArquivo = livro.getNomeArquivoCapa();

    if (nomeArquivo == null || nomeArquivo.isBlank()) {
        return Optional.empty();
    }

    try {
        Path caminho = Paths.get("uploads/capas")
                .resolve(nomeArquivo)
                .normalize();

        if (!Files.exists(caminho) || !Files.isRegularFile(caminho)) {
            return Optional.empty();
        }

        return Optional.of(Files.readAllBytes(caminho));

    } catch (IOException e) {
        throw new RuntimeException("Erro ao ler a capa do livro", e);
    }
}
}
