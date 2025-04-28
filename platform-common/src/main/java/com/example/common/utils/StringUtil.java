package com.example.common.utils;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 字符串工具类，扩展Apache Commons Lang3和Hutool
 *
 * @author platform
 * @since 1.0.0
 */
public class StringUtil extends StringUtils {

    /**
     * 生成UUID（不带横线）
     *
     * @return UUID字符串
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成带横线的UUID
     *
     * @return UUID字符串
     */
    public static String uuidWithHyphen() {
        return UUID.randomUUID().toString();
    }

    /**
     * 驼峰转下划线
     *
     * @param str 驼峰字符串
     * @return 下划线字符串
     */
    public static String camelToUnderscore(final String str) {
        return StrUtil.toUnderlineCase(str);
    }

    /**
     * 下划线转驼峰
     *
     * @param str 下划线字符串
     * @return 驼峰字符串
     */
    public static String underscoreToCamel(final String str) {
        return StrUtil.toCamelCase(str);
    }

    /**
     * 下划线转帕斯卡（首字母大写驼峰）
     *
     * @param str 下划线字符串
     * @return 帕斯卡字符串
     */
    public static String underscoreToPascal(final String str) {
        return StrUtil.upperFirst(StrUtil.toCamelCase(str));
    }

    /**
     * 获取字符串的字节长度
     *
     * @param str 字符串
     * @return 字节长度
     */
    public static int getByteLength(final String str) {
        // 修复默认编码问题，明确指定UTF-8编码
        return str == null ? 0 : str.getBytes(StandardCharsets.UTF_8).length;
    }

    /**
     * 截取字符串（按字节长度）
     *
     * @param str    字符串
     * @param length 字节长度
     * @return 截取后的字符串
     */
    public static String subByteLength(final String str, final int length) {
        if (str == null) {
            return null;
        }
        // 修复默认编码问题，明确指定UTF-8编码
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        if (bytes.length <= length) {
            return str;
        }
        return new String(bytes, 0, length, StandardCharsets.UTF_8);
    }

    /**
     * 获取字符串中的数字部分
     *
     * @param str 字符串
     * @return 数字字符串
     */
    public static String getNumbers(final String str) {
        if (str == null) {
            return "";
        }
        return str.replaceAll("\\D", "");
    }

    /**
     * 获取字符串中的字母部分
     *
     * @param str 字符串
     * @return 字母字符串
     */
    public static String getLetters(final String str) {
        if (str == null) {
            return "";
        }
        return str.replaceAll("[^a-zA-Z]", "");
    }

    /**
     * 是否包含中文
     *
     * @param str 字符串
     * @return 是否包含中文
     */
    public static boolean containsChinese(final String str) {
        return str != null && str.matches(".*[\\u4e00-\\u9fa5].*");
    }
}