package backend;

import backend.auth.TokenManager;
import backend.services.TransferenciaService;
import backend.servidor.ClusterServidores;
import comon.RMIServer;
import comon.model.Estado;
import comon.model.Saldo;
import comon.model.Transferencia;
import comon.model.Usuario;
import org.jgroups.Address;
import org.jgroups.blocks.MethodCall;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.util.RspList;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


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
    public String fazerLogin(String login, String senha) throws RemoteException {
        System.out.println("Realizando login");
        String user = null;
        RequestOptions opcoes = new RequestOptions();
        MethodCall methodCall = new MethodCall("fazerLogin", new Object[]{login, senha}, new Class[]{String.class, String.class});
        opcoes.setMode(ResponseMode.GET_FIRST);
        try {
            RspList<String> resposta = servidor.obterDespachante().callRemoteMethods(null, methodCall, opcoes);
            user = resposta.getFirst();
        } catch (Exception e) {
            System.out.println("Erro ao realizar login: " + e.getMessage());
        }
        return user;
    }

    @Override
    public Usuario criarConta(String login, String senha) throws RemoteException {
        System.out.println("Criar Conta");
        Usuario usuario = new Usuario();
        try {
            Estado estado = new Estado(servidor.getVersaoBanco());
            RequestOptions opcoes = new RequestOptions();
            MethodCall methodCall = new MethodCall("criarConta", new Object[]{login, senha}, new Class[]{String.class, String.class});
            opcoes.setMode(ResponseMode.GET_ALL);
            opcoes.setTimeout(4000);
            RspList<Usuario> resposta = servidor.obterDespachante().callRemoteMethods(null, methodCall, opcoes);
            AtomicInteger membrosComErro = new AtomicInteger(0);
            ArrayList<Address> membros = resposta.entrySet().stream()
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toCollection(ArrayList::new));
            resposta.entrySet().forEach(membro -> {
                if (membro.getValue().wasReceived() && membro.getKey().equals(servidor.getAdress())) {
                    usuario.setSenha(membro.getValue().getValue().getSenha());
                    usuario.setLogin(membro.getValue().getValue().getLogin());
                } else {
                    System.out.println("Membro com erro: " + membro.getKey());
                    membrosComErro.addAndGet(1);
                    try {
                        System.out.println("Atualiza Membro");
                        servidor.obterDespachante().callRemoteMethod(membro.getKey(), "atualiza", null, null, new RequestOptions(ResponseMode.GET_FIRST, 2000));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            if (membrosComErro.get() > (membros.size() / 2)) {
                System.out.println("Como não foram todos os membros que concordaram, a mudança será disfeita.");
                opcoes.setMode(ResponseMode.GET_NONE);
                opcoes.setTimeout(300);
                try {
                    servidor.obterDespachante().callRemoteMethods(membros, "desfazMudancasParaOriginal", new Object[]{estado}, new Class[]{Estado.class}, opcoes);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao enviar usuario: " + e.getMessage());
        }
        return usuario;
    }

    @Override
    public Saldo consultarSaldo(String token) throws RemoteException {
        System.out.println("Consultar Saldo");
        if (!validarLogin(token)) {
            return null;
        }
        Saldo saldo = new Saldo();
        RequestOptions opcoes = new RequestOptions();
        MethodCall methodCall = new MethodCall("consultarSaldo", new Object[]{TokenManager.decodeToken(token)}, new Class[]{String.class});
        opcoes.setMode(ResponseMode.GET_ALL);
        opcoes.setTimeout(3000);
        try {
            RspList<Saldo> resposta = servidor.obterDespachante().callRemoteMethods(null, methodCall, opcoes);
            resposta.entrySet().forEach(membro -> {
                if (membro.getValue().wasReceived() && membro.getKey().equals(servidor.getAdress())) {
                    saldo.setSaldo(membro.getValue().getValue().getSaldo());
                } else {
                    try {
                        System.out.println("Atualiza Membro");
                        servidor.obterDespachante().callRemoteMethod(membro.getKey(), "atualiza", null, null, new RequestOptions(ResponseMode.GET_FIRST, 2000));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (Exception e) {
            System.out.println("Erro ao enviar saldo: " + e.getMessage());
        }
        return saldo;
    }

    @Override
    public Usuario alterarSenha(String token, String senha) throws RemoteException {
        System.out.println("Alterar Dados");
        if (!validarLogin(token)) {
            return null;
        }
        Usuario usuario = new Usuario();
        try {
            Estado estado = new Estado(servidor.getVersaoBanco());
            RequestOptions opcoes = new RequestOptions();
            MethodCall methodCall = new MethodCall("alterarSenha", new Object[]{TokenManager.decodeToken(token), senha}, new Class[]{String.class, String.class});
            opcoes.setMode(ResponseMode.GET_ALL);
            opcoes.setTimeout(4000);
            RspList<Usuario> resposta = servidor.obterDespachante().callRemoteMethods(null, methodCall, opcoes);
            AtomicInteger membrosComErro = new AtomicInteger(0);
            ArrayList<Address> membros = resposta.entrySet().stream()
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toCollection(ArrayList::new));
            resposta.entrySet().forEach(membro -> {
                if (membro.getValue().wasReceived() && membro.getKey().equals(servidor.getAdress())) {
                    usuario.setSenha(membro.getValue().getValue().getSenha());
                } else {
                    System.out.println("Membro com erro: " + membro.getKey());
                    membrosComErro.addAndGet(1);
                    try {
                        System.out.println("Atualiza Membro");
                        servidor.obterDespachante().callRemoteMethod(membro.getKey(), "atualiza", null, null, new RequestOptions(ResponseMode.GET_FIRST, 2000));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            if (membrosComErro.get() != 0) {
                System.out.println("Como não foram todos os membros que concordaram, a mudança será disfeita.");
                opcoes.setMode(ResponseMode.GET_NONE);
                opcoes.setTimeout(300);
                try {
                    servidor.obterDespachante().callRemoteMethods(membros, "desfazMudancasParaOriginal", new Object[]{estado}, new Class[]{Estado.class}, opcoes);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao enviar usuario: " + e.getMessage());
        }
        return usuario;
    }

    @Override
    public Transferencia fazerTransferencia(String token, Transferencia transferencia) throws RemoteException {
        System.out.println("Fazer Tranferência");
        if (!validarLogin(token)) {
            return null;
        }
        Transferencia tt = new Transferencia();
        try {
            Estado estado = new Estado(servidor.getVersaoBanco());
            RequestOptions opcoes = new RequestOptions();
            MethodCall methodCall = new MethodCall("fazerTransferencia", new Object[]{transferencia}, new Class[]{Transferencia.class});
            opcoes.setMode(ResponseMode.GET_ALL);
            opcoes.setTimeout(4000);
            RspList<Transferencia> resposta = servidor.obterDespachante().callRemoteMethods(null, methodCall, opcoes);
            AtomicInteger membrosComErro = new AtomicInteger(0);
            ArrayList<Address> membros = resposta.entrySet().stream()
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toCollection(ArrayList::new));
            resposta.entrySet().forEach(membro -> {
                if (membro.getValue().wasReceived() && membro.getKey().equals(servidor.getAdress())) {
                        tt.setData(membro.getValue().getValue().getData());
                        tt.setValor(membro.getValue().getValue().getValor());
                        tt.setContaDestino(membro.getValue().getValue().getContaDestino());
                        tt.setContaRemetente(membro.getValue().getValue().getContaRemetente());
                } else {
                    System.out.println("Membro com erro: " + membro.getKey());
                    membrosComErro.addAndGet(1);
                    try {
                        System.out.println("Atualiza Membro");
                        servidor.obterDespachante().callRemoteMethod(membro.getKey(), "atualiza", null, null, new RequestOptions(ResponseMode.GET_FIRST, 2000));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            if ((membrosComErro.get() > (membros.size() / 2))) {
                System.out.println("Como não foram todos os membros que concordaram, a mudança será disfeita.");
                opcoes.setMode(ResponseMode.GET_NONE);
                opcoes.setTimeout(300);
                try {
                    servidor.obterDespachante().callRemoteMethods(membros, "desfazMudancasParaOriginal", new Object[]{estado}, new Class[]{Estado.class}, opcoes);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao realizar tranferencia: " + e.getMessage());
        }
        return tt;
    }

    @Override
    public List<Transferencia> extrato(String token) throws RemoteException {
        System.out.println("Buscar Extrato");
        if (!validarLogin(token)) {
            return null;
        }
        List<Transferencia> transferencia = new ArrayList<>();
        RequestOptions opcoes = new RequestOptions();
        MethodCall methodCall = new MethodCall("extrato", new Object[]{TokenManager.decodeToken(token)}, new Class[]{String.class});
        opcoes.setMode(ResponseMode.GET_ALL);
        opcoes.setTimeout(3000);
        try {
            RspList<List<Transferencia>> resposta = servidor.obterDespachante().callRemoteMethods(null, methodCall, opcoes);
            resposta.entrySet().forEach(membro -> {
                if (membro.getValue().wasReceived() && membro.getKey().equals(servidor.getAdress())) {
                    transferencia.addAll(membro.getValue().getValue());
                } else {
                    try {
                        System.out.println("Atualiza Membro");
                        servidor.obterDespachante().callRemoteMethod(membro.getKey(), "atualiza", null, null, new RequestOptions(ResponseMode.GET_FIRST, 2000));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (Exception e) {
            System.out.println("Erro ao enviar saldo: " + e.getMessage());
        }
        return transferencia;
    }

    private boolean validarLogin(String token) {
        String login = TokenManager.decodeToken(token);
        if (login == null) {
            System.out.println("403: Token invalido, valide o token");
            return false;
        }
        return true;
    }
}
