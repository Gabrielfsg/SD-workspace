package comon.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Transferencia implements Serializable {

    private static final String FILE_PATH = "src/main/java/org/example/comon.model/transferencias.json";
    String contaRemetente;
    String contaDestino;
    Double valor;
    LocalDateTime data;

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
}
