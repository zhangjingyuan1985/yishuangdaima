package com.sutpc.bigdata.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;

/**
 * 字符编码转换类
 * Created by xiaoyuzhou on 2019/7/16.
 */
public class CharsetUtils {
    private enum Charset {
        /**
         * 7位ASCII字符，也叫作ISO646-US、Unicode字符集的基本拉丁块
         */
        US_ASCII("US-ASCII", "位ASCII字符，也叫作ISO646-US、Unicode字符集的基本拉丁块 "),
        ISO_8859_1("ISO-8859-1", "ISO 拉丁字母表 No.1，也叫作 ISO-LATIN-1"),
        GBK("GBK", "中文超大字符集"),
        UTF_8("UTF-8", "8 位 UCS 转换格式"),
        UTF_16BE("UTF-16BE", "16 位 UCS 转换格式，Big Endian（最低地址存放高位字节）字节顺序"),
        UTF_16LE("UTF_16LE", "16 位 UCS 转换格式，Big Endian（最低地址存放高位字节）字节顺序"),
        UTF_16("UTF_16", "16 位 UCS 转换格式，字节顺序由可选的字节顺序标记来标识");
        private String encode;
        private String desc;

        public String getEncode() {
            return encode;
        }

        public void setEncode(String encode) {
            this.encode = encode;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        private Charset(String encode, String desc) {
            this.encode = encode;
            this.desc = desc;

        }


    }



    /**
     * 获取传入字符串的编码格式
     *
     * @param str
     * @return
     */
    public static String getEncode(String str) throws UnsupportedEncodingException {
        if (!StringUtils.isEmpty(str)) {
            for (Charset charset : Charset.values()) {
                String tryStr = new String(str.getBytes(charset.getEncode()), charset.getEncode());
                if (str.equals(tryStr))
                    return charset.getEncode();
            }
        }
        throw new UnsupportedEncodingException("编码库中不存在");
    }

    /**
     * 字符串编码转换的实现方法
     *
     * @param str        待转换编码的字符串
     * @param newCharset 目标编码
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String changeCharset(String str, String newCharset)
            throws UnsupportedEncodingException {
        if (str != null) {
            //获取到原字符编码
            String charsetName = getEncode(str);
            //用默认字符编码解码字符串。
            byte[] bs = str.getBytes(charsetName);
            //用新的字符编码生成字符串
            return new String(bs, newCharset);
        }
        return null;
    }

    public static String changeCharset(String str, String oldCharset, String newCharset)
            throws UnsupportedEncodingException {
        if (str != null) {
            //获取到原字符编码
            String charsetName = oldCharset;
            //用默认字符编码解码字符串。
            byte[] bs = str.getBytes(charsetName);
            //用新的字符编码生成字符串
            return new String(bs, newCharset);
        }
        return null;
    }


    /**
     * 将字符编码转换成US-ASCII码
     */
    public static String toASCII(String str) throws UnsupportedEncodingException {
        return changeCharset(str, Charset.US_ASCII.getEncode());
    }

    /**
     * 将字符编码转换成ISO-8859-1码
     */
    public static String toISO_8859_1(String str) throws UnsupportedEncodingException {
        return changeCharset(str, Charset.ISO_8859_1.getEncode());
    }

    /**
     * 将字符编码转换成UTF-8码
     */
    public static String toUTF_8(String str) throws UnsupportedEncodingException {
        return changeCharset(str, Charset.UTF_8.getEncode());
    }

    /**
     * 将字符编码转换成UTF-16BE码
     */
    public static String toUTF_16BE(String str) throws UnsupportedEncodingException {
        return changeCharset(str, Charset.UTF_16BE.getEncode());
    }

    /**
     * 将字符编码转换成UTF-16LE码
     */
    public static String toUTF_16LE(String str) throws UnsupportedEncodingException {
        return changeCharset(str, Charset.UTF_16LE.getEncode());
    }

    /**
     * 将字符编码转换成UTF-16码
     */
    public static String toUTF_16(String str) throws UnsupportedEncodingException {
        return changeCharset(str, Charset.UTF_16.getEncode());
    }

    /**
     * 将字符编码转换成GBK码
     */
    public static String toGBK(String str) throws UnsupportedEncodingException {
        return changeCharset(str, Charset.GBK.getEncode());
    }

}