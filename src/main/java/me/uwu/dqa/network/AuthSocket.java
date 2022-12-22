package me.uwu.dqa.network;

import lombok.Getter;
import lombok.SneakyThrows;
import me.uwu.dqa.struct.PendingUser;
import me.uwu.dqa.utils.DiscordCrypto;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.security.MessageDigest;
import java.util.Base64;

public class AuthSocket extends WebSocketClient {
    private final boolean debug;
    @Getter
    private final DiscordCrypto crypto;
    @Getter
    private long timeout;
    @Getter
    private String fingerprint;
    @Getter
    private String tempToken;
    @Getter
    private PendingUser user;

    @SneakyThrows
    public AuthSocket(URI serverUri) {
        super(serverUri);
        this.addHeader("Origin", "https://discord.com");
        crypto = new DiscordCrypto();
        debug = false;
    }

    @SneakyThrows
    public AuthSocket(URI serverUri, boolean debug) {
        super(serverUri);
        this.addHeader("Origin", "https://discord.com");
        crypto = new DiscordCrypto();
        this.debug = debug;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {

    }

    @SneakyThrows
    @Override
    public void onMessage(String message) {
        if(debug)
            System.out.println("<- " + message);
        if (message.contains("\"op\":\"hello\"")) {
            timeout = System.currentTimeMillis() + Integer.parseInt(message.split("\"timeout_ms\":")[1].split(",")[0]) - 1000;

            String payload = "{\"op\":\"init\",\"encoded_public_key\":\"" +
                    crypto.getEncodedKey() +
                    "\"}";

            send(payload);
            if(debug)
                System.out.println("-> " + payload);
        } else if (message.contains("\"op\":\"nonce_proof\"")) {
            byte[] decryptedNonce = crypto.decryptPayload(message.split("\"encrypted_nonce\":\"")[1].split("\"")[0]);
            byte[] proof = MessageDigest.getInstance("SHA-256").digest(decryptedNonce);

            String payload =
                    "{\"op\":\"nonce_proof\",\"proof\":\"" +
                    Base64.getUrlEncoder().encodeToString(proof).replace("=", "") +
                    "\"}";

            send(payload);
            if(debug)
                System.out.println("-> " + payload);
        } else if (message.contains("\"op\":\"pending_remote_init\"")) {
            fingerprint = message.split("\"fingerprint\":\"")[1].split("\"")[0];
        } else if (message.contains("\"op\":\"pending_ticket\"")) {
            String[] userData = new String(
                    crypto.decryptPayload(
                            message.split("\"encrypted_user_payload\":\"")[1].split("\"")[0]
                    )
            ).split(":");

            user = new PendingUser(
                    Long.parseLong(userData[0]),
                    userData[1],
                    userData[2],
                    userData[3]
            );
        } else if (message.contains("\"ticket\":\"")) {
            tempToken = message.split("\"ticket\":\"")[1].split("\"")[0];
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {

    }
}
