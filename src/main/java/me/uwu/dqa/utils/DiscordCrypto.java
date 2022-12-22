package me.uwu.dqa.utils;

import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.MGF1ParameterSpec;
import java.util.Base64;

public class DiscordCrypto {
    private static final OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), PSource.PSpecified.DEFAULT);
    private final KeyPair pair;

    @SneakyThrows
    public DiscordCrypto() {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        pair = keyGen.generateKeyPair();
    }

    public String getEncodedKey() {
        return Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());
    }

    @SneakyThrows
    public byte[] decryptPayload(String encryptedPayload) {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, pair.getPrivate(), oaepParams);
        byte[] payload = Base64.getDecoder().decode(encryptedPayload);
        return cipher.doFinal(payload);
    }
}
