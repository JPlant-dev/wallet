package com.muchbetter.wallet;

import org.junit.jupiter.api.Test;
import ratpack.http.client.ReceivedResponse;
import ratpack.test.embed.EmbeddedApp;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApplicationSpendTest {
    @Test
    public void whenSingleSpendReturnOk() throws Exception {
        EmbeddedApp.of(server -> Application.startServer(server))
                .test(httpClient -> {
                    ReceivedResponse loginResp = httpClient.post("login");

                    String authToken = "Bearer " + loginResp.getHeaders().get("Authorization");

                    ReceivedResponse spendResp = httpClient.requestSpec(rSpec -> rSpec
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

                    assertEquals(200, spendResp.getStatusCode());
                    String s = spendResp.getBody().getText();
                    assertEquals("ok", s);
                });
    }

    @Test
    public void whenMultipleSpendsReturnOk() throws Exception {
        EmbeddedApp.of(server -> Application.startServer(server))
                .test(httpClient -> {
                    ReceivedResponse loginResp = httpClient.post("login");

                    String authToken = "Bearer " + loginResp.getHeaders().get("Authorization");

                    ReceivedResponse spendResp = httpClient.requestSpec(rSpec -> rSpec
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

                    assertEquals(200, spendResp.getStatusCode());
                    String s = spendResp.getBody().getText();
                    assertEquals("ok", s);
                });
    }

    @Test
    public void whenNoAuthSpendHas401() throws Exception {
        EmbeddedApp.of(server -> Application.startServer(server))
                .test(httpClient -> {
                    ReceivedResponse resp = httpClient.post("spend");
                    assertEquals(401, resp.getStatusCode());
                });
    }

    @Test
    public void whenInvalidAuthSpendHas401() throws Exception {
        EmbeddedApp.of(server -> Application.startServer(server))
                .test(httpClient -> {
                    ReceivedResponse loginResp = httpClient.post("login");
                    String authToken = loginResp.getHeaders().get("Authorization") + "INVALID";
                    ReceivedResponse spendResp = httpClient.requestSpec(rSpec -> rSpec.headers(headers -> headers.set("Authorization", authToken))).post("spend");
                    assertEquals(401, spendResp.getStatusCode());
                });
    }

    @Test
    public void whenSpendWithNoJsonHas500() throws Exception {
        EmbeddedApp.of(server -> Application.startServer(server))
                .test(httpClient -> {
                    ReceivedResponse loginResp = httpClient.post("login");
                    String authToken = loginResp.getHeaders().get("Authorization");
                    ReceivedResponse spendResp = httpClient.requestSpec(rSpec -> rSpec.headers(headers -> headers.set("Authorization", authToken))).post("spend");
                    assertEquals(500, spendResp.getStatusCode());
                });
    }

    @Test
    public void whenSpendWithWrongContentTypeHas500() throws Exception {
        EmbeddedApp.of(server -> Application.startServer(server))
                .test(httpClient -> {
                    ReceivedResponse loginResp = httpClient.post("login");

                    String authToken = "Bearer " + loginResp.getHeaders().get("Authorization");

                    ReceivedResponse spendResp = httpClient.requestSpec(rSpec -> rSpec
                            .headers(headers ->
                                    headers.set("Authorization", authToken)
                                            .set("Content-Type", "application/text")
                            )
                            .body(body -> body.text("{\n" +
                                    "\t\"date\": \"2016-12-15T10:44:33Z\",\n" +
                                    "\t\"description\": \"Some item\",\n" +
                                    "\t\"amount\": \"1.45\",\n" +
                                    "\t\"currency\": \"GBP\"\n" +
                                    "}"))
                    ).post("spend");

                    assertEquals(500, spendResp.getStatusCode());
                });
    }

    @Test
    public void whenSpendIsGET405() throws Exception {
        EmbeddedApp.of(server -> Application.startServer(server))
                .test(httpClient -> {
                    ReceivedResponse loginResp = httpClient.post("login");
                    String authToken = loginResp.getHeaders().get("Authorization");
                    ReceivedResponse spendResp = httpClient.requestSpec(rSpec -> rSpec.headers(headers -> headers.set("Authorization", authToken))).get("spend");
                    assertEquals(405, spendResp.getStatusCode());
                });
    }
}
