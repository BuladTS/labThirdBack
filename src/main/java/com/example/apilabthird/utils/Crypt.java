package com.example.apilabthird.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;



@Component
@Slf4j
public class Crypt {
    public String encrypt(String content, String key) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, InvalidAlgorithmParameterException {
        String secretKey = getAesStrKey(key);
        String ivString = getIv(key);

        log.info("Generating secretKey: " + secretKey + " and iv: " + ivString);

        return encryptAES(content.getBytes(), secretKey, ivString);
    }

    /**
     * Шифрование
     *
     * @param content      содержание контента для шифрования
     * @param secretKeyStr Ключ AES, используемый для шифрования, строка в кодировке BASE64
     * @param iv           вектор инициализации, длина 16 байтов, 16 * 8 = 128 бит
     *                     зашифрованный текст @return, возвращаемый после обработки BASE64
     */
    public static String encryptAES(byte[] content, String secretKeyStr, String iv) throws
            NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException
    {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyStr.getBytes(), "AES");

        Cipher cipher = Cipher.getInstance("AES / CBC / PKCS5Padding");

        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

        return Base64.getEncoder().encodeToString(cipher.doFinal(content));
    }

    public String decrypt(String content, String key) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String secretKey = getAesStrKey(key);
        String ivString = getIv(key);

        return decryptAES(content.getBytes(), secretKey, ivString);
    }

    /**
     * Расшифровывать
     *
     * @param content:      контент для дешифрования, который является байтовым массивом после кодирования BASE64
     * @param secretKeyStr: ключ AES, используемый для расшифровки, строка в кодировке BASE64
     * @param iv:           вектор инициализации, длина 16 байтов, 16 * 8 = 128 бит
     * @return расшифрованный открытый текст, вернуть открытый текст после преобразования кодировки UTF-8
     */
    public static String decryptAES(byte[] content, String secretKeyStr, String iv) throws
            NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException
    {

        byte[] contentDecByBase64 = Base64.getDecoder().decode(content);

        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyStr.getBytes(), "AES");

        Cipher cipher = Cipher.getInstance("AES / CBC / PKCS5Padding");

        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

        return new String(cipher.doFinal(contentDecByBase64), StandardCharsets.UTF_8);
    }

    private String getAesStrKey(String key) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(key.getBytes());
        byte[] encryptionKey = md.digest();
        return Base64.getEncoder().encodeToString(encryptionKey).substring(2, 34);
    }

    private String getIv(String key) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(key.getBytes());
        byte[] encryptionIv = md.digest();
        return Base64.getEncoder().encodeToString(encryptionIv).substring(2, 18);
    }
}
