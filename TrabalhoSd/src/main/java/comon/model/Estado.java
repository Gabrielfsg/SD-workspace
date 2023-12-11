package comon.model;

import java.io.*;

public class Estado implements Serializable {

    private byte[] usuarios;
    private byte[] transferencias;

    private int versaoBanco;

    public int getVersaoBanco() {
        return versaoBanco;
    }

    public void setVersaoBanco(int versaoBanco) {
        this.versaoBanco = versaoBanco;
    }

    // Construtor vazio
    public Estado() {
    }

    // Métodos getters e setters
    public byte[] getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(byte[] usuarios) {
        this.usuarios = usuarios;
    }

    public byte[] getTransferencias() {
        return transferencias;
    }

    public void setTransferencias(byte[] transferencias) {
        this.transferencias = transferencias;
    }


    public Estado(int versaoBanco) throws IOException {
        File file = new File("usuario.json");
//        File file = new File("/home/daniel/Documentos/sd/SD-workspace/TrabalhoSd/usuario.json");
        BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));
        this.setUsuarios(stream.readAllBytes());
        stream.close();
        file = new File("transferencias.json");
//        file = new File("/home/daniel/Documentos/sd/SD-workspace/TrabalhoSd/transferencias.json");
        stream = new BufferedInputStream(new FileInputStream(file));
        this.setTransferencias(stream.readAllBytes());
        stream.close();
        this.setVersaoBanco(versaoBanco);
    }

}
