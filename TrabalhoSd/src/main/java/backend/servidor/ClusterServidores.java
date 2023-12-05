package backend.servidor;

import backend.Servidor;
import backend.auth.TokenManager;
import backend.services.TransferenciaService;
import backend.services.UsuarioService;
import comon.model.Estado;
import comon.model.Saldo;
import comon.model.Transferencia;
import comon.model.Usuario;
import org.jgroups.*;
import org.jgroups.blocks.RequestHandler;
import org.jgroups.blocks.Response;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.blocks.locking.LockService;
import org.jgroups.util.Util;

import java.io.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

public class ClusterServidores implements Receiver, RequestHandler {

    private JChannel channel;
    private RpcDispatcher despachante;
    private LockService servicoTravas;
    private Servidor bancoServer;

    private TokenManager tokenManager;

    private boolean estaSincronizado = false;

    private int versaoBanco;

    private static final String FILE_PATH_VERSAO = "versaoBanco.txt";

    public Address getAdress() {
        return channel.getAddress();
    }

    public void atualiza(){
        channel.getState();
    }

    private boolean souCordenador = false;

    public ClusterServidores() {
        try {
            this.bancoServer = new Servidor();
            this.tokenManager = new TokenManager();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void viewAccepted(View view) {
        System.out.println("View Start");
        if (souCoordenador()) {
            this.souCordenador = true;
            try {
                System.out.println("Novo Cordenador!");
                this.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            this.getEstado();
        }
    }

    public int getVersaoBanco() {
        return versaoBanco;
    }

    public void setVersaoBanco(int versaoBanco) {
        this.versaoBanco = versaoBanco;
    }

    public String lerArquivo() {
        StringBuilder conteudo = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH_VERSAO))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                conteudo.append(linha);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return conteudo.toString();
    }

    public void atualizarArquivo(String novoConteudo) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH_VERSAO))) {
            bw.write(novoConteudo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void receive(Message msg) {
        System.out.println("Mensage braba: " + msg.getSrc());
    }

    public void getState(OutputStream output) {
        try {
            Estado estado = new Estado();
            File file = new File("TrabalhoSd/usuario.json");
            BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));
            estado.setUsuarios(stream.readAllBytes());
            stream.close();
            file = new File("TrabalhoSd/transferencias.json");
            stream = new BufferedInputStream(new FileInputStream(file));
            estado.setTransferencias(stream.readAllBytes());
            stream.close();
            System.out.println("Estado enviado.");
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo(s) inexistente, erro: " + e.getMessage());
            this.channel.disconnect();
            this.iniciarCanal();
        } catch (IOException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    public void setState(InputStream input) throws Exception {
        try {
            Estado estado = (Estado) Util.objectFromStream(new DataInputStream(input));

            File file = new File("TrabalhoSd/usuario.json");
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
            stream.write(estado.getUsuarios());
            stream.flush();
            stream.close();
            file = new File("TrabalhoSd/transferencias.json");
            stream = new BufferedOutputStream(new FileOutputStream(file));
            estado.setTransferencias(estado.getTransferencias());
            stream.flush();
            stream.close();
            System.out.println("Estado Coordenador: " + this.channel.view().getCoord());
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo(s) inexistente, erro: " + e.getMessage());
            this.channel.disconnect();
            this.iniciarCanal();
        } catch (IOException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    @Override
    public Object handle(Message message) throws Exception {
        return null;
    }

    @Override
    public void handle(Message request, Response response) throws Exception {
        RequestHandler.super.handle(request, response);
    }

    public void start() {
        try {
            this.channel = new JChannel("banco.xml").connect("banco");
//            this.channel = new JChannel("/home/daniel/Documentos/sd/SD-workspace/TrabalhoSd/banco.xml").connect("banco");
            this.iniciarCanal();
            this.iniciarBanco();
            this.sincronizar();
            channel.close();
        } catch (Exception erro) {
            // DEBUG
            System.out.println("ERRO: Server " + erro.getMessage());
            erro.printStackTrace();
        }
    }

    private void sincronizar(){
        while (channel.getView().getMembers().size() < 1){
            Util.sleep(1000);
        }

//        while (!estaSincronizado){
//            Util.sleep(100);
//        }
        channel.getState();
        while (true) {
        }
    }

    private void iniciarCanal() {
        boolean iniciouConecxao = false;
        while (!iniciouConecxao) {
            try {
                this.channel.connect("banco");
                this.despachante = new RpcDispatcher(this.channel, this);
                //olhar hello word tipos de cast
                this.despachante.setReceiver(this);
                //Olhar hello word multex ou trava
                this.servicoTravas = new LockService(this.channel);
                iniciouConecxao = true;
                System.out.println("Conectado com Sucesso!");
            } catch (Exception erro) {
                System.out.println("ERRO: Server " + erro.getMessage());
                erro.printStackTrace();
            }
        }
    }

    private void iniciarBanco() {
        try {

            if (souCoordenador()) {
                this.bancoServer.bancoServerRmiStart();
            } else {
                this.channel.getState(null, 10000);
            }
        } catch (Exception erro) {
            // DEBUG
            System.out.println("ERRO: Server " + erro.getMessage());
            erro.printStackTrace();
        }
    }

    private boolean souCoordenador() {
        return (this.channel.getAddress()
                .equals(this.channel.getView().getMembers().get(0)));
    }

    private void getEstado() {
        boolean achouEstado = false;
        while (!achouEstado) {
            try {
                this.channel.getState(null, 10000);
                achouEstado = true;
            } catch (Exception e) {
                System.out.println("Estado não encontrado, erro:" + e.getMessage() + ", tentando novamente...");
                Util.sleep(1500);
            }
        }
    }

    public RpcDispatcher obterDespachante() {
        return this.despachante;
    }


    public void reiniciarMembro(){
        this.channel.disconnect();
        this.iniciarCanal();
    }

    public String fazerLogin(String login, String senha) {
        System.out.println("Realizar login: " + login);
        String token = null;
        Lock trava = this.servicoTravas.getLock(login);
        try {
            trava.lock();
            UsuarioService.realizarLogin(login, senha);
            token = tokenManager.generateToken(login);
            System.out.println("token gerado " + token);
        } catch(Exception e){
            System.out.println("ERRO: " + e.getMessage());
        } finally {
            trava.unlock();
        }
        return token;
    }
    public Usuario criarConta(String login, String senha) {
        Usuario usuario = new Usuario();
        Lock trava = this.servicoTravas.getLock(login);
        try {
            trava.lock();
            usuario = UsuarioService.criarConta(login,senha);
        } catch(Exception e){
            System.out.println("ERRO: " + e.getMessage());
        } finally {
            trava.unlock();
        }
        return usuario;
    }

    public Saldo consultarSaldo(String login){
        System.out.println("Consultar saldo login: " + login);
        Saldo saldo = new Saldo();
        Lock trava = this.servicoTravas.getLock(login);
        try {
            trava.lock();
            saldo = UsuarioService.consultarSaldo(login);
        } catch(Exception e){
            System.out.println("ERRO: " + e.getMessage());
        } finally {
            trava.unlock();
        }
        return saldo;
    }

    public Usuario alterarSenha(String login, String senha) {
        Usuario usuario = new Usuario();
        Lock trava = this.servicoTravas.getLock(login);
        try {
            trava.lock();
            usuario = UsuarioService.alterarSenha(login,senha);
        } catch(Exception e){
            System.out.println("ERRO: " + e.getMessage());
        } finally {
            trava.unlock();
        }
        return usuario;
    }

    public Transferencia fazerTransferencia(Transferencia transferencia) {
        Transferencia transferenciaResp = new Transferencia();
        Lock travaRemetente = this.servicoTravas.getLock(transferencia.getContaRemetente());
        Lock travaDestinatario = this.servicoTravas.getLock(transferencia.getContaDestino());

        try {
            travaRemetente.lock();
            travaDestinatario.lock();
            transferenciaResp = TransferenciaService.fazerTransferencia(transferencia);
        } catch(Exception e){
            System.out.println("ERRO: " + e.getMessage());
        } finally {
            travaRemetente.unlock();
            travaDestinatario.unlock();
        }
        return transferenciaResp;
    }

    public List<Transferencia> extrato(String login){
        System.out.println("Extrato: " + login);
        List<Transferencia> transferencia = new ArrayList<>();
        Lock trava = this.servicoTravas.getLock(login);
        try {
            trava.lock();
            transferencia = TransferenciaService.extrato(login);
        } catch(Exception e){
            System.out.println("ERRO: " + e.getMessage());
        } finally {
            trava.unlock();
        }
        return transferencia;
    }


    public void desfazMudancasParaOriginal(Estado estado) throws RemoteException {
        try {
            File file = new File("transferencias.json");
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
            stream.write(estado.getTransferencias());
            stream.flush();
            stream.close();
            file = new File("usuario.json");
            stream = new BufferedOutputStream(new FileOutputStream(file));
            stream.write(estado.getUsuarios());
            stream.flush();
            stream.close();
            atualizarArquivo(String.valueOf(estado.getVersaoBanco()));
        } catch (Exception e) {
            this.channel.disconnect();
            this.iniciarCanal();
        }
    }
}
