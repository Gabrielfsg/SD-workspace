package backend;

import comon.model.Saldo;
import comon.model.Transferencia;
import comon.model.Usuario;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface BancoAPI extends Remote {

    String fazerLogin(String login, String senha) throws RemoteException;

    Usuario criarConta(String login, String senha) throws RemoteException;

    Saldo consultarSaldo(String token) throws RemoteException;

    Usuario alterarSenha(String token, String senha) throws IOException;

    Transferencia fazerTransferencia(String token, Transferencia transferencia) throws RemoteException;

    List<Transferencia> extrato(String token) throws RemoteException;
}
