package com.muchbetter.wallet;

public class RedisConfig {
    private final String host;
    private final int port;

    public RedisConfig(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
