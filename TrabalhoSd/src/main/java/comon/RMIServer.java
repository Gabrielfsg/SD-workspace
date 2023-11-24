package comon;

import backend.Servidor;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIServer extends Thread {

    public void run() {
        addShutdownHook();

        try (MulticastSocket socket = new MulticastSocket(ConfiguracoesMulticast.port)) {
            InetAddress groupAddress = InetAddress.getByName(ConfiguracoesMulticast.ip);
            socket.joinGroup(groupAddress);

            while (true) {
                DatagramPacket packet = receivePacket(socket);
                System.out.println("Mensagem recebida: " + getMessage(packet));

                if (ConfiguracoesMulticast.TOKEN_COORDENADOR.equals(getMessage(packet))) {
                    createRegistryIfNotExists();

                    String stubMessage = String.format("rmi://%s/banco", InetAddress.getLocalHost().getHostAddress());
                    System.out.println("Retornando stub: " + stubMessage);

                    sendStubMessage(socket, groupAddress, stubMessage);
                }
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Registry registry = LocateRegistry.getRegistry(1099);
                UnicastRemoteObject.unexportObject(registry, true);
            } catch (Exception e) {
                handleException(e);
            }
        }));
    }

    private DatagramPacket receivePacket(MulticastSocket socket) throws Exception {
        byte[] buffer = new byte[256];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        System.out.println("Esperendo pedido de stub...");
        socket.receive(packet);
        return packet;
    }

    private String getMessage(DatagramPacket packet) {
        return new String(packet.getData(), 0, packet.getLength());
    }

    private Servidor createRegistryIfNotExists() throws Exception {
        try {
            LocateRegistry.createRegistry(1099);
            System.out.println("Criando registry.");
        } catch (Exception e) {
            // Registry já criado
        }

        Servidor servidor = new Servidor();
        Naming.rebind("rmi://localhost/banco", servidor);
        return servidor;
    }

    private void sendStubMessage(MulticastSocket socket, InetAddress groupAddress, String message) throws Exception {
        byte[] bufferSend = message.getBytes();
        DatagramPacket packet = new DatagramPacket(bufferSend, bufferSend.length, groupAddress, ConfiguracoesMulticast.port);
        socket.send(packet);
    }

    private void handleException(Exception e) {
        System.out.println("Erro rmiserver: " + e.getMessage());
    }
}
