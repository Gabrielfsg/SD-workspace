package comon;

import backend.Servidor;

import java.net.*;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;

public class RMIServer extends Thread {

    public void run() {
        addShutdownHook();
        try (MulticastSocket socket = new MulticastSocket(ConfiguracoesMulticast.port)) {
            InetAddress groupAddress = InetAddress.getByName(ConfiguracoesMulticast.ip);
            socket.joinGroup(groupAddress);
            String ip = buscarIP();
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

    public static String buscarIP() {
        try {
            Enumeration<NetworkInterface> interfacesRede = NetworkInterface.getNetworkInterfaces();
            while (interfacesRede.hasMoreElements()) {
                NetworkInterface interfaceRede = interfacesRede.nextElement();
                if (isInterfaceValida(interfaceRede)) {
                    String enderecoIP = buscarEnderecoIPNaInterface(interfaceRede);
                    if (enderecoIP != null && !enderecoIP.isEmpty()) {
                        return enderecoIP;
                    }
                }
            }
            return null;
        } catch (SocketException e) {
            System.err.println("Erro ao obter o endereço IP: " + e.getMessage());
            return null;
        }
    }

    private static boolean isInterfaceValida(NetworkInterface interfaceRede) {
        try {
            return (interfaceRede.getName().equals("eno1") || interfaceRede.getName().contains("eth")) &&
                    !interfaceRede.isLoopback() && interfaceRede.isUp();
        } catch (SocketException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String buscarEnderecoIPNaInterface(NetworkInterface interfaceRede) {
        Enumeration<InetAddress> enderecos = interfaceRede.getInetAddresses();
        while (enderecos.hasMoreElements()) {
            InetAddress endereco = enderecos.nextElement();
            if (endereco instanceof Inet4Address) {
                String enderecoIP = endereco.getHostAddress();
                System.out.println(" " + enderecoIP);
                return enderecoIP;
            }
        }
        return null;
    }
}
