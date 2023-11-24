package backend.servidor;

import backend.Servidor;
import comon.RMIServer;
import comon.model.Estado;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.View;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.RequestHandler;
import org.jgroups.blocks.Response;
import org.jgroups.blocks.locking.LockService;
import org.jgroups.util.Util;

import java.io.*;
import java.rmi.RemoteException;

public class ClusterServidores implements Receiver, RequestHandler {

    private JChannel channel;
    private MessageDispatcher despachante;
    private LockService servicoTravas;
    private Servidor bancoServer;

    final int TAMANHO_MINIMO_CLUSTER = 1;

    private boolean souCordenador = false;

    private RMIServer rmiServer;

    public ClusterServidores() {
        try {
            this.bancoServer = new Servidor();
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


    public void receive(Message msg) {
        System.out.println("Mensage braba: " + msg.getSrc());
    }

    public void getState(OutputStream output) {
        System.out.println("Obteve o estado");
        try {
            Estado estado = new Estado();
            File file = new File("users.json");
            BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));
            estado.setUsuarios(stream.readAllBytes());
            stream.close();
            file = new File("transferencias.json");
            stream = new BufferedInputStream(new FileInputStream(file));
            estado.setTransferencias(stream.readAllBytes());
            stream.close();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(output);
            objectOutputStream.writeObject(estado);
            System.out.println("Envio de Estado Feito com Sucesso.");
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo(s) não encontrado(s), erro: " + e.getMessage());
            this.channel.disconnect();
            this.iniciarCanal();
        } catch (IOException e) {
            System.out.println("Erro ao enviar estado: " + e.getMessage());
        }
    }

    public void setState(InputStream input) throws Exception {

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
            this.iniciarBanco();
            channel.close();
        } catch (Exception erro) {
            // DEBUG
            System.out.println("ERRO: Server " + erro.getMessage());
            erro.printStackTrace();
        }
    }

    private void iniciarCanal() {
        boolean iniciouConecxao = false;
        while (!iniciouConecxao) {
            try {
                this.channel.connect("banco");
                this.despachante = new MessageDispatcher(this.channel);
                //olhar hello word tipos de cast
                this.despachante.setReceiver(this);
                this.despachante.setRequestHandler(this);
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

            while (this.channel.getView().size() < TAMANHO_MINIMO_CLUSTER) {
                Util.sleep(1000);
            }

            if (souCoordenador()) {
                this.bancoServer.bancoServerRmiStart();
            } else {
                this.channel.getState(null, 10000);
            }

            while (true) {
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
}
