package backend;

import backend.services.TransferenciaService;
import backend.services.UsuarioService;
import backend.servidor.ClusterServidores;
import comon.RMIServer;
import comon.model.Saldo;
import comon.model.Transferencia;
import comon.model.Usuario;

import java.rmi.RemoteException;
import java.rmi.server.*;


public class Servidor extends UnicastRemoteObject implements BancoAPI {

    private RMIServer rmiServer;

    public Servidor() throws RemoteException {
        super();
    }

    public static void iniciaRegistroRMI() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                UnicastRemoteObject.unexportObject(java.rmi.registry.LocateRegistry.getRegistry(1099), true);
            } catch (Exception e) {
            }
        }));
    }

    public static void main(String[] args) {
        Servidor.iniciaRegistroRMI();
        ClusterServidores servidor = new ClusterServidores();
        servidor.start();
    }

    public void bancoServerRmiStart() {
        rmiServer = new RMIServer();
        this.rmiServer.start();
    }

    @Override
    public Usuario fazerLogin(String login, String senha) throws RemoteException {
        System.out.println("Realizando login");
        return UsuarioService.realizarLogin(login, senha);
    }

    @Override
    public Usuario criarConta(String login, String senha) throws RemoteException {
        System.out.println("Criar Conta");
        return UsuarioService.criarConta(login, senha);
    }

    @Override
    public Saldo consultarSaldo(String login) throws RemoteException {
        System.out.println("Consultar Saldo");
        return UsuarioService.consultarSaldo(login);
    }

    @Override
    public Usuario alterarSenha(String login, String senha) throws RemoteException {
        System.out.println("Alterar Dados");
        return UsuarioService.alterarSenha(login, senha);
    }

    @Override
    public Transferencia fazerTransferencia(Transferencia transferencia) throws RemoteException {
        System.out.println("Fazer Tranferência");
        return TransferenciaService.fazerTransferencia(transferencia);
    }
}
