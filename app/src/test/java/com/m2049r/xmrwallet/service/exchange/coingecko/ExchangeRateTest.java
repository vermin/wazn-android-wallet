/*
 * Copyright (c) 2017 m2049r et al.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

<<<<<<< HEAD:app/src/test/java/com/m2049r/xmrwallet/service/exchange/coingecko/ExchangeRateTest.java
package com.m2049r.xmrwallet.service.exchange.coingecko;
=======
package com.m2049r.xmrwallet.service.exchange.kraken;
>>>>>>> e076c19e3e6c4158c3f3e7b1a954b34c58c25dfa:app/src/test/java/com/m2049r/xmrwallet/service/exchange/kraken/ExchangeRateTest.java

import com.m2049r.xmrwallet.model.Wallet;
import com.m2049r.xmrwallet.service.exchange.api.ExchangeApi;
import com.m2049r.xmrwallet.service.exchange.api.ExchangeCallback;
import com.m2049r.xmrwallet.service.exchange.api.ExchangeException;
import com.m2049r.xmrwallet.service.exchange.api.ExchangeRate;

import net.jodah.concurrentunit.Waiter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.TimeoutException;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static org.junit.Assert.assertEquals;


public class ExchangeRateTest {

    private MockWebServer mockWebServer;

    private ExchangeApi exchangeApi;

    private final OkHttpClient okHttpClient = new OkHttpClient();
    private Waiter waiter;

    @Mock
    ExchangeCallback mockExchangeCallback;

    @Before
    public void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        waiter = new Waiter();

        MockitoAnnotations.initMocks(this);

        exchangeApi = new ExchangeApiImpl(okHttpClient, mockWebServer.url("/"));
    }

    @After
    public void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    public void queryExchangeRate_shouldBeGetMethod()
            throws InterruptedException, TimeoutException {

<<<<<<< HEAD:app/src/test/java/com/m2049r/xmrwallet/service/exchange/coingecko/ExchangeRateTest.java
        exchangeApi.queryExchangeRate(Wallet.LOKI_SYMBOL, "USD", mockExchangeCallback);
=======
        exchangeApi.queryExchangeRate("XMR", "USD", mockExchangeCallback);
>>>>>>> e076c19e3e6c4158c3f3e7b1a954b34c58c25dfa:app/src/test/java/com/m2049r/xmrwallet/service/exchange/kraken/ExchangeRateTest.java

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("GET", request.getMethod());
    }

    @Test
    public void queryExchangeRate_shouldHavePairInUrl()
            throws InterruptedException, TimeoutException {

<<<<<<< HEAD:app/src/test/java/com/m2049r/xmrwallet/service/exchange/coingecko/ExchangeRateTest.java
        exchangeApi.queryExchangeRate(Wallet.LOKI_SYMBOL, "USD", mockExchangeCallback);

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/price?ids=loki-network&vs_currencies=usd", request.getPath());
=======
        exchangeApi.queryExchangeRate("XMR", "USD", mockExchangeCallback);

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/?pair=XMRUSD", request.getPath());
>>>>>>> e076c19e3e6c4158c3f3e7b1a954b34c58c25dfa:app/src/test/java/com/m2049r/xmrwallet/service/exchange/kraken/ExchangeRateTest.java
    }

    @Test
    public void queryExchangeRate_wasSuccessfulShouldRespondWithRate()
<<<<<<< HEAD:app/src/test/java/com/m2049r/xmrwallet/service/exchange/coingecko/ExchangeRateTest.java
            throws TimeoutException {
        final String base = Wallet.LOKI_SYMBOL;
        final String quote = "EUR";
        final double rate = 1.56;
        MockResponse jsonMockResponse = new MockResponse().setBody(
                createMockExchangeRateResponse("loki-network", quote, rate));
        mockWebServer.enqueue(jsonMockResponse);

        exchangeApi.queryExchangeRate(base, quote, new ExchangeCallback() {
            @Override
            public void onSuccess(final ExchangeRate exchangeRate) {
                waiter.assertEquals(exchangeRate.getBaseCurrency(), base);
                waiter.assertEquals(exchangeRate.getQuoteCurrency(), quote);
                waiter.assertEquals(exchangeRate.getRate(), rate);
                waiter.resume();
            }

            @Override
            public void onError(final Exception e) {
                waiter.fail(e);
                waiter.resume();
            }
        });
        waiter.await();
    }

    @Test
    public void queryExchangeRate_wasSuccessfulShouldRespondWithRateUSD()
            throws TimeoutException {
        final String base = Wallet.LOKI_SYMBOL;
=======
            throws InterruptedException, JSONException, TimeoutException {
        final String base = "XMR";
>>>>>>> e076c19e3e6c4158c3f3e7b1a954b34c58c25dfa:app/src/test/java/com/m2049r/xmrwallet/service/exchange/kraken/ExchangeRateTest.java
        final String quote = "USD";
        final double rate = 100;
        MockResponse jsonMockResponse = new MockResponse().setBody(
                createMockExchangeRateResponse("loki-network", quote, rate));
        mockWebServer.enqueue(jsonMockResponse);

        exchangeApi.queryExchangeRate(base, quote, new ExchangeCallback() {
            @Override
            public void onSuccess(final ExchangeRate exchangeRate) {
                waiter.assertEquals(exchangeRate.getBaseCurrency(), base);
                waiter.assertEquals(exchangeRate.getQuoteCurrency(), quote);
                waiter.assertEquals(exchangeRate.getRate(), rate);
                waiter.resume();
            }

            @Override
            public void onError(final Exception e) {
                waiter.fail(e);
                waiter.resume();
            }
        });
        waiter.await();
    }

    @Test
    public void queryExchangeRate_wasNotSuccessfulShouldCallOnError()
            throws InterruptedException, JSONException, TimeoutException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
<<<<<<< HEAD:app/src/test/java/com/m2049r/xmrwallet/service/exchange/coingecko/ExchangeRateTest.java
        exchangeApi.queryExchangeRate(Wallet.LOKI_SYMBOL, "USD", new ExchangeCallback() {
=======
        exchangeApi.queryExchangeRate("XMR", "USD", new ExchangeCallback() {
>>>>>>> e076c19e3e6c4158c3f3e7b1a954b34c58c25dfa:app/src/test/java/com/m2049r/xmrwallet/service/exchange/kraken/ExchangeRateTest.java
            @Override
            public void onSuccess(final ExchangeRate exchangeRate) {
                waiter.fail();
                waiter.resume();
            }

            @Override
            public void onError(final Exception e) {
                waiter.assertTrue(e instanceof ExchangeException);
                waiter.assertTrue(((ExchangeException) e).getCode() == 500);
                waiter.resume();
            }

        });
        waiter.await();
    }

    @Test
    public void queryExchangeRate_unknownAssetShouldCallOnError()
<<<<<<< HEAD:app/src/test/java/com/m2049r/xmrwallet/service/exchange/coingecko/ExchangeRateTest.java
            throws TimeoutException {
        MockResponse jsonMockResponse = new MockResponse().setBody(
                createMockExchangeRateErrorResponse());
        mockWebServer.enqueue(jsonMockResponse);

        exchangeApi.queryExchangeRate(Wallet.LOKI_SYMBOL, "ABC", new ExchangeCallback() {
=======
            throws InterruptedException, JSONException, TimeoutException {
        mockWebServer.enqueue(new MockResponse().
                setResponseCode(200).
                setBody("{\"error\":[\"EQuery:Unknown asset pair\"]}"));
        exchangeApi.queryExchangeRate("XMR", "ABC", new ExchangeCallback() {
>>>>>>> e076c19e3e6c4158c3f3e7b1a954b34c58c25dfa:app/src/test/java/com/m2049r/xmrwallet/service/exchange/kraken/ExchangeRateTest.java
            @Override
            public void onSuccess(final ExchangeRate exchangeRate) {
                waiter.fail();
                waiter.resume();
            }

            @Override
            public void onError(final Exception e) {
                waiter.assertTrue(e instanceof ExchangeException);
                ExchangeException ex = (ExchangeException) e;
                waiter.assertTrue(ex.getCode() == 200);
<<<<<<< HEAD:app/src/test/java/com/m2049r/xmrwallet/service/exchange/coingecko/ExchangeRateTest.java
                waiter.assertEquals(ex.getErrorMsg(), "No price found");
=======
                waiter.assertEquals(ex.getErrorMsg(), "EQuery:Unknown asset pair");
>>>>>>> e076c19e3e6c4158c3f3e7b1a954b34c58c25dfa:app/src/test/java/com/m2049r/xmrwallet/service/exchange/kraken/ExchangeRateTest.java
                waiter.resume();
            }

        });
        waiter.await();
    }

<<<<<<< HEAD:app/src/test/java/com/m2049r/xmrwallet/service/exchange/coingecko/ExchangeRateTest.java
    private String createMockExchangeRateResponse(final String base, final String quote, final double rate) {
        /*
        {
          "loki-network": {
            "usd": 0.166536
          }
        }
        */
        return "{" +
                    "\"" + base + "\": {" +
                        "\"" + quote.toLowerCase() + "\":" + rate +
                    "}" +
                "}";
    }

    private String createMockExchangeRateErrorResponse() {
        return "{}";
=======
    static public String createMockExchangeRateResponse(final String base, final String quote, final double rate) {
        return "{\n" +
                "   \"error\":[],\n" +
                "   \"result\":{\n" +
                "       \"X" + base + "Z" + quote + "\":{\n" +
                "           \"a\":[\"" + rate + "\",\"322\",\"322.000\"],\n" +
                "           \"b\":[\"" + rate + "\",\"76\",\"76.000\"],\n" +
                "           \"c\":[\"" + rate + "\",\"2.90000000\"],\n" +
                "           \"v\":[\"4559.03962053\",\"5231.33235586\"],\n" +
                "           \"p\":[\"" + rate + "\",\"" + rate + "\"],\n" +
                "           \"t\":[801,1014],\n" +
                "           \"l\":[\"" + (rate * 0.8) + "\",\"" + rate + "\"],\n" +
                "           \"h\":[\"" + (rate * 1.2) + "\",\"" + rate + "\"],\n" +
                "           \"o\":\"" + rate + "\"\n" +
                "       }\n" +
                "   }\n" +
                "}";
>>>>>>> e076c19e3e6c4158c3f3e7b1a954b34c58c25dfa:app/src/test/java/com/m2049r/xmrwallet/service/exchange/kraken/ExchangeRateTest.java
    }
}
