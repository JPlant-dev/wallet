package com.muchbetter.wallet;

import org.junit.jupiter.api.Test;
import ratpack.http.client.ReceivedResponse;
import ratpack.test.embed.EmbeddedApp;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApplicationTransactionTest {

    @Test
    public void whenNoAuthTransactionsHas401() throws Exception {
        EmbeddedApp.of(server -> Application.startServer(server))
                .test(httpClient -> {
                    ReceivedResponse resp = httpClient.get("transactions");
                    assertEquals(401, resp.getStatusCode());
                });
    }

    @Test
    public void whenInvalidAuthTransactionsHas401() throws Exception {
        EmbeddedApp.of(server -> Application.startServer(server))
                .test(httpClient -> {
                    ReceivedResponse loginResp = httpClient.post("login");
                    String authToken = loginResp.getHeaders().get("Authorization") + "INVALID";
                    ReceivedResponse transactionResp = httpClient.requestSpec(rSpec -> rSpec.headers(headers -> headers.set("Authorization", authToken))).get("transactions");
                    assertEquals(401, transactionResp.getStatusCode());
                });
    }

    @Test
    public void whenValidAuthTransactionsRespondsWithData() throws Exception {
        EmbeddedApp.of(server -> Application.startServer(server))
                .test(httpClient -> {
                    ReceivedResponse loginResp = httpClient.post("login");

                    String authToken = "Bearer " + loginResp.getHeaders().get("Authorization");

                    httpClient.requestSpec(rSpec -> rSpec
                            .headers(headers ->
                                    headers.set("Authorization", authToken)
                                            .set("Content-Type", "application/json")
                            )
                            .body(body -> body.text("{\n" +
                                    "\t\"date\": \"2016-12-15T10:44:33Z\",\n" +
                                    "\t\"description\": \"Some item\",\n" +
                                    "\t\"amount\": \"1.45\",\n" +
                                    "\t\"currency\": \"GBP\"\n" +
                                    "}"))
                    ).post("spend");

                    ReceivedResponse transactionResp = httpClient.requestSpec(rSpec -> rSpec.headers(headers -> headers.set("Authorization", authToken))).get("transactions");
                    assertEquals(200, transactionResp.getStatusCode());
                    String s = transactionResp.getBody().getText();
                    assertEquals("[{\"date\":1481798673000,\"description\":\"Some item\",\"amount\":1.45,\"currency\":\"GBP\"}]", s);
                });
    }

    @Test
    public void whenMultipleSpendsTransactionsRespondsWithData() throws Exception {
        EmbeddedApp.of(server -> Application.startServer(server))
                .test(httpClient -> {
                    ReceivedResponse loginResp = httpClient.post("login");

                    String authToken = "Bearer " + loginResp.getHeaders().get("Authorization");

                    httpClient.requestSpec(rSpec -> rSpec
                            .headers(headers ->
                                    headers.set("Authorization", authToken)
                                            .set("Content-Type", "application/json")
                            )
                            .body(body -> body.text("{\n" +
                                    "\t\"date\": \"2016-12-15T10:44:33Z\",\n" +
                                    "\t\"description\": \"Some item\",\n" +
                                    "\t\"amount\": \"1.45\",\n" +
                                    "\t\"currency\": \"GBP\"\n" +
                                    "}"))
                    ).post("spend");

                    httpClient.requestSpec(rSpec -> rSpec
                            .headers(headers ->
                                    headers.set("Authorization", authToken)
                                            .set("Content-Type", "application/json")
                            )
                            .body(body -> body.text("{\n" +
                                    "\t\"date\": \"2020-12-15T10:44:33Z\",\n" +
                                    "\t\"description\": \"Item 2\",\n" +
                                    "\t\"amount\": \"999999\",\n" +
                                    "\t\"currency\": \"GBP\"\n" +
                                    "}"))
                    ).post("spend");

                    ReceivedResponse transactionResp = httpClient.requestSpec(rSpec -> rSpec.headers(headers -> headers.set("Authorization", authToken))).get("transactions");
                    assertEquals(200, transactionResp.getStatusCode());
                    String s = transactionResp.getBody().getText();
                    assertEquals("[{\"date\":1608029073000,\"description\":\"Item 2\",\"amount\":999999.0,\"currency\":\"GBP\"}, {\"date\":1481798673000,\"description\":\"Some item\",\"amount\":1.45,\"currency\":\"GBP\"}]", s);
                });
    }

    @Test
    public void whenTransactionIsPOST405() throws Exception {
        EmbeddedApp.of(server -> Application.startServer(server))
                .test(httpClient -> {
                    ReceivedResponse loginResp = httpClient.post("login");
                    String authToken = loginResp.getHeaders().get("Authorization");
                    ReceivedResponse transactionResp = httpClient.requestSpec(rSpec -> rSpec.headers(headers -> headers.set("Authorization", authToken))).post("transactions");
                    assertEquals(405, transactionResp.getStatusCode());
                });
    }
}
