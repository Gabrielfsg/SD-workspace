package backend.servidor;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.View;

import java.io.InputStream;
import java.io.OutputStream;

public class ClusterServidores implements Receiver {

    private JChannel channel;

    public ClusterServidores(JChannel channel){
        this.channel = channel;
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

}
