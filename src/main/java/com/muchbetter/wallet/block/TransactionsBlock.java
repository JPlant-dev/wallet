package com.muchbetter.wallet.block;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.func.Block;
import ratpack.handling.Context;
import redis.clients.jedis.Jedis;

import java.util.List;

public class TransactionsBlock implements Block {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionsBlock.class);

    private final Context ctx;
    private final Jedis jedis;
    private final String authToken;

    public TransactionsBlock(Context ctx, Jedis jedis, String authToken) {

        this.ctx = ctx;
        this.jedis = jedis;
        this.authToken = authToken;
    }

    @Override
    public void execute() {
        LOGGER.info("transaction");

        String entryKey = "Transactions: " + authToken;
        List<String> userTransactions = jedis.lrange(entryKey, 0, -1);

        // List toString returns a valid json string
        ctx.render(userTransactions.toString());
    }
}
