package comon;

import backend.Servidor;

import java.net.*;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;

public class RMIServer extends Thread {

    public void run() {
        addShutdownHook();
        try (MulticastSocket socket = new MulticastSocket(ConfiguracoesMulticast.port)) {
            InetAddress groupAddress = InetAddress.getByName(ConfiguracoesMulticast.ip);
            socket.joinGroup(groupAddress);
            String ip = buscarIP();
            System.out.println("ip " + ip);
            System.setProperty("java.rmi.server.hostname", ip);
            Servidor objeto = new Servidor();
            System.out.println("Registry IP: " + ip);
            String enderecoRMI = String.format("rmi://%s/banco", ip);
            try {
                java.rmi.registry.LocateRegistry.getRegistry(1099);
                Naming.rebind(enderecoRMI, objeto);
            } catch (Exception e) {
                java.rmi.registry.LocateRegistry.createRegistry(1099);
                Naming.bind(enderecoRMI, objeto);
            }

            while (true) {
                DatagramPacket packet = receivePacket(socket);
                System.out.println("Mensagem recebida: " + getMessage(packet));

                if (ConfiguracoesMulticast.TOKEN_COORDENADOR.equals(getMessage(packet))) {
                    createRegistryIfNotExists();

                    String stubMessage = String.format("rmi://%s/banco", ip);
                    System.out.println("Stub: " + stubMessage);

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
        e.printStackTrace();
        System.out.println("Erro rmiserver: " + e);
    }

    public static String buscarIP() {
        List<String> ips = new ArrayList<>();

        System.out.println("---- Seleção de Interface de Rede ----");

        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            int indice = 0;
            while (networkInterfaces.hasMoreElements()) {

                NetworkInterface networkInterface = networkInterfaces.nextElement();

                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();

                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();

                    if (address instanceof Inet6Address) {

                        continue;
                    }

                    ips.add(address.getHostAddress());
                    System.out.println(String.format("%d: %s - %s",indice, networkInterface.getName(), address.getHostAddress()));
                    indice++;
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        int op;
        do {
            System.out.println("Escolha a interface de rede adequada: ");
            op = new Scanner(System.in).nextInt();

            if (op < 0 || op >= ips.size()) {
                System.out.println("Opção inválida. Tente novamente.");
            }

        } while (op < 0 || op >= ips.size());

        System.out.println("---------------------------------------");

        return ips.get(op);
    }
}
