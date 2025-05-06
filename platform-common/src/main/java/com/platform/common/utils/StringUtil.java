package com.platform.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 字符串工具类，扩展自Apache Commons Lang3的StringUtils
 */
public class StringUtil extends StringUtils {

    /**
     * 私有构造函数，防止实例化
     */
    private StringUtil() {
        throw new AssertionError("No StringUtil instances for you!");
    }
    
    /**
     * 随机对象
     */
    private static final Random RANDOM = new Random();
    
    /**
     * 数字字符集
     */
    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    
    /**
     * 字母字符集
     */
    private static final char[] LETTERS = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };
    
    /**
     * 字母数字字符集
     */
    private static final char[] LETTERS_AND_DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };
    
    /**
     * 检查字符串是否为null或空
     *
     * @param str 待检查字符串
     * @return 如果为null或空则返回true，否则返回false
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
    
    /**
     * 检查字符串是否为非null且非空
     *
     * @param str 待检查字符串
     * @return 如果非null且非空则返回true，否则返回false
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    /**
     * 检查字符串是否为null、空或仅包含空白字符
     *
     * @param str 待检查字符串
     * @return 如果为null、空或仅包含空白字符则返回true，否则返回false
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * 检查字符串是否为非null且包含非空白字符
     *
     * @param str 待检查字符串
     * @return 如果非null且包含非空白字符则返回true，否则返回false
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
    
    /**
     * 检查字符串数组中是否存在null或空字符串
     *
     * @param strs 待检查字符串数组
     * @return 如果存在null或空字符串则返回true，否则返回false
     */
    public static boolean hasEmpty(String... strs) {
        if (strs == null || strs.length == 0) {
            return true;
        }
        
        for (String str : strs) {
            if (isEmpty(str)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 检查字符串数组中是否存在null、空或仅包含空白字符的字符串
     *
     * @param strs 待检查字符串数组
     * @return 如果存在null、空或仅包含空白字符的字符串则返回true，否则返回false
     */
    public static boolean hasBlank(String... strs) {
        if (strs == null || strs.length == 0) {
            return true;
        }
        
        for (String str : strs) {
            if (isBlank(str)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 检查字符串数组中是否所有字符串都非null且非空
     *
     * @param strs 待检查字符串数组
     * @return 如果所有字符串都非null且非空则返回true，否则返回false
     */
    public static boolean areNotEmpty(String... strs) {
        return !hasEmpty(strs);
    }
    
    /**
     * 检查字符串数组中是否所有字符串都非null且包含非空白字符
     *
     * @param strs 待检查字符串数组
     * @return 如果所有字符串都非null且包含非空白字符则返回true，否则返回false
     */
    public static boolean areNotBlank(String... strs) {
        return !hasBlank(strs);
    }
    
    /**
     * 字符串驼峰转下划线
     *
     * @param camelCaseStr 驼峰格式字符串
     * @return 下划线格式字符串
     */
    public static String camelToUnderscore(String camelCaseStr) {
        if (isEmpty(camelCaseStr)) {
            return camelCaseStr;
        }
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < camelCaseStr.length(); i++) {
            char c = camelCaseStr.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    result.append('_');
                }
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }
    
    /**
     * 字符串下划线转驼峰（首字母小写）
     *
     * @param underscoreStr 下划线格式字符串
     * @return 驼峰格式字符串（首字母小写）
     */
    public static String underscoreToCamel(String underscoreStr) {
        return underscoreToCamel(underscoreStr, false);
    }
    
    /**
     * 字符串下划线转驼峰
     *
     * @param underscoreStr 下划线格式字符串
     * @param capitalizeFirstLetter 首字母是否大写
     * @return 驼峰格式字符串
     */
    public static String underscoreToCamel(String underscoreStr, boolean capitalizeFirstLetter) {
        if (isEmpty(underscoreStr)) {
            return underscoreStr;
        }
        
        StringBuilder result = new StringBuilder();
        boolean nextUpperCase = false;
        
        for (int i = 0; i < underscoreStr.length(); i++) {
            char c = underscoreStr.charAt(i);
            if (c == '_') {
                nextUpperCase = true;
            } else {
                if (nextUpperCase) {
                    result.append(Character.toUpperCase(c));
                    nextUpperCase = false;
                } else {
                    result.append(Character.toLowerCase(c));
                }
            }
        }
        
        if (capitalizeFirstLetter && result.length() > 0) {
            result.setCharAt(0, Character.toUpperCase(result.charAt(0)));
        }
        
        return result.toString();
    }
    
    /**
     * 生成UUID（无连字符）
     *
     * @return UUID字符串（32位）
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * 生成带连字符的UUID
     *
     * @return UUID字符串（36位）
     */
    public static String uuidWithHyphen() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * 生成指定长度的随机数字字符串
     *
     * @param length 长度
     * @return 随机数字字符串
     */
    public static String randomDigits(int length) {
        return randomString(length, DIGITS);
    }
    
    /**
     * 生成指定长度的随机字母字符串
     *
     * @param length 长度
     * @return 随机字母字符串
     */
    public static String randomLetters(int length) {
        return randomString(length, LETTERS);
    }
    
    /**
     * 生成指定长度的随机字母数字字符串
     *
     * @param length 长度
     * @return 随机字母数字字符串
     */
    public static String randomAlphanumeric(int length) {
        return randomString(length, LETTERS_AND_DIGITS);
    }
    
    /**
     * 生成指定长度的随机字符串
     *
     * @param length 长度
     * @param characters 字符集
     * @return 随机字符串
     */
    public static String randomString(int length, char[] characters) {
        if (length <= 0) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters[RANDOM.nextInt(characters.length)]);
        }
        
        return sb.toString();
    }
    
    /**
     * 检查字符串是否为有效的电子邮件地址
     *
     * @param email 待检查字符串
     * @return 如果是有效的电子邮件地址则返回true，否则返回false
     */
    public static boolean isEmail(String email) {
        if (isBlank(email)) {
            return false;
        }
        
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        
        return matcher.matches();
    }
    
    /**
     * 检查字符串是否为有效的手机号码（中国大陆）
     *
     * @param mobile 待检查字符串
     * @return 如果是有效的手机号码则返回true，否则返回false
     */
    public static boolean isMobile(String mobile) {
        if (isBlank(mobile)) {
            return false;
        }
        
        String regex = "^1[3-9]\\d{9}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(mobile);
        
        return matcher.matches();
    }
    
    /**
     * 检查字符串是否为有效的身份证号码（中国大陆）
     *
     * @param idCard 待检查字符串
     * @return 如果是有效的身份证号码则返回true，否则返回false
     */
    public static boolean isIdCard(String idCard) {
        if (isBlank(idCard)) {
            return false;
        }
        
        String regex = "(^\\d{15}$)|(^\\d{17}([0-9]|X)$)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(idCard);
        
        return matcher.matches();
    }
    
    /**
     * 将对象转换为字符串，如果对象为null则返回指定的默认值
     *
     * @param obj 对象
     * @param defaultValue 默认值
     * @return 字符串
     */
    public static String toString(Object obj, String defaultValue) {
        return obj == null ? defaultValue : obj.toString();
    }
    
    /**
     * 将对象转换为字符串，如果对象为null则返回空字符串
     *
     * @param obj 对象
     * @return 字符串
     */
    public static String toString(Object obj) {
        return toString(obj, "");
    }
    
    /**
     * 将集合转换为以指定分隔符分隔的字符串
     *
     * @param collection 集合
     * @param separator 分隔符
     * @return 字符串
     */
    public static String join(Collection<?> collection, String separator) {
        if (collection == null || collection.isEmpty()) {
            return "";
        }
        
        return collection.stream()
                .map(Object::toString)
                .collect(Collectors.joining(separator));
    }
    
    /**
     * 将数组转换为以指定分隔符分隔的字符串
     *
     * @param array 数组
     * @param separator 分隔符
     * @return 字符串
     */
    public static String join(Object[] array, String separator) {
        if (array == null || array.length == 0) {
            return "";
        }
        
        return Arrays.stream(array)
                .map(Object::toString)
                .collect(Collectors.joining(separator));
    }
    
    /**
     * 将字符串按照指定分隔符分割为列表
     *
     * @param str 字符串
     * @param separator 分隔符
     * @return 列表
     */
    public static List<String> split(String str, String separator) {
        if (isEmpty(str)) {
            return List.of();
        }
        
        return Arrays.asList(str.split(separator));
    }
    
    /**
     * 将字符串按照指定分隔符分割为列表，并移除空元素
     *
     * @param str 字符串
     * @param separator 分隔符
     * @return 列表
     */
    public static List<String> splitAndTrim(String str, String separator) {
        if (isEmpty(str)) {
            return List.of();
        }
        
        return Arrays.stream(str.split(separator))
                .map(String::trim)
                .filter(StringUtil::isNotEmpty)
                .collect(Collectors.toList());
    }
    
    /**
     * 替换字符串中的占位符（格式：${key}）
     *
     * @param template 模板字符串
     * @param params 参数Map
     * @return 替换后的字符串
     */
    public static String replacePlaceholders(String template, Map<String, Object> params) {
        if (isEmpty(template) || params == null || params.isEmpty()) {
            return template;
        }
        
        StringBuilder sb = new StringBuilder(template);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = toString(entry.getValue());
            
            int startIndex = 0;
            while (true) {
                String placeholder = "${" + key + "}";
                int index = sb.indexOf(placeholder, startIndex);
                if (index == -1) {
                    break;
                }
                
                sb.replace(index, index + placeholder.length(), value);
                startIndex = index + value.length();
            }
        }
        
        return sb.toString();
    }
}
