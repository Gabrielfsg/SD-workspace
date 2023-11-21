package frotend;

import backend.BancoAPI;
import comon.ConfiguracoesMulticast;
import comon.model.Usuario;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.*;
import java.util.Scanner;

public class Cliente implements Serializable {

    public static Scanner scanner = new Scanner(System.in);
    public static BancoAPI bancoAPI = null;

    public static void main(String[] args) {
        try {
            String addr = null;
            while (addr == null) {
                addr = obterHostServidor();
            }
            bancoAPI = (BancoAPI) Naming.lookup(addr);
            primeiroMenu();
        } catch (Exception erro) {
            System.out.println("Erro ao iniciar o cliente: " + erro.getMessage());
            erro.printStackTrace();
        }
    }

    public static void primeiroMenu() {
        int opcao = 0;
        Usuario resp;
        String login = "";
        String senha = "";

        while (true){
            try {
                System.out.println("### Bem vindo. ### \n" +
                        "1.Login. \n" +
                        "2.Criar Conta. \n" +
                        "3.Sair. \n" +
                        "Escolha uma opção: ");
                opcao = scanner.nextInt();
                scanner.nextLine();

                if (opcao == 1){
                    resp = login(login,senha);
                    System.out.println(resp);
                }
                else if (opcao == 2){
                    resp = criarConta(login,senha);
                    System.out.println(resp);
                }
                else if (opcao == 3){
                    System.out.println("Saida feita com Sucesso. ");
                    System.exit(0);
                } else {
                    System.out.println("Digite uma opção válida. ");
                }
            } catch (Exception e) {
                System.out.println("Erro ao tentar executar uma operação: " + e.getMessage());
            }

        }
    }

    public static void menuJaLogado(Usuario usuario){
        int opcao = 0;

        while (true){
            try {
                System.out.println("### Bem vindo " + usuario.getLogin() + " ### \n" +
                        "1.Consultar Saldo. \n" +
                        "2.Fazer Transferência. \n" +
                        "3.Alterar Dados. \n" +
                        "4.Sair para tela de login. \n" +
                        "Escolha uma opção: ");
                opcao = scanner.nextInt();
                scanner.nextLine();

                if (opcao == 1){

                }
                else if (opcao == 2){

                }
                else if (opcao == 3) {

                }else if (opcao == 4){

                } else {
                    System.out.println("Digite uma opção válida. ");
                }
            } catch (Exception e) {
                System.out.println("Erro ao tentar executar uma operação: " + e.getMessage());
            }

        }
    }

    public static Usuario login(String login, String senha) {
        System.out.println("### LOGIN ### \n");

        System.out.println("Entre com o login: ");
        login = scanner.nextLine();

        System.out.println("Entre com a senha: ");
        senha = scanner.nextLine();
        try {
            return bancoAPI.fazerLogin(login, senha);
        } catch (IOException e) {
            System.out.println("ERRO: Tentando novamente ");
            try {
                return bancoAPI.fazerLogin(login, senha);
            } catch (RemoteException ex) {
                System.out.println("Houve um erro contate o administrador " + ex.getMessage());
            }
        }
        return new Usuario();
    }


    public static Usuario criarConta(String login, String senha){
        System.out.println("### CRIAR CONTA ### \n");

        System.out.println("Entre com o login: ");
        login = scanner.nextLine();

        System.out.println("Entre com a senha: ");
        senha = scanner.nextLine();

        System.out.println(login);
        System.out.println(senha);
        return null;
    }

    public static String obterHostServidor() {

        MulticastSocket socket = null;
        InetAddress addr = null;
        String rmiAddr = null;

        try {
            socket = new MulticastSocket(ConfiguracoesMulticast.port);
            addr = InetAddress.getByName(ConfiguracoesMulticast.ip);
            socket.joinGroup(addr);

            byte[] bufferSend = ConfiguracoesMulticast.TOKEN_COORDENADOR.getBytes();
            DatagramPacket pedido = new DatagramPacket(bufferSend, bufferSend.length, addr, ConfiguracoesMulticast.port);

            while (true) {
                System.out.println("Pedindo ip coordenador");
                socket.send(pedido);
                byte[] buffer = new byte[256];
                DatagramPacket resposta = new DatagramPacket(buffer, buffer.length, addr, ConfiguracoesMulticast.port);
                socket.receive(resposta);
                String msg = new String(resposta.getData(), 0, resposta.getLength());
                System.out.println("resposta " + msg);

                if(!ConfiguracoesMulticast.TOKEN_COORDENADOR.equals(msg)){
                    rmiAddr = msg;
                    break;
                }
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            System.out.println("Erro ao receber stub: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                socket.leaveGroup(addr);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            socket.close();
        }
        return rmiAddr;
    }
}
