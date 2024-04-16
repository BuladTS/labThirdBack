package com.example.apilabthird.service;

import com.example.apilabthird.DTO.demo.TestData;
import com.example.apilabthird.DTO.demo.TestDataString;
import com.example.apilabthird.DTO.keys.FirstPathOfKeyResponse;
import com.example.apilabthird.DTO.keys.SecondPartOfKeyResponse;
import com.example.apilabthird.DTO.keys.SecondPathOfKeyRequest;
import com.example.apilabthird.model.Key;
import com.example.apilabthird.utils.Crypt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

@Service
@Slf4j
public class KeyService {

    private final Crypt crypt;

    private final RedisTemplate<String, Key> redisTemplate;
    private final HashOperations<String, String, Key> hashOperations;

    private final String TABLE_KEY = "TOKEN_KEY";

    public KeyService(Crypt crypt, RedisTemplate<String, Key> redisTemplate) {
        this.crypt = crypt;
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    public Key addKey(String token, Key key) {
        hashOperations.put(TABLE_KEY, token, key);
        return key;
    }

    public Key getKey(String token) {
        return hashOperations.get(TABLE_KEY, token);
    }

    public void delKey(String token) {
        hashOperations.delete(TABLE_KEY, token);
    }

    public SecondPartOfKeyResponse getSecondPartOfKey(String id, SecondPathOfKeyRequest request) {
        BigInteger privateB = findPrimeWithLength(8);
        BigInteger publicB = request.getG().modPow(privateB, request.getP());
        log.info(request.toString());
        BigInteger s = request.getA().modPow(privateB, request.getP());

        Key key = Key.builder()
                .g(request.getG())
                .p(request.getP())
                .privateB(privateB)
                .publicB(publicB)
                .s(s)
                .build();
        hashOperations.put(TABLE_KEY, id, key);

        log.info("Key: " + key.toString());

        return SecondPartOfKeyResponse.builder()
                .g(request.getG())
                .p(request.getP())
                .B(publicB)
                .build();
    }

    public FirstPathOfKeyResponse getFirstPathOfKey() {
        BigInteger p = findPrimeWithLength(12);
        BigInteger g = findPrimeWithLength(7);
//        BigInteger privateB = findPrimeWithLength(8);
//        BigInteger publicB = g.modPow(privateB, p);
//
//        Key key = Key.builder()
//                .p(p)
//                .g(g)
//                .privateB(privateB)
//                .publicB(publicB)
//                .build();
//
//        hashOperations.put(TABLE_KEY, token, key);

        return new FirstPathOfKeyResponse(p, g);
    }

    public TestDataString testCode(String id) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, InvalidKeySpecException {
        Key key = hashOperations.get(TABLE_KEY, id);
        TestData data = new TestData(132123, "testCode");
        assert key != null;
        String name = crypt.encrypt(data.getName(), key.getS().toString());
        String number = crypt.encrypt(String.valueOf(data.getNumber()), key.getS().toString());

        return TestDataString.builder()
                .name(name)
                .number(number)
                .build();
    }

    public String testDecode(TestDataString dataString, String id) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Key key = hashOperations.get(TABLE_KEY, id);
        assert key != null;
        String name=  crypt.decrypt(dataString.getName(), key.getS().toString());
        String number = crypt.decrypt(dataString.getNumber(), key.getS().toString());
        log.debug("Name: " + name + " Number: " + number);
        return name;
    }


    public BigInteger findPrimeWithLength(int length) {
        while (true) {
            BigInteger prime = genRandomBigIntegerWithLength(length);
            if (testMillerRabin(prime, 10) && testFarm(prime, 10))
                return prime;
        }
    }

    public BigInteger genRandomBigIntegerWithLength(int length) {
        SecureRandom secureRandom = new SecureRandom();
        BigInteger lowerBound = BigInteger.TEN.pow(length - 1);
        BigInteger upperBound = BigInteger.TEN.pow(length);
        BigInteger bigInteger;
        do {
            bigInteger = lowerBound.add(new BigInteger(upperBound.subtract(lowerBound).bitLength(), secureRandom));
        } while (bigInteger.compareTo(upperBound) >= 0 || bigInteger.compareTo(lowerBound) < 0);
        return bigInteger;
    }

    public  int getRandomInt(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    /**
     * Генерация числа в допазоне
     * @param lowerBound включается
     * @param upperBound не включается
     * @return числа в допазоне [lowerBound, upperBound)
     */
    public  BigInteger genRandomBigIntegerBetween(BigInteger lowerBound, BigInteger upperBound) {
        Random secureRandom = new SecureRandom();
        int bites = getRandomInt(lowerBound.bitLength(), upperBound.bitLength() + 1);
        BigInteger randomNumber;
        do {

            randomNumber = new BigInteger(bites, secureRandom);
        } while (!(randomNumber.compareTo(lowerBound) >= 0 && randomNumber.compareTo(upperBound) <= 0));
        return randomNumber;
    }

    public boolean testFarm(BigInteger num, int iterations) {
        for (int i = 0; i < iterations; i++) {
            BigInteger random = genRandomBigIntegerBetween(new BigInteger("2"), num);
            if (random.modPow(num.subtract(new BigInteger("1")), num).compareTo(new BigInteger("1")) != 0)
                return false;
        }
        return true;
    }

    public boolean testMillerRabin(BigInteger num, int iterations) {
        int s = 0;
        BigInteger decrNum = num.subtract(new BigInteger("1"));
        BigInteger d = decrNum;
        while (d.mod(new BigInteger("2")).compareTo(new BigInteger("0")) == 0) {
            s++;
            d = d.divide(new BigInteger("2"));
        }

        for (int i = 0; i < iterations; i++) {
            BigInteger randomBigInteger = genRandomBigIntegerBetween(new BigInteger("2"), num.subtract(new BigInteger("1")));
            boolean find = false;
            for (int j = 0; j < s; j++) {
                BigInteger power = new BigInteger("2").pow(j).multiply(d);

                BigInteger x = randomBigInteger.modPow(power, num);
                if (decrNum.compareTo(x) == 0) {
                    find = true;
                    break;
                }
            }
            if (!find) {
                return false;
            }
        }
        return true;
    }
}
