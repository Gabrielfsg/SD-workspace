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
import org.jgroups.blocks.*;
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
        if (this.souCoordenador()) {
            this.souCordenador = true;
            try {
                System.out.println("Novo Cordenador!");
                this.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            this.souCordenador = false;
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
            File file = new File("usuario.json");
            BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));
            estado.setUsuarios(stream.readAllBytes());
            stream.close();
            file = new File("transferencias.json");
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

            File file = new File("usuario.json");
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
            this.iniciarCanal();
            if (this.souCoordenador()) {
                souCordenador = true;
            }
            this.iniciarBanco();
            this.sincronizar();
            channel.close();
        } catch (Exception erro) {
            // DEBUG
            System.out.println("ERRO: Server " + erro.getMessage());
            erro.printStackTrace();
        }
    }

    private void sincronizar() throws IOException {
        while (channel.getView().getMembers().size() < 3){
            if(channel.getView().getMembers().size() >= 3){
                break;
            }
        }

        Address enderecoMaior = encontrarMaiorVersao();
        Estado estado = new Estado(obterVersaoRemota(enderecoMaior));
        List<Address> membros = channel.getView().getMembers();
        membros.remove(enderecoMaior);
        try {
            obterDespachante().callRemoteMethods(membros, "desfazMudancasParaOriginal", new Object[]{estado}, new Class[]{Estado.class}, new RequestOptions(ResponseMode.GET_NONE, 2000));
            obterDespachante().callRemoteMethods(membros, "atualizaSincronizado", null, null, new RequestOptions(ResponseMode.GET_NONE, 2000));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        while (!estaSincronizado){
            Util.sleep(100);
        }

        channel.getState();
        while (true) {
        }
    }

    public void atualizaSincronizado(){
        estaSincronizado = true;
    }

    public Address encontrarMaiorVersao() {
        View view = channel.getView();
        List<Address> membros = view.getMembers();

        Address enderecoMaiorVersao = null;
        int maiorVersao = Integer.MIN_VALUE;

        for (Address membro : membros) {
            if (!membro.equals(channel.getAddress())) {
                try {
                    int versaoRemota = obterVersaoRemota(membro);
                    if (versaoRemota > maiorVersao) {
                        maiorVersao = versaoRemota;
                        enderecoMaiorVersao = membro;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return enderecoMaiorVersao;
    }

    private int obterVersaoRemota(Address enderecoRemoto) {
        try {
            return (int) obterDespachante().callRemoteMethod(enderecoRemoto, "lerArquivo", null, null,null);
        } catch (Exception e) {
            e.printStackTrace();
            return Integer.MIN_VALUE;
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
                this.channel.getState(null, 1000);
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
            atualizarArquivo(String.valueOf(Integer.parseInt(lerArquivo()) + 1));
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
            atualizarArquivo(String.valueOf(Integer.parseInt(lerArquivo()) + 1));
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
            atualizarArquivo(String.valueOf(Integer.parseInt(lerArquivo()) + 1));
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
