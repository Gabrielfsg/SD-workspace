package backend.services;

import comon.model.Transferencia;
import comon.model.Usuario;

public class TransferenciaService {

    public static void fazerTransferencia(Transferencia transferencia) {
        Usuario remetente = new Usuario().buscarUsuarioPorLogin(transferencia.getContaRemetente());
        Usuario destino = new Usuario().buscarUsuarioPorLogin(transferencia.getContaDestino());
        if (destino != null) {
            if (remetente.getSaldo() < transferencia.getValor()) {
                System.out.println("Remetente não tem saldo suficiente!");
            } else {
                remetente.setSaldo(remetente.getSaldo() - transferencia.getValor());
                destino.setSaldo(destino.getSaldo() + transferencia.getValor());
                remetente.salvarUsuario(remetente, remetente.listarTodos());
                destino.salvarUsuario(destino, destino.listarTodos());
                transferencia.salvarTransferencia(transferencia);
                System.out.println("Transferência Feita com Sucesso!");
            }
        } else {
            System.out.println("Destinatário não existe!");
        }
    }
}
