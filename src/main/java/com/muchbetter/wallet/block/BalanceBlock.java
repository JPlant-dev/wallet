package com.muchbetter.wallet.block;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.func.Block;
import ratpack.handling.Context;
import redis.clients.jedis.Jedis;

public class BalanceBlock implements Block {
    private static final Logger LOGGER = LoggerFactory.getLogger(BalanceBlock.class);

    private final Context ctx;
    private final Jedis jedis;
    private final String authToken;

    public BalanceBlock(Context ctx, Jedis jedis, String authToken) {
        this.ctx = ctx;
        this.jedis = jedis;
        this.authToken = authToken;
    }

    @Override
    public void execute(){
        LOGGER.info("balance");

        String userBalanceJson = jedis.hget("token:"+authToken, "balance");
        ctx.render(userBalanceJson);
    }
}
