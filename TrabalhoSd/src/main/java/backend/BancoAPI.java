package backend;

import comon.model.Saldo;
import comon.model.Transferencia;
import comon.model.Usuario;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BancoAPI extends Remote {

    Usuario fazerLogin(String login, String senha) throws RemoteException;

    Usuario criarConta(String login, String senha) throws RemoteException;

    Saldo consultarSaldo(String login) throws RemoteException;

    Usuario alterarSenha(String login, String senha) throws RemoteException;

    Transferencia fazerTransferencia(Transferencia transferencia) throws RemoteException;
}
