package comon.model;

import java.io.Serializable;

public class Saldo implements Serializable {

    private Double saldo;

    private int versaoBanco;

    public int getVersaoBanco() {
        return versaoBanco;
    }

    public void setVersaoBanco(int versaoBanco) {
        this.versaoBanco = versaoBanco;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public Saldo() {}

    public Saldo(Double saldo) {
        this.saldo = saldo;
    }
}
