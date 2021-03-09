package com.muchbetter.wallet.token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TokenUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenUtil.class);

    /**
     * strips the "Bearer" prepended String from bearer tokens.
     *
     * If the token is in an invalid format (doesn't include the Bearer prepended String), an error is logged, and the orignial string is returned
     *
     * @param token typically, a bearer token incoming from a request header.
     * @return the token without the "Bearer" prepended String.
     */
    public static String stripBearerToken(String token) {
        if (token.startsWith("Bearer ")){
            token = token.substring(7);
        } else {
            LOGGER.error("Invalid format for bearer token provided");
        }

        return token;
    }
}
