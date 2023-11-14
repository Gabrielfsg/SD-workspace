package org.example;

import org.example.model.Usuario;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BancoAPI extends Remote {

    Usuario fazerLogin(String login, String senha) throws RemoteException;

    String criarConta(String login, String senha) throws RemoteException;

    Double consultarSaldo(String login) throws RemoteException;

    String alterarDados(String login, String senha) throws RemoteException;

    String fazerTransferencia(Usuario usuario) throws RemoteException;
}
