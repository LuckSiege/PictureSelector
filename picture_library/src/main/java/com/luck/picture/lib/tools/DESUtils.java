package com.luck.picture.lib.tools;

import android.text.TextUtils;

import java.net.URLEncoder;
import java.security.Key;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * @author：luck
 * @date：2020-03-26 18:14
 * @describe：DESUtils
 */
public class DESUtils {
    private static final String ALGORITHM_DES = "DES/CBC/PKCS5Padding";
    /**
     * des 加密 key
     */
    public final static String DES_KEY_STRING = "lmw#2020";

    /**
     * DES算法，加密
     *
     * @param data 待加密字符串
     * @param key  加密私钥，长度不能够小于8位
     * @return 加密后的字节数组，一般结合Base64编码使用
     * @throws Exception
     */
    public static String encode(String key, String data, int width, int height) {
        if (data == null) {
            return null;
        }
        String newData = data + "_" + width + "x" + height;
        try {
            DESKeySpec dks = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            // key的长度不能够小于8位字节
            Key secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            IvParameterSpec iv = new IvParameterSpec(DES_KEY_STRING.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] bytes = cipher.doFinal(newData.getBytes());
            String encode = URLEncoder.encode(byte2String(bytes), "UTF-8");
            return TextUtils.isEmpty(encode) ? null : encode.length() > 30 ? encode.substring(encode.length() - 30) : encode;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 二行制转字符串
     *
     * @param b
     * @return
     */
    private static String byte2String(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1) {
                hs.append('0');
            }
            hs.append(stmp);
        }
        return hs.toString().toUpperCase(Locale.CHINA);
    }
}
