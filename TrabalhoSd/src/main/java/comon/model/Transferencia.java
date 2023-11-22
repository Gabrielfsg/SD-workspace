package comon.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Transferencia implements Serializable {

    private static final String FILE_PATH = "transferencias.json";
    String contaRemetente;
    String contaDestino;
    Double valor;
    LocalDateTime data;

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


}
