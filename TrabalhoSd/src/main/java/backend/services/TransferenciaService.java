package backend.services;

import comon.model.Transferencia;
import comon.model.Usuario;

public class TransferenciaService {

    public static Transferencia fazerTransferencia(Transferencia transferencia) {
        Usuario remetente = new Usuario().buscarUsuarioPorLogin(transferencia.getContaRemetente());
        Usuario destino = new Usuario().buscarUsuarioPorLogin(transferencia.getContaDestino());
        if (!destino.getLogin().equals(remetente.getLogin()) ){
            if (destino != null && remetente != null ) {
                if (remetente.getSaldo() < transferencia.getValor()) {
                    System.out.println("Remetente não tem saldo suficiente!");
                } else {
                    remetente.setSaldo(remetente.getSaldo() - transferencia.getValor());
                    destino.setSaldo(destino.getSaldo() + transferencia.getValor());
                    remetente.salvarUsuario(remetente, remetente.listarTodos());
                    destino.salvarUsuario(destino, destino.listarTodos());
                    transferencia.salvarTransferencia(transferencia);
                    System.out.println("Transferência Feita com Sucesso!");
                    return transferencia;
                }
            } else {
                System.out.println("Erro: Destinatario ou Remetente inexistente.");
            }
        } else {
            System.out.println("Erro: Destinatario e Remetente não podem ser os mesmos.");
        }
            return null;
    }
}
