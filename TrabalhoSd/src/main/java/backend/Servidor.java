package backend;

import backend.services.UsuarioService;
import comon.RMIServer;
import comon.model.Usuario;
import backend.servidor.ClusterServidores;
import org.jgroups.JChannel;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.locking.LockService;

import java.rmi.RemoteException;
import java.rmi.server.*;


public class Servidor extends UnicastRemoteObject implements BancoAPI {



    public Servidor() throws RemoteException {
        super();
        //start();
    }



    public static void iniciaRegistroRMI() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                UnicastRemoteObject.unexportObject(java.rmi.registry.LocateRegistry.getRegistry(1099), true);
            } catch (Exception e) {
            }
        }));
    }



    public void bancoServer() throws Exception {

        RMIServer rmiServer = new RMIServer();

    }

    @Override
    public Usuario fazerLogin(String login, String senha) throws RemoteException {
        System.out.println("Realizando login");
        return UsuarioService.realizarLogin(login, senha);
    }

    @Override
    public String criarConta(String login, String senha) throws RemoteException {
        return null;
    }

    @Override
    public Double consultarSaldo(String login) throws RemoteException {
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
