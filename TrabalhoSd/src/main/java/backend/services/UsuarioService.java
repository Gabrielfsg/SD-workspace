package backend.services;

import comon.model.Saldo;
import comon.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class UsuarioService {

    public static Usuario realizarLogin(String login, String senha) {
        Usuario c = new Usuario();
        Usuario user = c.buscarUsuarioPorLogin(login);
        if (user != null) {
            if (user.getSenha().equals(senha)) {
                System.out.println("Login realizado com sucesso: " + user.getLogin() + ", " + user.getSenha());
                return user;
            }
            System.out.println("Senha incorreta para o usuario: " + user.getLogin());
            return null;
        } else {
            System.out.println("Usuario não encontrado: ");
            return null;
        }
    }

    public static Usuario criarConta(String login, String senha) {
        if (!usuarioExistente(login)){
            Usuario usuario = new Usuario();
            usuario.setSenha(senha);
            usuario.setLogin(login);
            usuario.setSaldo(1000.0);
            usuario.salvarUsuario(usuario, new ArrayList<>());
            return usuario;
        }
        return null;
    }

    public static Saldo consultarSaldo(String login){
        Usuario usuario = new Usuario();
        usuario = usuario.buscarUsuarioPorLogin(login);
        if (usuario != null){
            Saldo saldo = new Saldo();
            saldo.setSaldo(usuario.getSaldo());
            return saldo;
        }
        return null;
    }

    public static Usuario alterarSenha(String login, String senha){
        Usuario usuario = new Usuario();
        usuario = usuario.buscarUsuarioPorLogin(login);
        if (usuario != null) {
            usuario.setSenha(senha);
            usuario.salvarUsuario(usuario, usuario.listarTodos());
            return usuario;
        }
            return null;
    }

    private static boolean usuarioExistente(String login){
        Usuario usuario = new Usuario();
        usuario = usuario.buscarUsuarioPorLogin(login);
        if (usuario != null){
            return true;
        }
        return false;
    }
}
