package io.freddi.hub.utils;

import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.proxy.Player;
import io.freddi.hub.Hub;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ForcedHostUtils extends Utils<ForcedHostUtils> {

    private final Set<String> forcedHosts = new HashSet<>(); // Forced hosts from velocity.toml

    public ForcedHostUtils(Hub hub, Path dataDirectory) {
        super(hub);

        File velocityToml = dataDirectory.resolve("../../velocity.toml").normalize().toFile();
        MessageUtils messageUtils = Utils.util(MessageUtils.class);

        if (velocityToml.exists()) {
            Toml toml = new Toml().read(velocityToml);
            Toml forcedHostsToml = toml.getTable("forced-hosts");

            if (forcedHostsToml != null) {
                for (Map.Entry<String, Object> entry : forcedHostsToml.entrySet()) {
                    String host = entry.getKey();
                    if (host != null) {
                        forcedHosts.add(host.toLowerCase());
                    }
                }
                messageUtils.broadcastDebugMessage("Loaded forced hosts: " + String.join(", ",forcedHosts));
            } else {
                messageUtils.broadcastDebugMessage("No forced hosts found in velocity.toml!");
            }
        } else {
            messageUtils.broadcastDebugMessage("velocity.toml not found! Forced hosts will not be loaded.");
        }
    }

    public boolean isForcedHost(String host) {
        logger.debug("Checking if host is forced: {}", host); // temporary debug log
        return host != null && forcedHosts.contains(host.toLowerCase());
    }

    public boolean isForcedHost(@NotNull Player player) {
        return player.getVirtualHost()
                .map(addr -> isForcedHost(addr.getHostString()))
                .orElse(false);
    }
}
