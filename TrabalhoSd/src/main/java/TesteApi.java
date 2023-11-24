import backend.services.TransferenciaService;
import backend.services.UsuarioService;
import comon.model.Transferencia;
import comon.model.Usuario;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TesteApi {
    public static void main(String[] args) {
//        List<Usuario> usuarios = new ArrayList<>();
       Usuario usuario = new Usuario();
//        UsuarioService.realizarLogin("Teste3","sigma132");
//        usuario.setLogin("Teste3");
//        usuario.setSenha("1234");
//        usuario.setSaldo(15.0);
//        usuarios.add(usuario);
//        usuarios.add(usuario);
//        usuario.salvarUsuario(usuario, new ArrayList<>());
        List<Usuario> usuarios2 = usuario.listarTodos();
        System.out.println(usuarios2);
//        Usuario usuario1 = usuario.buscarUsuarioPorLogin("Teste2");
//        Double saldo = usuario.consultarSaldo("Teste2");
//        System.out.println(saldo);
//        System.out.println(UsuarioService.consultarSaldo("Teste2"));
//        System.out.println(UsuarioService.alterarSenha("Teste2", "sigma132"));
//        Transferencia transferencia = new Transferencia();
//        transferencia.setValor(200.0);
//        transferencia.setContaRemetente("Teste2");
//        transferencia.setContaDestino("Teste3");
//        transferencia.setData(LocalDateTime.now());
//        TransferenciaService.fazerTransferencia(transferencia);
//        List<Transferencia> transferencias = transferencia.listarTodos();
//        transferencias.forEach(transferencia1 -> {
//            System.out.println(transferencia1.getData());
//            System.out.println(transferencia1.getContaDestino());
//            System.out.println(transferencia1.getContaRemetente());
//            System.out.println(transferencia1.getValor());
//        });
    }
}
