package com.muchbetter.wallet.block;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muchbetter.wallet.balance.Balance;
import com.muchbetter.wallet.balance.Currency;
import org.pac4j.core.authorization.authorizer.csrf.DefaultCsrfTokenGenerator;
import org.pac4j.core.context.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.func.Block;
import ratpack.handling.Context;
import ratpack.http.MutableHeaders;
import ratpack.pac4j.RatpackPac4j;
import redis.clients.jedis.Jedis;

import java.security.SecureRandom;

import static com.muchbetter.wallet.Application.AUTH_HEADER;

public class LoginBlock implements Block {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginBlock.class);

    private final Context ctx;
    private final Jedis jedis;

    public LoginBlock(Context ctx, Jedis jedis) {

        this.ctx = ctx;
        this.jedis = jedis;
    }

    @Override
    public void execute() {
        LOGGER.info("login");
        RatpackPac4j.webContext(ctx).then(webContext -> {
            String token = String.valueOf(generateSecureRandomToken());
            MutableHeaders headers = ctx.getResponse().getHeaders();
            headers.add(AUTH_HEADER, token);

            String entryKey = "token:" + token;

            Balance balance = new Balance(0, Currency.GBP);
            ObjectMapper objectMapper = new ObjectMapper();
            String balanceJson = objectMapper.writeValueAsString(balance);

            // todo data should not be coupled with tokens directly. 
            //  There should be some security by obfuscation here by using a separate token for data retrieval,
            //  that is associated with the security token.
            jedis.hset(entryKey, "balance", balanceJson);

            // tokens will be valid for 3 hours or until server close
            jedis.expire(entryKey, 10800);

            ctx.getResponse().send("ok");
        });
    }

    // We're not using this due to it generating the same token each time for the same webcontext
    private String generatePac4JToken(WebContext context) {

        DefaultCsrfTokenGenerator generator = new DefaultCsrfTokenGenerator();
        //because this is based on the webcontext, it will generate the same token each time
        return generator.get(context);
    }

    private Long generateSecureRandomToken() {
        SecureRandom r = new SecureRandom();
        return r.nextLong();
    }
}
