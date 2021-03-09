package com.muchbetter.wallet;

import com.muchbetter.wallet.block.*;
import com.muchbetter.wallet.token.AuthTokenAuthenticator;
import com.muchbetter.wallet.token.TokenUtil;
import org.pac4j.http.client.direct.HeaderClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.error.ServerErrorHandler;
import ratpack.guice.Guice;
import ratpack.pac4j.RatpackPac4j;
import ratpack.server.RatpackServer;
import ratpack.server.RatpackServerSpec;
import ratpack.session.SessionModule;

public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    public static final String AUTH_HEADER = "Authorization";

    private static String HOST_IP = "localhost";
    private static int REDIS_PORT = 6379;

    public static void main(String[] args) {
        boolean argsParsed = parseArguments(args);
        if (argsParsed) {
            try {
                RatpackServer.start(server -> startServer(server));
            } catch (Exception e) {
                LOGGER.error("Unhandled exception thrown. Project may behave in an unexpected manner!");
            }
        }
    }

    public static RatpackServerSpec startServer(RatpackServerSpec server) {
        RedisService redisService = new RedisService(HOST_IP, REDIS_PORT);
        return server.registry(Guice.registry(b -> b
                .bindInstance(ServerErrorHandler.class, (ctx, error) -> {
                            LOGGER.error("Unexpected error", error);
                            ctx.render("Unexpected error - please view server logs");
                        }
                )
                .module(SessionModule.class)
                .add(redisService)
        ))
                .handlers(chain -> {
                    HeaderClient headerClient = new HeaderClient(AUTH_HEADER, new AuthTokenAuthenticator(redisService));
                    chain
                            .all(RatpackPac4j.authenticator(headerClient))
                            .path("login", ctx -> ctx.byMethod(m -> m
                                    .post(new LoginBlock(ctx, ctx.get(RedisService.class).getJedis()))))
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
                });
    }

    // todo more stringent parsing
    private static boolean parseArguments(String[] args) {
        if (args.length > 0) {
            if (args[0] != null) {
                if (args[0].equals("host.docker.internal")) {
                    LOGGER.info("Using host.docker.internal as the host ip address.");
                    LOGGER.info("Note. If this is a linux environment you may need to specify the IP address in the dockerfile.");
                }

                HOST_IP = args[0];
            } else {
                LOGGER.error("Cannot start without host IP, please provide as argument zero to the java -jar command");
                return false;
            }
        }
        if (args.length > 1) {
            if (args[1] != null) {
                String arg1 = args[1];
                boolean isPortProvidedValid = true;
                if (arg1.length() == 4) {
                    try {
                        int port = Integer.parseInt(arg1);
                        REDIS_PORT = port;
                    } catch (NumberFormatException e) {
                        isPortProvidedValid = false;
                    }
                } else {
                    isPortProvidedValid = false;
                }

                if (!isPortProvidedValid) {
                    LOGGER.info("Port provided in arg[1] is invalid");
                    return false;
                }
            } else {
                LOGGER.info("No Redis port provided, using default Redis port");
                return false;
            }
        }
        return true;
    }
}
