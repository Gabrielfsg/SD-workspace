package backend;

import comon.RMIServer;
import comon.model.Usuario;
import backend.servidor.ClusterServidores;
import org.jgroups.JChannel;

import java.rmi.RemoteException;
import java.rmi.server.*;


public class Servidor extends UnicastRemoteObject implements BancoAPI {

    static JChannel channel;
    static ClusterServidores cluster = new ClusterServidores(channel);

    public Servidor() throws RemoteException {
        super();
    }

    public static void main(String[] args) {

        // Hook que invoca o desligamento do registry quando o programa é encerrado
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                UnicastRemoteObject.unexportObject(java.rmi.registry.LocateRegistry.getRegistry(1099), true);
            } catch (Exception e) {
            }
        }));

        try {
            // talvez usar buildind blocks como uma forma de abstração na comunicação entre
            // os membros do cluster
            channel = new JChannel("TrabalhoSd/src/main/java/backend/config/banco.xml").setReceiver(cluster).connect("banco");
            bancoServer();
            channel.close();

        } catch (Exception erro) {
            // DEBUG
            System.out.println("ERRO: Server " + erro.getMessage());
            erro.printStackTrace();
        }

    }
    private static void bancoServer() throws Exception {

        RMIServer rmiServer = new RMIServer();

        if (souCoordenador()) {
            rmiServer.start();
        }else{
            channel.getState(null,10000);
        }

        while (true) {

        }

    }

    private static boolean souCoordenador() {
        return (channel.getAddress()
                .equals(
                        channel.getView().getMembers().get(0)));
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
