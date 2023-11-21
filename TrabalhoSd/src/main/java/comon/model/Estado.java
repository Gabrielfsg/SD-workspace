package comon.model;

import java.io.Serializable;

public class Estado implements Serializable {

    private byte[] usuarios;
    private byte[] transferencias;

    // Construtor vazio
    public Estado() {
    }

    // Métodos getters e setters
    public byte[] getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(byte[] users) {
        this.usuarios = users;
    }

    public byte[] getTransferencias() {
        return transferencias;
    }

    public void setTransferencias(byte[] transferencias) {
        this.transferencias = transferencias;
    }
}
