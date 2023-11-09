package org.example.backend;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.RequestHandler;

public class ServidorJBanco extends ReceiverAdapter implements RequestHandler
{

    private JChannel canal;

    private MessageDispatcher despachante;

    @Override
    public Object handle(Message msg) throws Exception {
        return null;
    }

    private void iniciar() throws Exception{

        canal =new JChannel("org/example/backend/config/bancoConfig.xml");

        canal.setReceiver(this);

        canal.connect("Banco");

        despachante=new MessageDispatcher(canal, this, this, this);

        canal.connect("Banco");
        principal();
        canal.close();

    }

    private Object principal(){
        return null;
    }
}