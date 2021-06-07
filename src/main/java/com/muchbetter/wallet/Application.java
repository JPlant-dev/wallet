package com.muchbetter.wallet;

import com.muchbetter.wallet.block.*;
import com.muchbetter.wallet.token.AuthTokenAuthenticator;
import com.muchbetter.wallet.token.TokenUtil;
import org.pac4j.http.client.direct.HeaderClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.error.ServerErrorHandler;
import ratpack.func.Function;
import ratpack.guice.Guice;
import ratpack.guice.internal.GuiceUtil;
import ratpack.pac4j.RatpackPac4j;
import ratpack.registry.Registry;
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
                LOGGER.error(e.getMessage());
            }
        }
    }

    public static Function<Registry, Registry> initialiseRegistry() {
        return Guice.registry(b -> b
                .bindInstance(ServerErrorHandler.class, (ctx, error) -> {
                            LOGGER.error("Unexpected error", error);
                            ctx.render("Unexpected error - please view server logs");
                        }
                )
                .module(SessionModule.class)
                .module(WalletModule.class)
                .module(new RedisConfigModule(new RedisConfig(HOST_IP, REDIS_PORT)))
                //These have to be specified in the WalletModule or Ratpack can't find them
                .add(RedisService.class)
        );
    }

    public static RatpackServerSpec startServer(RatpackServerSpec server) {
        return server.registry(initialiseRegistry())
                .handlers(chain -> {
                            // annoyingly, we need to init the RedisService with these values here
                            // if we're parsing them from command line
//                            chain.getRegistry().get(RedisService.class).init(
//                                    HOST_IP, REDIS_PORT);
                            chain.insert(WalletAction.class);
                }
                );
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
