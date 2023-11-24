package backend.services;

import comon.model.Transferencia;
import comon.model.Usuario;

import java.util.List;

public class TransferenciaService {

    public static Transferencia fazerTransferencia(Transferencia transferencia) {
        Usuario banco = new Usuario();
        Usuario remetente = banco.buscarUsuarioPorLogin(transferencia.getContaRemetente());
        Usuario destino = banco.buscarUsuarioPorLogin(transferencia.getContaDestino());
        if (destino != null && remetente != null) {
            if (!destino.getLogin().equals(remetente.getLogin())) {
                if (remetente.getSaldo() < transferencia.getValor()) {
                    throw new RuntimeException("Remetente não tem saldo suficiente!");
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
                throw new RuntimeException("Erro: Destinatario e Remetente não podem ser os mesmos.");
            }
        } else {
            throw new RuntimeException("Erro: Destinatario ou Remetente inexistente.");
        }
    }

    public static List<Transferencia> extrato(String login){
            Transferencia transferencia = new Transferencia();
            return transferencia.listarTodosPorLogin(login);
    }
}
