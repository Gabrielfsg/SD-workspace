package backend;

import backend.services.TransferenciaService;
import backend.services.UsuarioService;
import backend.servidor.ClusterServidores;
import comon.RMIServer;
import comon.model.Saldo;
import comon.model.Transferencia;
import comon.model.Usuario;
import frotend.Cliente;
import org.jgroups.blocks.MethodCall;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.util.RspList;

import java.rmi.RemoteException;
import java.rmi.server.*;
import java.util.List;


public class Servidor extends UnicastRemoteObject implements BancoAPI {

    private RMIServer rmiServer;

    private static ClusterServidores servidor;

    public Servidor() throws RemoteException {
        super();
    }

    public static void main(String[] args) {
        servidor = new ClusterServidores();
        servidor.start();
    }

    public void bancoServerRmiStart() {
        rmiServer = new RMIServer();
        this.rmiServer.start();
    }

    @Override
    public Usuario fazerLogin(String login, String senha) throws RemoteException {
        System.out.println("Realizando login");
        Usuario user = new Usuario();
        RequestOptions opcoes = new RequestOptions();
        MethodCall methodCall1 = new MethodCall("getLogados", new Object[] {}, new Class[] { });
        opcoes.setMode(ResponseMode.GET_FIRST);
        try {
            RspList<List<String>> resposta1 = servidor.obterDespachante().callRemoteMethods(null, methodCall1, opcoes);
            List<String> logados = resposta1.getFirst();
            System.out.println("Logados ..." + logados.toString());
            if (logados.contains(login)) {
                user.setLogin(login);
                System.out.println("Usuario já está logado");
            } else {
                MethodCall methodCall = new MethodCall("fazerLogin", new Object[] { login, senha }, new Class[] { String.class, String.class });
                opcoes.setMode(ResponseMode.GET_FIRST);
                try {
                    RspList<Usuario> resposta = servidor.obterDespachante().callRemoteMethods(null, methodCall, opcoes);
                    user = resposta.getFirst();
                } catch (Exception e) {
                    System.out.println("Erro ao realizar login: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao realizar login: " + e.getMessage());
        }

        return user;
    }

    @Override
    public Usuario criarConta(String login, String senha) throws RemoteException {
        System.out.println("Criar Conta");
        return UsuarioService.criarConta(login, senha);
    }

    @Override
    public Saldo consultarSaldo(String login) throws RemoteException {
        System.out.println("Consultar Saldo");
        Saldo saldo = new Saldo();
        RequestOptions opcoes = new RequestOptions();
        MethodCall methodCall = new MethodCall("consultarSaldo", new Object[] { login }, new Class[] { String.class });
        opcoes.setMode(ResponseMode.GET_FIRST);
        try {
            RspList<Saldo> resposta = servidor.obterDespachante().callRemoteMethods(null, methodCall, opcoes);
            saldo = resposta.getFirst();
        } catch (Exception e) {
            System.out.println("Erro ao enviar saldo: " + e.getMessage());
        }
        return saldo;
    }

    @Override
    public Usuario alterarSenha(String login, String senha) throws RemoteException {
        System.out.println("Alterar Dados");
        Usuario usuario = new Usuario();
        RequestOptions opcoes = new RequestOptions();
        MethodCall methodCall = new MethodCall("alterarSenha", new Object[] { login, senha }, new Class[] { String.class, String.class  });
        opcoes.setMode(ResponseMode.GET_ALL);
        try {
            RspList<Usuario> resposta = servidor.obterDespachante().callRemoteMethods(null, methodCall, opcoes);
            usuario = resposta.getFirst();
        } catch (Exception e) {
            System.out.println("Erro ao enviar saldo: " + e.getMessage());
        }
        return usuario;
    }

    @Override
    public Transferencia fazerTransferencia(Transferencia transferencia) throws RemoteException {
        System.out.println("Fazer Tranferência");
        return TransferenciaService.fazerTransferencia(transferencia);
    }

    @Override
    public List<Transferencia> extrato(String login) throws RemoteException {
        System.out.println("Buscar Extrato");
        return TransferenciaService.extrato(login);
    }
}
