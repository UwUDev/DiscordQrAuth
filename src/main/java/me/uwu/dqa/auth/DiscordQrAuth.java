package me.uwu.dqa.auth;

import lombok.SneakyThrows;
import me.uwu.dqa.exception.AuthentificationTimedOutException;
import me.uwu.dqa.network.AuthSocket;
import me.uwu.dqa.struct.PendingUser;
import okhttp3.*;

import java.net.URI;
import java.net.URLEncoder;

@SuppressWarnings("deprecation")
public class DiscordQrAuth {
    private final boolean debug;
    private final AuthSocket socket;
    private Thread socketHandler;
    private String token;

    @SneakyThrows
    public DiscordQrAuth() {
        this.socket = new AuthSocket(new URI("wss://remote-auth-gateway.discord.gg/?v=2"));
        this.debug = false;
    }

    @SneakyThrows
    public DiscordQrAuth(boolean debug) {
        this.socket = new AuthSocket(new URI("wss://remote-auth-gateway.discord.gg/?v=2"), debug);
        this.debug = debug;
    }

    public void start() {
        if (socketHandler != null) {
            socketHandler.interrupt();
        }

        socketHandler = new Thread(socket::connect);
        socketHandler.start();
        while (socket.getFingerprint() == null) {
            try {
                //noinspection BusyWait
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public String awaitToken() {
        if (token != null)
            return token;

        while (System.currentTimeMillis() < socket.getTimeout() && socket.getTempToken() == null) {
            try {
                //noinspection BusyWait
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (System.currentTimeMillis() >= socket.getTimeout()) {
            throw new AuthentificationTimedOutException();
        }

        authenticate(socket.getTempToken());

        return token;
    }

    public String getFingerprint() {
        return socket.getFingerprint();
    }

    @SneakyThrows
    private void authenticate(String tempToken) {
        OkHttpClient client = new OkHttpClient();

        String payload = "{\"ticket\": \"" + tempToken + "\"}";
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, payload);
        Request request = new Request.Builder()
                .url("https://discord.com/api/v9/users/@me/remote-auth/login")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        if (debug)
            System.out.println("-> " + payload);

        Response response = client.newCall(request).execute();
        //noinspection ConstantConditions
        String responseBody = response.body().string();
        response.close();

        if (debug)
            System.out.println("<- " + response);

        String encryptedToken = responseBody.split("\"encrypted_token\": \"")[1].split("\"")[0];
        token = new String(socket.getCrypto().decryptPayload(encryptedToken));
    }

    public String getQrUrl() {
        return "https://discord.com/ra/" + getFingerprint();
    }

    public String getQrImageUrl() {
        return "https://api.qrserver.com/v1/create-qr-code/?size=800x800&data=" + URLEncoder.encode(getQrUrl());
    }

    public PendingUser getUser() {
        return socket.getUser();
    }
}
