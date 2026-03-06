package org.pollub.feedback.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

//Lab1 - Singleton 2 Start
/**
 * Lazy Singleton (Double-Checked Locking) for managing banned IP addresses.
 * The list is loaded into memory only upon the first request to check an IP.
 */
public class IpBlacklist {

    private static volatile IpBlacklist instance;
    private final Set<String> bannedIps;

    private IpBlacklist() {
        Set<String> ips = new HashSet<>();
        try (java.io.InputStream is = getClass().getClassLoader().getResourceAsStream("ipblacklist.txt")) {
            if (is != null) {
                try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (!line.isEmpty() && !line.startsWith("#")) {
                            ips.add(line);
                        }
                    }
                }
            } else {
                System.err.println("Warning: ipblacklist.txt not found. IP blacklist will be empty.");
            }
        } catch (java.io.IOException e) {
            System.err.println("Error reading ipblacklist.txt: " + e.getMessage());
        }
        this.bannedIps = Collections.unmodifiableSet(ips);
    }

    public static IpBlacklist getInstance() {
        if (instance == null) {
            synchronized (IpBlacklist.class) {
                if (instance == null) {
                    instance = new IpBlacklist();
                }
            }
        }
        return instance;
    }

    public boolean isBanned(String ipAddress) {
        if (ipAddress == null) {
            return false;
        }
        return bannedIps.contains(ipAddress);
    }
}
//Lab1 End Singleton 2
