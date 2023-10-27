package com.function;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {

    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it
     * using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
    @FunctionName("primeFactors")
    public HttpResponseMessage primeFactors(
            @HttpTrigger(name = "req", methods = { HttpMethod.GET,
                    HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        final String query = request.getQueryParameters().get("number");
        final String numberString = request.getBody().orElse(query);

        if (numberString == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Please pass a number on the query string or in the request body").build();
        } else {
            try {
                final BigInteger number = new BigInteger(numberString);
                List<BigInteger> factors = new ArrayList<BigInteger>();
                if (number.compareTo(BigInteger.ONE) < 0)
                    throw new Exception();
                if (number.compareTo(BigInteger.ONE) == 0)
                    return request.createResponseBuilder(HttpStatus.OK).body("One can't be split into prime factors")
                            .build();
                Thread thread = new Thread(() -> factors.addAll(primeFactors(number)));
                thread.start();
                long endTimeMillis = System.currentTimeMillis() + 20000;
                while (thread.isAlive()) {
                    if (System.currentTimeMillis() > endTimeMillis) {
                        thread.interrupt();
                        return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Time ran out. Number probably was too big").build();
                    }
                }
                return request.createResponseBuilder(HttpStatus.OK).body(primeFactors(number)).build();
            } catch (Exception e) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body("Please pass a whole positive number on the query string or in the request body.")
                        .build();
            }
        }
    }

    @FunctionName("factorial")
    public HttpResponseMessage factorial(
            @HttpTrigger(name = "req", methods = { HttpMethod.GET,
                    HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        final String query = request.getQueryParameters().get("number");
        final String numberString = request.getBody().orElse(query);

        if (numberString == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Please pass a number on the query string or in the request body").build();
        } else {
            try {
                long number = Long.parseLong(numberString);
                if (number < 0)
                    throw new Exception();
                List<BigInteger> res = new ArrayList<BigInteger>();
                Thread thread = new Thread(() -> {
                    res.add(factorial(number));
                });
                thread.start();
                long endTimeMillis = System.currentTimeMillis() + 20000;
                while (thread.isAlive()) {
                    if (System.currentTimeMillis() > endTimeMillis) {
                        thread.interrupt();
                        return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Time ran out. Number probably was too big").build();
                    }
                }
                return request.createResponseBuilder(HttpStatus.OK).body(res.get(0).toString()).build();
            } catch (Exception e) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body("Please pass a positive whole number on the query string or in the request body. Note that the number can't be bigger than "
                                + Long.MAX_VALUE)
                        .build();
            }
        }
    }

    @FunctionName("pi")
    public HttpResponseMessage pi(
            @HttpTrigger(name = "req", methods = { HttpMethod.GET,
                    HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        final String query = request.getQueryParameters().get("digits");
        final String digitString = request.getBody().orElse(query);

        if (digitString == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Please pass a number on the query string or in the request body").build();
        } else {
            try {
                int digits = Integer.parseInt(digitString);
                if (digits < 0)
                    throw new Exception();
                List<BigDecimal> res = new ArrayList<BigDecimal>();
                Thread thread = new Thread(() -> {
                    res.add(chudnovsky(digits));
                });
                thread.start();
                long endTimeMillis = System.currentTimeMillis() + 20000;
                while (thread.isAlive()) {
                    if (System.currentTimeMillis() > endTimeMillis) {
                        thread.interrupt();
                        return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Time ran out. Number probably was too big").build();
                    }
                }
                return request.createResponseBuilder(HttpStatus.OK).body(res.get(0).toString()).build();
            } catch (Exception e) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body("Please pass a positive whole number on the query string or in the request body. Note that the number can't be bigger than "
                                + Integer.MAX_VALUE)
                        .build();
            }
        }
    }

    private BigDecimal chudnovsky(int numSigDigits) {
        int numDigits = numSigDigits + 20;
        MathContext mc = new MathContext(numDigits, RoundingMode.HALF_EVEN),
                mcx = new MathContext(numDigits * 20, RoundingMode.HALF_EVEN);
        BigDecimal ratC = new BigDecimal(426880L, mc),
                irratC = new BigDecimal(10005L, mcx),
                incrL = new BigDecimal(545140134L, mc),
                l = new BigDecimal(13591409L, mcx),
                mulX = new BigDecimal(-262537412640768000L, mc),
                x = new BigDecimal(1L, mcx),
                incrK = new BigDecimal(12L, mc),
                k = new BigDecimal(6L, mc),
                m = new BigDecimal(1L, mc),
                b16 = new BigDecimal(16L, mc), j;

        BigDecimal C = ratC.multiply(irratC.sqrt(mcx), mcx);
        BigDecimal sum = new BigDecimal(0, mcx);

        for (int i = 0; i < numDigits / 13 + 1; i++) {
            j = new BigDecimal(i + 1, mc);

            sum = sum.add(m.multiply(l, mc).divide(x, mc), mc);

            l = l.add(incrL, mcx);
            x = x.multiply(mulX, mcx);
            m = m.multiply(k.pow(3, mc).subtract(b16.multiply(k, mc), mc).divide(j.pow(3, mc), mc), mc);
            k = k.add(incrK, mc);
        }

        return C.divide(sum, mcx).setScale(numSigDigits, RoundingMode.HALF_EVEN);
    }

    private BigInteger factorial(long number) {
        BigInteger result = BigInteger.ONE;
        for (long factor = 2; factor <= number; factor++) {
            result = result.multiply(BigInteger.valueOf(factor));
        }
        return result;
    }

    private List<BigInteger> primeFactors(BigInteger number) {
        BigInteger n = number;
        List<BigInteger> factors = new ArrayList<BigInteger>();
        while (n.mod(BigInteger.TWO).compareTo(BigInteger.ZERO) == 0) {
            factors.add(BigInteger.TWO);
            n = n.divide(BigInteger.TWO);
        }
        for (BigInteger i = BigInteger.valueOf(3); i.sqrt().compareTo(n) <= 0; i = i.add(BigInteger.TWO)) {
            while (n.mod(i).compareTo(BigInteger.ZERO) == 0) {
                factors.add(i);
                n = n.divide(i);
            }
        }
        if (n.compareTo(BigInteger.TWO) > 0) {
            factors.add(n);
        }
        return factors;
    }

}
