package com.muchbetter.wallet.block;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muchbetter.wallet.balance.Balance;
import com.muchbetter.wallet.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.func.Block;
import ratpack.handling.Context;
import redis.clients.jedis.Jedis;

import java.io.IOException;

import static ratpack.jackson.Jackson.fromJson;

public class SpendBlock implements Block {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpendBlock.class);

    private final Context ctx;
    private final Jedis jedis;
    private final String authToken;
    private final ObjectMapper objectMapper;

    public SpendBlock(Context ctx, Jedis jedis, String authToken) {
        this.ctx = ctx;
        this.jedis = jedis;
        this.authToken = authToken;
        objectMapper = new ObjectMapper();
    }

    @Override
    public void execute() {
        LOGGER.info("spend");

        ctx.parse(fromJson(Transaction.class))
                .onError(a -> {
                    LOGGER.error("Invalid Spend request submitted");
                })
                .then(transaction -> {
                    String entryKey = "Transactions: " + authToken;

                    applyTransactionToBalance(transaction.getAmount());

                    ObjectMapper objectMapper = new ObjectMapper();
                    // add as a list of these
                    //todo do we want to serialise to json or bytes?
                    String transactionJson = objectMapper.writeValueAsString(transaction);
                    jedis.lpush(entryKey, transactionJson);

                    // User data will stick around potentially longer than the users token is valid.
                    // todo we should look into adding a hook that removes corresponding data
                    //  when the token expires if we aren't persisting users
                    jedis.expire(entryKey, 10800);
                    ctx.getResponse().send("ok");
                });
    }

    /**
     * This function is synchronised as we need to ensure that no
     * transactions are applied inbetween one threads retrieval and application of a balance
     * @param transactionAmount
     * @throws JsonProcessingException
     */
    private synchronized void applyTransactionToBalance(float transactionAmount) throws IOException {
        String balanceKey = "token:"+authToken;
        String userBalanceJson = jedis.hget(balanceKey, "balance");

        Balance balance = objectMapper.readValue(userBalanceJson, Balance.class);

        // a positive transaction adds to this balance, a negative one would take away
        float newBalanceAmount = balance.getBalance() + transactionAmount;

        Balance newBalance = new Balance(newBalanceAmount, balance.getCurrency());

        String balanceJson = objectMapper.writeValueAsString(newBalance);
        jedis.hset(balanceKey, "balance", balanceJson);
    }
}
