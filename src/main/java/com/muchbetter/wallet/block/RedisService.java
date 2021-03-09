package com.muchbetter.wallet.block;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.service.Service;
import ratpack.service.StartEvent;
import ratpack.service.StopEvent;
import redis.clients.jedis.Jedis;

public class RedisService implements Service {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisService.class);

    private Jedis jedis;
    private final String host;
    private final int port;

    public RedisService(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void onStart(StartEvent event) {
        LOGGER.info("Creating new Jedis instance");
        jedis = new Jedis(host, port);
    }

    /**
     * All JEDIS data is flushed when the server closes.
     * This fits with the project requirements, but in reality
     * would cause issues with customer data persistence
     * if the server goes down.
     *
     * todo A better alternative would be if user data was not re-created
     * one very login and instead persisted between different logins/logouts
     *
     * @param event
     */
    public void onStop(StopEvent event) {
        jedis.flushAll();
        jedis.close();
        jedis = null;
    }

    public Jedis getJedis() {
        return jedis;
    }
}
