package org.example;

import org.example.model.Usuario;

import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;


class OffRmiRegistry extends Thread {
    @Override
    public void run() {
        try {
            UnicastRemoteObject.unexportObject(java.rmi.registry.LocateRegistry.getRegistry(1099), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class ServidorRMI extends UnicastRemoteObject implements BancoAPI {

    private static final Logger logger = Logger.getLogger(ServidorRMI.class.getName());

    public static void main(String[] args) {

        logger.info("RMI Registry off.");
        Runtime.getRuntime().addShutdownHook(new OffRmiRegistry());

        try {
            ServidorRMI servidorRMI = new ServidorRMI();
            try {
                logger.info("Alocando registry criado. ");
                java.rmi.registry.LocateRegistry.getRegistry(1099);
                Naming.rebind("rmi://localhost/banco", servidorRMI);

            } catch (Exception e) {
                logger.info("Registry atual falhou, criando novo registry. ");
                java.rmi.registry.LocateRegistry.createRegistry(1099);
                Naming.bind("rmi://localhost/banco", servidorRMI);
            }
        } catch (RemoteException | AlreadyBoundException | MalformedURLException e) {
            System.out.println("Erro no servidor: " + e.getMessage());
        }


    }

    public ServidorRMI() throws RemoteException {
        super();
    }

    @Override
    public Usuario fazerLogin(String login, String senha) throws RemoteException {
        return null;
    }

    @Override
    public String criarConta(String login, String senha) throws RemoteException {
        return null;
    }

    @Override
    public Double consultarSaldo(String token) throws RemoteException {
        return null;
    }

    @Override
    public String alterarDados(String login, String senha) throws RemoteException {
        return null;
    }

    @Override
    public String fazerTransferencia(Usuario usuario) throws RemoteException {
        return null;
    }
}
