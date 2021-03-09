package com.muchbetter.wallet;

import org.junit.jupiter.api.Test;
import ratpack.http.client.ReceivedResponse;
import ratpack.test.embed.EmbeddedApp;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationLoginTest {
    @Test
    public void whenLoginCalledTokenIsCreated() throws Exception {
        EmbeddedApp.of(server -> Application.startServer(server))
                .test(httpClient -> {
                    ReceivedResponse resp = httpClient.post("login");
                    assertTrue(resp.getHeaders().contains("Authorization"));
                    assertTrue(resp.getHeaders().get("Authorization").length() > 0);
                });
    }

    @Test
    public void whenLoginIsGET405() throws Exception {
        EmbeddedApp.of(server -> Application.startServer(server))
                .test(httpClient -> {
                    ReceivedResponse loginResp = httpClient.get("login");
                    assertEquals(405, loginResp.getStatusCode());
                });
    }

    @Test
    public void whenLoginTwiceUniqueKeysReturned() throws Exception {
        EmbeddedApp.of(server -> Application.startServer(server))
                .test(httpClient -> {
                    ReceivedResponse resp = httpClient.post("login");
                    String authToken1 = resp.getHeaders().get("Authorization");
                    ReceivedResponse resp2 = httpClient.post("login");
                    String authToken2 = resp2.getHeaders().get("Authorization");

                    assertNotEquals(authToken1, authToken2);
                });
    }

    @Test
    public void whenRequestAttemptedDifferentServerInstanceLoginIsNotValid() throws Exception {
        StringBuilder generatedAuthToken = new StringBuilder();
        EmbeddedApp.of(server -> Application.startServer(server))
                .test(httpClient -> {
                    ReceivedResponse resp = httpClient.post("login");
                    generatedAuthToken.append(resp.getHeaders().get("Authorization"));
                });

        EmbeddedApp.of(server -> Application.startServer(server))
                .test(httpClient -> {
                    ReceivedResponse transactionResp = httpClient.requestSpec(rSpec -> rSpec.headers(headers -> headers.set("Authorization", generatedAuthToken))).get("balance");
                    assertEquals(401, transactionResp.getStatusCode());
                });
    }
}