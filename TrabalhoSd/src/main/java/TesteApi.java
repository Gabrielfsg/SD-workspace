import comon.model.Usuario;

public class TesteApi {
    public static void main(String[] args) {
//        List<Usuario> usuarios = new ArrayList<>();
        Usuario usuario = new Usuario();
//        usuario.setLogin("Teste2");
//        usuario.setSenha("1234");
//        usuario.setSaldo(0.0);
//        usuario.setToken("");
//        usuarios.add(usuario);
//        usuarios.add(usuario);
//        usuario.salvarUsuario(usuarios);
//        List<Usuario> usuarios2 = usuario.listarTodos();
        Usuario usuario1 = usuario.buscarUsuarioPorLogin("Teste2");
        Double saldo = usuario.consultarSaldo("Teste2");
        System.out.println(saldo);
    }
}
