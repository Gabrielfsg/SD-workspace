package org.example.model;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Usuario implements Serializable {

    private String login;
    private String senha;

    private Double saldo;
    private String token;

    private ObjectMapper objectMapper;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }


    public void salvar(Usuario usuario) {
        List<Usuario> usuarios = listarTodos();
        usuarios.add(usuario);
        salvarNoArquivo(usuarios);
    }

    public void editarPorId(String id, Usuario novoUsuario) {
        List<Usuario> usuarios = listarTodos();
        Optional<Usuario> usuarioExistente = usuarios.stream().filter(u -> u.getLogin().equals(id)).findFirst();

        usuarioExistente.ifPresent(usuario -> {
            usuario.setLogin(novoUsuario.getLogin());
            usuario.setSenha(novoUsuario.getSenha());
            usuario.setToken(novoUsuario.getToken());
            usuario.setSaldo(novoUsuario.getSaldo());
        });

        salvarNoArquivo(usuarios);
    }

    public void excluirPorId(String id) {
        List<Usuario> usuarios = listarTodos();
        usuarios.removeIf(usuario -> usuario.getLogin().equals(id));
        salvarNoArquivo(usuarios);
    }

    public Optional<Usuario> buscarPorId(String id) {
        List<Usuario> usuarios = listarTodos();
        return usuarios.stream().filter(usuario -> usuario.getLogin().equals(id)).findFirst();
    }

    public List<Usuario> listarTodos() {
        String filePath = "org/example/backend/database/usuario.json";
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
                return new ArrayList<>();
            } else {
                return objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, Usuario.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void salvarNoArquivo(List<Usuario> usuarios) {
        String filePath = "org/example/backend/database/usuario.json";
        try {
            objectMapper.writeValue(new File(filePath), usuarios);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
