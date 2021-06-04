package com.muchbetter.wallet;

import com.muchbetter.wallet.block.*;
import com.muchbetter.wallet.token.AuthTokenAuthenticator;
import com.muchbetter.wallet.token.TokenUtil;
import org.pac4j.http.client.direct.HeaderClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.pac4j.RatpackPac4j;

public class WalletAction implements Action<Chain> {
    public static final String AUTH_HEADER = "Authorization";
    private static final Logger LOGGER = LoggerFactory.getLogger(WalletAction.class);

    @Override
    public void execute(Chain chain) throws Exception {
        LOGGER.info("Wallet Action");
        chain
                .all(RatpackPac4j.authenticator(new HeaderClient(AUTH_HEADER, new AuthTokenAuthenticator(chain.getRegistry().get(RedisService.class)))))
                .path("login", ctx ->
                {
                    LOGGER.info("handling login");
                    ctx.byMethod(m -> m
                        .post(new LoginBlock(ctx, ctx.get(RedisService.class).getJedis())));
                    LOGGER.info("login handled");
                }
                )
                .all(RatpackPac4j.requireAuth(HeaderClient.class)) // all subsequent handlers must have a valid token
                .path("transactions", ctx -> ctx.byMethod(m -> m
                        .get(new TransactionsBlock(ctx,
                                ctx.get(RedisService.class).getJedis(),
                                TokenUtil.stripBearerToken(ctx.getRequest().getHeaders().get("Authorization")))))
                )
                .path("balance", ctx -> ctx.byMethod(m -> m
                        .get(new BalanceBlock(ctx,
                                ctx.get(RedisService.class).getJedis(),
                                TokenUtil.stripBearerToken(ctx.getRequest().getHeaders().get("Authorization")))))
                )
                .path("spend", ctx -> ctx.byMethod(m -> m
                        .post(new SpendBlock(ctx,
                                ctx.get(RedisService.class).getJedis(),
                                TokenUtil.stripBearerToken(ctx.getRequest().getHeaders().get("Authorization")))))
                );
    }
}
