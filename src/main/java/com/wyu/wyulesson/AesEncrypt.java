package com.wyu.wyulesson;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;


/**
 * AesEncrypt
 *
 * @author wenxian.wen
 * @version 1.0
 * @description
 * @date 2020/11/18 16:56
 */
public final class AesEncrypt {

    private AesEncrypt() {
        //构造器
    }

    /**
     * 对明文加密
     *
     * @param content 明文
     * @param key     密钥
     * @return
     */
    public static String encrypt(String content, String key) {
        //加密为16进制表示
        return getAes(key).encryptHex(content);
    }

    /**
     * 对密文解密
     *
     * @param encryptContent 密文
     * @param key            密钥
     * @return
     */
    public static String decrypt(String encryptContent, String key) {
        // 解密为字符串
        return getAes(key).decryptStr(encryptContent, CharsetUtil.CHARSET_UTF_8);
    }

    private static AES getAes(String key) {
        if (key != null && key.length() != 16) {
            throw new RuntimeException("密钥长度需为16位");
        }
        //构建
        return new AES(Mode.ECB, Padding.PKCS5Padding, key.getBytes());
    }

    public static String getEncryptPassword(String password, String captcha) {
        String key = captcha + captcha + captcha + captcha;
        return encrypt(password, key);
    }

    public static void main(String[] args) {
        String ds = encrypt("zb001222!", "nmafnmafnmafnmaf");
        System.out.println(ds);
        System.out.println(decrypt(ds, "nmafnmafnmafnmaf"));
    }
}