package com.muchbetter.wallet.token;

import com.muchbetter.wallet.block.RedisService;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.profile.CommonProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

public class AuthTokenAuthenticator implements Authenticator {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenAuthenticator.class);

    private final RedisService redisService;

    public AuthTokenAuthenticator(RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public void validate(Credentials credentials, WebContext context) {
        LOGGER.info("validating");

        String token = ((TokenCredentials) credentials).getToken();
        // validate token
        token = TokenUtil.stripBearerToken(token);

        // check the token and create a profile
        Jedis jedis = redisService.getJedis();

        // tokens will be valid for 3 hours from creation, if not valid it won't exist here
        boolean tokenExists = jedis.exists("token:"+token);

        //if we have a userBalance, the token was valid
        if (tokenExists) {
            LOGGER.info("validating - Token does exist");
            CommonProfile profile = new CommonProfile();
            //todo use an id that is not the token
            profile.setId(token);
            // save in the credentials to be passed to the default AuthenticatorProfileCreator
            credentials.setUserProfile(profile);
        } else {
            LOGGER.error("validating - Invalid token provided");
        }
    }
}
