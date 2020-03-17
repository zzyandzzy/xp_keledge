package xyz.zzyitj.keledge.downloader.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/18 2:43 下午
 * @email zzy.main@gmail.com
 */
public class AESUtils {
    /**
     * 解密
     *
     * @param content 密文
     * @param key     加密密码
     * @return String
     * @throws Exception 异常
     */
    public static byte[] decode(byte[] content, String key) throws Exception {
        byte[] raw = key.getBytes("utf-8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        return cipher.doFinal(content);
    }
}
