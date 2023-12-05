package comon.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Transferencia implements Serializable {

    private static final String FILE_PATH = "transferencias.json";
//    private static final String FILE_PATH = "/home/daniel/Documentos/sd/SD-workspace/TrabalhoSd/transferencias.json";

    private static final String FILE_PATH_VERSAO = "versaoBanco.txt";
//    private static final String FILE_PATH_VERSAO = "/home/daniel/Documentos/sd/SD-workspace/TrabalhoSd/versaoBanco.txt";
    String contaRemetente;
    String contaDestino;
    Double valor;
    LocalDateTime data;

    private int versaoBanco;

    public int getVersaoBanco() {
        return versaoBanco;
    }

    public void setVersaoBanco(int versaoBanco) {
        this.versaoBanco = versaoBanco;
    }

    public Transferencia() {}

    public String getContaRemetente() {
        return contaRemetente;
    }

    public void setContaRemetente(String contaRemetente) {
        this.contaRemetente = contaRemetente;
    }

    public String getContaDestino() {
        return contaDestino;
    }

    public void setContaDestino(String contaDestino) {
        this.contaDestino = contaDestino;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }


    public void salvarTransferencia(Transferencia transferencia) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.findAndRegisterModules();

            File file = new File(FILE_PATH);
            if (!file.exists()) {
                throw new RuntimeException("Arquivo não existe.");
            }

            List<Transferencia> transferencias = listarTodos();
            transferencias.add(transferencia);
            objectMapper.writeValue(file, transferencias);

            System.out.println("Usuários salvos com sucesso no arquivo JSON.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Transferencia> listarTodos() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.registerModule(new JavaTimeModule());

            File file = new File(FILE_PATH);
            if (!file.exists()) {
                throw new RuntimeException("Arquivo não existe.");
            }
            List<Transferencia> transferencias = objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, Transferencia.class));
            return transferencias;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public List<Transferencia> listarTodosPorLogin(String login) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.registerModule(new JavaTimeModule());

            File file = new File(FILE_PATH);
            if (!file.exists()) {
                throw new RuntimeException("Arquivo não existe.");
            }
            List<Transferencia> transferencias = objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, Transferencia.class));
            List<Transferencia> transferenciasFiltradas = transferencias.stream()
                    .filter(transferencia -> transferencia.getContaRemetente().equals(login))
                    .collect(Collectors.toList());

            return transferenciasFiltradas;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public String lerArquivo() {
        StringBuilder conteudo = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH_VERSAO))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                conteudo.append(linha);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return conteudo.toString();
    }

    public void atualizarArquivo(String novoConteudo) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH_VERSAO))) {
            bw.write(novoConteudo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
