package me.uwu.dqa;

import me.uwu.dqa.auth.DiscordQrAuth;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        // TODO: 22/12/2022 Custom User-Agent
        DiscordQrAuth auth = new DiscordQrAuth(true);
        auth.start();

        openUrl(auth.getQrImageUrl());

        System.out.println("Token: " + auth.awaitToken());
        System.out.println("User: " + auth.getUser().getTag() + "  [" + auth.getUser().getId() + "]");
        System.out.println("Avatar: " + auth.getUser().getAvatarUrl());
    }

    public static void openUrl(String url) {
        String os = System.getProperty("os.name").toLowerCase();
        Runtime rt = Runtime.getRuntime();
        try {
            if (os.contains("win")) {
                rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else rt.exec("open " + url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}