package comon.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Usuario implements Serializable {

    private static final String FILE_PATH = "usuario.json";

    private static final String FILE_PATH_VERSAO = "versaoBanco.txt";
    private String login;
    private String senha;

    private Double saldo;

    private String salt;

    private int versaoBanco;

    public int getVersaoBanco() {
        return versaoBanco;
    }

    public void setVersaoBanco(int versaoBanco) {
        this.versaoBanco = versaoBanco;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Usuario() {}

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public void salvarUsuario(Usuario usuario, List<Usuario> usuarios) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            File file = new File(FILE_PATH);
            if (!file.exists()) {
                throw new RuntimeException("Arquivo não existe.");
            }

            if (usuarios.size() > 0){
                usuarios.forEach(usuario1 -> {
                    if (usuario1.getLogin().equals(usuario.getLogin())){
                        usuario1.setSenha(usuario.getSenha());
                        usuario1.setSaldo(usuario.getSaldo());
                    }
                });
            } else {
                usuarios = listarTodos();
                usuarios.add(usuario);
            }
            // Salva a lista de usuários no arquivo JSON
            objectMapper.writeValue(file, usuarios);

            System.out.println("Usuários salvos com sucesso no arquivo JSON.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Usuario> listarTodos() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            File file = new File(FILE_PATH);
            if (!file.exists()) {
                throw new RuntimeException("Arquivo não existe.");
            }
            List<Usuario> usuarios = objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, Usuario.class));
            return usuarios;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public Usuario buscarUsuarioPorLogin(String login){
        List<Usuario> usuarios = listarTodos();
        for (Usuario usuario : usuarios) {
            if (usuario.getLogin().equals(login)) {
                return usuario;
            }
        }
        return null;
    }

    public String lerArquivo() {
        StringBuilder conteudo = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH_VERSAO))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                conteudo.append(linha);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return conteudo.toString();
    }

    public void atualizarArquivo(String novoConteudo) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH_VERSAO))) {
            bw.write(novoConteudo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "login='" + login + '\'' +
                ", senha='" + senha + '\'' +
                ", saldo=" + saldo +
                ", salt='" + salt + '\'' +
                '}';
    }
}
