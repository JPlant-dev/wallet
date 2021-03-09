package com.muchbetter.wallet;

import org.junit.jupiter.api.Test;
import ratpack.http.client.ReceivedResponse;
import ratpack.test.embed.EmbeddedApp;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationBalanceTest {

    //todo mock redis so that running tests doesn't require a running redis instance

    @Test
    public void whenValidBalanceRespondsWithData() throws Exception {
        EmbeddedApp.of(server -> Application.startServer(server))
                .test(httpClient -> {
                    ReceivedResponse loginResp = httpClient.post("login");

                    String authToken = "Bearer " + loginResp.getHeaders().get("Authorization");

                    ReceivedResponse transactionResp = httpClient.requestSpec(rSpec -> rSpec.headers(headers -> headers.set("Authorization", authToken))).get("balance");
                    assertEquals(200, transactionResp.getStatusCode());
                    String s = transactionResp.getBody().getText();
                    assertEquals("{\"balance\":0.0,\"currency\":\"GBP\"}", s);
                });
    }

    @Test
    public void whenSpendAppliedBalanceIsAffected() throws Exception {
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

                    ReceivedResponse transactionResp = httpClient.requestSpec(rSpec -> rSpec.headers(headers -> headers.set("Authorization", authToken))).get("balance");
                    assertEquals(200, transactionResp.getStatusCode());
                    String s = transactionResp.getBody().getText();
                    assertEquals("{\"balance\":1.45,\"currency\":\"GBP\"}", s);
                });
    }

    @Test
    public void whenMultipleSpendsAppliedBalanceIsAffected() throws Exception {
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
                                    "\t\"date\": \"2016-12-15T10:44:33Z\",\n" +
                                    "\t\"description\": \"Some item\",\n" +
                                    "\t\"amount\": \"20.0\",\n" +
                                    "\t\"currency\": \"GBP\"\n" +
                                    "}"))
                    ).post("spend");

                    ReceivedResponse transactionResp = httpClient.requestSpec(rSpec -> rSpec.headers(headers -> headers.set("Authorization", authToken))).get("balance");
                    assertEquals(200, transactionResp.getStatusCode());
                    String s = transactionResp.getBody().getText();
                    assertEquals("{\"balance\":21.45,\"currency\":\"GBP\"}", s);
                });
    }

    /**
     * This test makes an assumption that balances can be negative
     * (i.e. the user owes money to the wallet)
     * @throws Exception
     */
    @Test
    public void whenMixedSignsSpendsAppliedBalanceIsAffected() throws Exception {
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
                                    "\t\"amount\": \"-40\",\n" +
                                    "\t\"currency\": \"GBP\"\n" +
                                    "}"))
                    ).post("spend");
                    httpClient.requestSpec(rSpec -> rSpec
                            .headers(headers ->
                                    headers.set("Authorization", authToken)
                                            .set("Content-Type", "application/json")
                            )
                            .body(body -> body.text("{\n" +
                                    "\t\"date\": \"2016-12-15T10:44:33Z\",\n" +
                                    "\t\"description\": \"Some item\",\n" +
                                    "\t\"amount\": \"20.0\",\n" +
                                    "\t\"currency\": \"GBP\"\n" +
                                    "}"))
                    ).post("spend");

                    ReceivedResponse transactionResp = httpClient.requestSpec(rSpec -> rSpec.headers(headers -> headers.set("Authorization", authToken))).get("balance");
                    assertEquals(200, transactionResp.getStatusCode());
                    String s = transactionResp.getBody().getText();
                    assertEquals("{\"balance\":-20.0,\"currency\":\"GBP\"}", s);
                });
    }

    /**
     * This test makes an assumption that balances can be negative
     * (i.e. the user owes money to the wallet)
     * @throws Exception
     */
    @Test
    public void whenNegativeSpendsAppliedBalanceIsAffected() throws Exception {
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
                                    "\t\"amount\": \"-5.9\",\n" +
                                    "\t\"currency\": \"GBP\"\n" +
                                    "}"))
                    ).post("spend");

                    ReceivedResponse transactionResp = httpClient.requestSpec(rSpec -> rSpec.headers(headers -> headers.set("Authorization", authToken))).get("balance");
                    assertEquals(200, transactionResp.getStatusCode());
                    String s = transactionResp.getBody().getText();
                    assertEquals("{\"balance\":-5.9,\"currency\":\"GBP\"}", s);
                });
    }

    /**
     * This test makes an assumption that balances can be negative
     * (i.e. the user owes money to the wallet)
     * @throws Exception
     */
    @Test
    public void whenMultipleNegativeSpendsAppliedBalanceIsAffected() throws Exception {
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
                                    "\t\"amount\": \"-40\",\n" +
                                    "\t\"currency\": \"GBP\"\n" +
                                    "}"))
                    ).post("spend");
                    httpClient.requestSpec(rSpec -> rSpec
                            .headers(headers ->
                                    headers.set("Authorization", authToken)
                                            .set("Content-Type", "application/json")
                            )
                            .body(body -> body.text("{\n" +
                                    "\t\"date\": \"2016-12-15T10:44:33Z\",\n" +
                                    "\t\"description\": \"Some item\",\n" +
                                    "\t\"amount\": \"-20.0\",\n" +
                                    "\t\"currency\": \"GBP\"\n" +
                                    "}"))
                    ).post("spend");

                    ReceivedResponse transactionResp = httpClient.requestSpec(rSpec -> rSpec.headers(headers -> headers.set("Authorization", authToken))).get("balance");
                    assertEquals(200, transactionResp.getStatusCode());
                    String s = transactionResp.getBody().getText();
                    assertEquals("{\"balance\":-60.0,\"currency\":\"GBP\"}", s);
                });
    }

    @Test
    public void whenNoAuthBalanceHas401() throws Exception {
        EmbeddedApp.of(server -> Application.startServer(server))
                .test(httpClient -> {
                    ReceivedResponse resp = httpClient.get("Balance");
                    assertEquals(401, resp.getStatusCode());
                });
    }

    @Test
    public void whenInvalidAuthBalanceHas401() throws Exception {
        EmbeddedApp.of(server -> Application.startServer(server))
                .test(httpClient -> {
                    ReceivedResponse loginResp = httpClient.post("login");
                    String authToken = loginResp.getHeaders().get("Authorization") + "INVALID";
                    ReceivedResponse balanceResp = httpClient.requestSpec(rSpec -> rSpec.headers(headers -> headers.set("Authorization", authToken))).get("balance");
                    assertEquals(401, balanceResp.getStatusCode());
                });
    }

    @Test
    public void whenValidAuthBalanceHas200() throws Exception {
        EmbeddedApp.of(server -> Application.startServer(server))
                .test(httpClient -> {
                    ReceivedResponse loginResp = httpClient.post("login");
                    String authToken = loginResp.getHeaders().get("Authorization");
                    ReceivedResponse balanceResp = httpClient.requestSpec(rSpec -> rSpec.headers(headers -> headers.set("Authorization", authToken))).get("balance");
                    assertEquals(200, balanceResp.getStatusCode());
                });
    }

    @Test
    public void whenBalanceIsPOST405() throws Exception {
        EmbeddedApp.of(server -> Application.startServer(server))
                .test(httpClient -> {
                    ReceivedResponse loginResp = httpClient.post("login");
                    String authToken = loginResp.getHeaders().get("Authorization");
                    ReceivedResponse balanceResp = httpClient.requestSpec(rSpec -> rSpec.headers(headers -> headers.set("Authorization", authToken))).post("balance");
                    assertEquals(405, balanceResp.getStatusCode());
                });
    }
}