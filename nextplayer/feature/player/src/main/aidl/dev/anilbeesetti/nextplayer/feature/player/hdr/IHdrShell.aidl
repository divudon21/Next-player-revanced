package dev.anilbeesetti.nextplayer.feature.player.hdr;

// Only handed to this app by Shizuku. Never exported as an Android service.
interface IHdrShell {
    String execute(String command) = 0;
    void destroy() = 16777114;
}
