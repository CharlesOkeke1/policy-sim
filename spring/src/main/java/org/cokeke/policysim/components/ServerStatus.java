package org.cokeke.policysim.components;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class ServerStatus {

    private boolean online = true;
    private LocalDateTime serverReadyTime;

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public LocalDateTime getServerReadyTime() {
        return serverReadyTime;
    }

    public void setServerReadyTime(LocalDateTime serverReadyTime) {
        this.serverReadyTime = serverReadyTime;
    }
}
