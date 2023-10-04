package org.example;

import org.example.model.Usuario;

import java.rmi.*;
import java.util.Scanner;

public class Cliente {

    public static Scanner scanner = new Scanner(System.in);
    public static BancoAPI bancoAPI = null;

    public static void main(String[] args) {
            try {
//                bancoAPI = (BancoAPI) Naming.lookup("rmi://localhost/banco");
                primeiroMenu();
            } catch (Exception erro) {
                System.out.println("Erro ao iniciar o cliente: " + erro.getMessage());
                erro.printStackTrace();
            }
    }

    public static void primeiroMenu(){
        int opcao = 0;
        String resp = "";
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

    public static String login(String login, String senha){
        System.out.println("### LOGIN ### \n");

        System.out.println("Entre com o login: ");
        login = scanner.nextLine();

        System.out.println("Entre com a senha: ");
        senha = scanner.nextLine();

        System.out.println(login);
        System.out.println(senha);
        return "";
    }

    public static String criarConta(String login, String senha){
        System.out.println("### CRIAR CONTA ### \n");

        System.out.println("Entre com o login: ");
        login = scanner.nextLine();

        System.out.println("Entre com a senha: ");
        senha = scanner.nextLine();

        System.out.println(login);
        System.out.println(senha);
        return "";
    }
}
