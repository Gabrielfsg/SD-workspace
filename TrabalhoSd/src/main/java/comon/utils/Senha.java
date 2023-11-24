package comon.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Senha {

    public static String gerarSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return bytesToHex(salt);
    }

    public static String gerarHash(String senha, String salt) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            String senhaComSalt = senha + salt;
            byte[] hash = messageDigest.digest(senhaComSalt.getBytes());
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean validarSenha(String senhaDigitada, String hashArmazenado, String salt) {
        // Gera o hash da senha digitada com o salt
        String hashSenhaDigitada = gerarHash(senhaDigitada, salt);

        // Compara o hash gerado com o hash armazenado
        return hashSenhaDigitada.equals(hashArmazenado);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xff & aByte);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
