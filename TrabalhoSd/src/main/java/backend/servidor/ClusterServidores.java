package backend.servidor;

import backend.Servidor;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.View;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.RequestHandler;
import org.jgroups.blocks.Response;
import org.jgroups.blocks.locking.LockService;
import org.jgroups.util.Util;

import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;

public class ClusterServidores implements Receiver, RequestHandler {

    JChannel channel;
    private MessageDispatcher despachante;
    private LockService servicoTravas;
    private Servidor bancoServer;

    public ClusterServidores(){
        try {
            bancoServer = new Servidor();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Servidor.iniciaRegistroRMI();
        ClusterServidores servidor = new ClusterServidores();
        servidor.start();

    }

    public void viewAccepted(View view){
        System.out.println("View ");
    }


    public void receive(Message msg){
        System.out.println("Mensage braba: " + msg.getSrc());
    }

    public void getState(OutputStream output){
        System.out.println("Obteve o estado");
    }

    public void setState(InputStream input) throws Exception {

    }

    @Override
    public Object handle(Message message) throws Exception {
        ///
//        message.getObject().
        return null;
    }

    @Override
    public void handle(Message request, Response response) throws Exception {
        RequestHandler.super.handle(request, response);
    }

    public void start() {
        try {
            // talvez usar buildind blocks como uma forma de abstração na comunicação entre
            // os membros do cluster
            channel = new JChannel("TrabalhoSd/src/main/java/backend/config/banco.xml").setReceiver(this).connect("banco");
            despachante = new MessageDispatcher(channel);

            //olhar hello word tipos de cast
            despachante.setReceiver(this);
            despachante.setRequestHandler(this);

            //Olhar hello word multex ou trava
            servicoTravas = new LockService(channel) ;


            if (souCoordenador()) {
//                rmiServer.start();
            } else{
                channel.getState(null,10000);
            }

            while (true) {
                Util.sleep(100);
            }
//            channel.close();

        } catch (Exception erro) {
            // DEBUG
            System.out.println("ERRO: Server " + erro.getMessage());
            erro.printStackTrace();
        }
    }




    public boolean souCoordenador() {
        return (channel.getAddress()
                .equals(channel.getView().getMembers().get(0)));
    }
}
