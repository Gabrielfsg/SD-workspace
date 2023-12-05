package frotend;

import backend.BancoAPI;
import backend.auth.TokenManager;
import comon.ConfiguracoesMulticast;
import comon.model.Saldo;
import comon.model.Transferencia;
import comon.model.Usuario;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
        String opcaoDig = "";
        int opcao = 0;
        String resp;
        String login = "";
        String senha = "";

        while (true) {
            try {
                System.out.println("### Bem vindo. ### \n" +
                        "1.Login. \n" +
                        "2.Criar Conta. \n" +
                        "3.Sair. \n" +
                        "Escolha uma opção: ");
                opcaoDig = scanner.nextLine();
                opcao = Integer.parseInt(opcaoDig);

                if (opcao == 1) {
                    resp = login(login, senha);
                    System.out.println("resposta " + resp);
                    if (resp != null) {
                        System.out.println("Login Feito com Sucesso.");
                        System.out.println("Redirecionando...");
                        menuJaLogado(resp);
                    } else {
                        System.out.println("Usuario ou senha incorretos");
                    }
                } else if (opcao == 2) {
                    resp = criarConta(login, senha);
                    if (resp != null) {
                        System.out.println("Usuário criado com Sucesso.");
                    } else {
                        System.out.println("Usuario já existe.");
                    }
                } else if (opcao == 3) {
                    System.out.println("Saida feita com Sucesso. ");
                    System.exit(0);
                } else {
                    System.out.println("Digite uma opção válida. ");
                }
            } catch (NumberFormatException e) {
                System.out.println("Digite uma opção válida. ");
            } catch (Exception e) {
                System.out.println("Erro ao tentar executar uma operação: " + e.getMessage());
            }

        }
    }

    public static void menuJaLogado(String usuario) {
        String opcaoDig = "";
        int opcao = 0;
        Saldo saldo;
        Usuario resp;
        String senha = "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        while (true) {
            try {
                System.out.println("### Bem vindo " + TokenManager.decodeToken(usuario) + " ### \n" +
                        "1.Consultar Saldo. \n" +
                        "2.Fazer Transferência. \n" +
                        "3.Alterar Senha. \n" +
                        "4.Extrato. \n" +
                        "5.Sair para tela de login. \n" +
                        "Escolha uma opção: ");
                opcaoDig = scanner.nextLine();
                opcao = Integer.parseInt(opcaoDig);

                if (opcao == 1) {
                    saldo = consultarSaldo(usuario);
                    if (saldo != null) {
                        System.out.println("Usuario: " + TokenManager.decodeToken(usuario));
                        System.out.println("Saldo: " + saldo.getSaldo());
                    } else {
                        System.out.println("Erro ao consultar o saldo, usuario não existe na base de dados");
                    }
                } else if (opcao == 2) {
                    Transferencia transferencia = new Transferencia();
                    transferencia.setContaRemetente((String) TokenManager.decodeToken(usuario));
                    transferencia = fazerTransferencia(usuario, transferencia);
                    if (transferencia != null) {
                        String dataHoraFormatada = transferencia.getData().format(formatter);
                        System.out.println("Tranferência concluida com sucesso.");
                        System.out.println("Remetente: " + transferencia.getContaRemetente());
                        System.out.println("Destinatario: " + transferencia.getContaDestino());
                        System.out.println("Valor: " + transferencia.getValor());
                        System.out.println("Data e Hora: " + dataHoraFormatada);
                    }
                } else if (opcao == 3) {
                    resp = alterarSenha(usuario, senha);
                    if (resp != null) {
                        System.out.println("Senha Alterada com Sucesso.");
                    }
                } else if (opcao == 4) {
                    List<Transferencia> extrato = extrato(usuario);
                    extrato.forEach(transferencia -> {
                        String dataHoraFormatada = transferencia.getData().format(formatter);
                        System.out.println("################################################");
                        System.out.println("Remetente: " + transferencia.getContaRemetente());
                        System.out.println("Destinatario: " + transferencia.getContaDestino());
                        System.out.println("Valor: " + transferencia.getValor());
                        System.out.println("Data e Hora: " + dataHoraFormatada);
                        System.out.println("################################################");
                    });

                } else if (opcao == 5) {
                    primeiroMenu();
                } else {
                    System.out.println("Digite uma opção válida. ");
                }
            } catch (NumberFormatException e) {
                System.out.println("Digite uma opção válida. ");
            } catch (Exception e) {
                System.out.println("Erro ao tentar executar uma operação: " + e.getMessage());
            }
        }
    }

    public static String login(String login, String senha) {
        System.out.println("### LOGIN ### \n");

        System.out.println("Entre com o login: ");
        login = scanner.nextLine();

        System.out.println("Entre com a senha: ");
        senha = scanner.nextLine();
        try {
            return bancoAPI.fazerLogin(login, senha);
        } catch (IOException e) {
            System.out.println("ERRO: " + e.getMessage());
        } catch (RuntimeException r) {
            System.out.println("ERRO: " + r.getMessage());
        }
        return null;
    }


    public static String criarConta(String login, String senha) {
        System.out.println("### CRIAR CONTA ### \n");

        System.out.println("Entre com o login: ");
        login = scanner.nextLine();

        System.out.println("Entre com a senha: ");
        senha = scanner.nextLine();
        String usuario = null;
        try {
            usuario = bancoAPI.criarConta(login, senha).getLogin();
        } catch (IOException e) {
            System.out.println("ERRO: " + e.getMessage());
        } catch (RuntimeException r) {
            System.out.println("ERRO: " + r.getMessage());
        }
        return usuario;
    }

    public static Usuario alterarSenha(String login, String senha) {
        System.out.println("### Alterar Senha ### \n");

        System.out.println("Entre com a nova senha: ");
        senha = scanner.nextLine();

        try {
            return bancoAPI.alterarSenha(login, senha);
        } catch (IOException e) {
            System.out.println("ERRO: " + e.getMessage());
        } catch (RuntimeException r) {
            System.out.println("ERRO: " + r.getMessage());
        }
        return new Usuario();
    }

    public static Saldo consultarSaldo(String login) {
        System.out.println("### Consultar Saldo ### \n");
        try {
            return bancoAPI.consultarSaldo(login);
        } catch (IOException e) {
            System.out.println("ERRO: " + e.getMessage());
        }
        return null;
    }

    public static List<Transferencia> extrato(String login) {
        System.out.println("### Extrato ### \n");
        try {
            return bancoAPI.extrato(login);
        } catch (IOException e) {
            System.out.println("ERRO: " + e.getMessage());
        }
        return null;
    }

    public static Transferencia fazerTransferencia(String token, Transferencia transferencia) {
        String destinatario = "";
        System.out.println("### Consultar Saldo ### \n");

        System.out.println("Entre com a destinatario: ");
        destinatario = scanner.nextLine();
        transferencia.setContaDestino(destinatario);

        System.out.println("Entre com o valor: ");
        transferencia.setValor(Double.valueOf(scanner.nextLine()));

        transferencia.setData(LocalDateTime.now());
        try {
            return bancoAPI.fazerTransferencia(token, transferencia);
        } catch (IOException e) {
            System.out.println("ERRO: " + e.getMessage());
        } catch (RuntimeException r) {
            System.out.println("ERRO: " + r.getMessage());
        }
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

                if (!ConfiguracoesMulticast.TOKEN_COORDENADOR.equals(msg)) {
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
