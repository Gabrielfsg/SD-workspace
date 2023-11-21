package backend.services;

import comon.model.Usuario;

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
        Usuario c = new Usuario();
        List<Usuario> user = c.listarTodos();
        Usuario newUsuario = new Usuario();
        newUsuario.setSenha(senha);
        newUsuario.setLogin(login);
        newUsuario.setSaldo(1000.0);
        user.add(newUsuario);
        c.salvarUsuario(user);
        return newUsuario;
    }
}
