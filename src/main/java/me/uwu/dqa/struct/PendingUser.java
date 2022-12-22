package me.uwu.dqa.struct;

import lombok.Data;

public @Data class PendingUser {
    private final long id;
    private final String discriminator, avatar, username;

    public String getAvatarUrl() {
        if (avatar == null)
            return "https://cdn.discordapp.com/embed/avatars/" + Integer.parseInt(discriminator) % 5 + ".png";

        if (avatar.isEmpty())
            return "https://cdn.discordapp.com/embed/avatars/" + Integer.parseInt(discriminator) % 5 + ".png";

        if (avatar.startsWith("a_"))
            return "https://cdn.discordapp.com/avatars/" + id + "/" + avatar + ".gif?size=2048&quality=lossless";
        return "https://cdn.discordapp.com/avatars/" + id + "/" + avatar + ".png?size=2048&quality=lossless";
    }

    public String getTag() {
        return username + "#" + discriminator;
    }
}
