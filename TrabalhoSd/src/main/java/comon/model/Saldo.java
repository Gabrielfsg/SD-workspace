package comon.model;

import java.io.Serializable;

public class Saldo implements Serializable {

    private Double saldo;

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
