package comon;

import backend.Servidor;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.Naming;

public class RMIServer extends Thread {

    public void run() {
        try {
            //criando socket UDP multicast
            MulticastSocket socket  = new MulticastSocket(ConfiguracoesMulticast.port);
            InetAddress addr = InetAddress.getByName(ConfiguracoesMulticast.ip);
            socket.joinGroup(addr);

            while (true) {

                byte[] buffer = new byte[256];
                DatagramPacket pacote = new DatagramPacket(buffer, buffer.length,addr, ConfiguracoesMulticast.port);
                System.out.println("Aguardando receber pedido de stub");

                socket.receive(pacote);

                String msg = new String(pacote.getData(),0,pacote.getLength());
                System.out.println("Mensagem recebida: " + msg);

                if(ConfiguracoesMulticast.TOKEN_COORDENADOR.equals(msg)){

                    Servidor obj = new Servidor();
                    try{
                        java.rmi.registry.LocateRegistry.getRegistry(1099);
                        System.out.println("Pegando serviço registry já criado");
                        Naming.rebind("rmi://localhost/banco", obj);
                    }catch(Exception e){
                        System.out.println("Criando registry");
                        java.rmi.registry.LocateRegistry.createRegistry(1099);
                        Naming.bind("rmi://localhost/banco", obj);
                    }

                    msg = String.format("rmi://%s/banco", InetAddress.getLocalHost().getHostAddress());
                    System.out.println("Retornando stub: " + msg);

                    byte[] bufferSend = msg.getBytes();
                    pacote = new DatagramPacket(bufferSend, bufferSend.length,addr, ConfiguracoesMulticast.port);
                    socket.send(pacote);
                }
            }
        } catch (Exception e) {
            System.out.println("Erro rmiserver: " + e.getMessage());
        }
    }
}
