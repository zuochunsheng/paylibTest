package com.huican.pay.tools.http.encrypt;



import android.util.Log;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;



/**
 * RSA算法，实现数据的加密解密。
 *
 */
public class RSAUtil {


    private static Cipher cipher;

    /**
     * 安卓端公钥，使用公钥加密密文后传入API，进行密文验证（数据库中有记录）
     */
    public final static String devKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJckczAv2uiJiiDSJ2Rs8T6OVYqDC0pRuewp1ONMwuuM\r\nk/LX1nhycAk2ZIkA9MdJ7xZJTNJO8MSgVFq1u4Lih+kCAwEAAQ==";

    static {
        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        } catch (NoSuchAlgorithmException e) {
            Log.e(e.getClass().toString(),e.getMessage());
        } catch (NoSuchPaddingException e) {
            Log.e(e.getClass().toString(),e.getMessage());
        }
    }

    /**
     * 生成密钥对
     *
     * @param
     * @return
     */
    public static Map<String, String> generateKeyPair() {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            // 密钥位数
            keyPairGen.initialize(512);
            // 密钥对
            KeyPair keyPair = keyPairGen.generateKeyPair();
            // 公钥
            PublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            // 私钥
            PrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            //得到公钥字符串
            String publicKeyString = getKeyString(publicKey);
            //得到私钥字符串
            String privateKeyString = getKeyString(privateKey);
            //将生成的密钥对返回
            Map<String, String> map = new HashMap<String, String>();
            map.put("publicKey", publicKeyString);
            map.put("privateKey", privateKeyString);
            return map;
        } catch (Exception e) {
            Log.e(e.getClass().toString(),e.getMessage());
        }
        return null;
    }

    /**
     * 得到公钥
     *
     * @param key 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PublicKey getPublicKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = (new BASE64Decoder()).decodeBuffer(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 得到私钥
     *
     * @param key 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = (new BASE64Decoder()).decodeBuffer(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * 得到密钥字符串（经过base64编码）
     *
     * @return
     */
    public static String getKeyString(Key key) throws Exception {
        byte[] keyBytes = key.getEncoded();
        String s = (new BASE64Encoder()).encode(keyBytes);
        return s;
    }

    /**
     * 使用公钥对明文进行加密，返回BASE64编码的字符串
     *
     * @param publicKey
     * @param plainText
     * @return
     */
    public static synchronized String encrypt(PublicKey publicKey, String plainText) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] enBytes = cipher.doFinal(plainText.getBytes());
            return (new BASE64Encoder()).encode(enBytes);
        } catch (InvalidKeyException e) {
            Log.e(e.getClass().toString(),e.getMessage());
        } catch (IllegalBlockSizeException e) {
            Log.e(e.getClass().toString(),e.getMessage());
        } catch (BadPaddingException e) {
            Log.e(e.getClass().toString(),e.getMessage());
        }
        return null;
    }

    /**
     * 使用私钥对明文密文进行解密
     *
     * @param privateKey
     * @param enStr
     * @return
     */
    public static String decrypt(PrivateKey privateKey, String enStr) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] deBytes = cipher.doFinal((new BASE64Decoder()).decodeBuffer(enStr));
            return new String(deBytes);
        } catch (InvalidKeyException e) {
            Log.e(e.getClass().toString(),e.getMessage());
        } catch (IllegalBlockSizeException e) {
            Log.e(e.getClass().toString(),e.getMessage());
        } catch (BadPaddingException e) {
            Log.e(e.getClass().toString(),e.getMessage());
        } catch (IOException e) {
            Log.e(e.getClass().toString(),e.getMessage());
        }
        return null;
    }

    /**
     * 生成密钥
     *
     * @return bawoKey 随机6位数字或字母 +当前时间戳
     */
    public static String getBawoKey() {
        StringBuffer buff = new StringBuffer();
        String chars = "0123456789abcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < 6; i++) {
            int rand = (int) (Math.random() * 36);
            buff.append(chars.charAt(rand));
        }
        return buff.toString() + Calendar.getInstance().getTimeInMillis();
    }
}
