package com.xhd.base.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则
 */
public class RegexUtils {

    // 常量
    public class RegexConstants {

        /**
         * 每个项目注册密码规则不一
         */
        public static final String REGEX_PWD = "^[a-zA-Z0-9]{6,16}$";
        public static final String REGEX_VERIFY_CODE = "^[0-9]{6}";
        /**
         * 正则：手机号
         */
        public static final String REGEX_PHONE11  = "1[3|4|5|7|8|]\\d{9}";
        public static final String REGEX_MOBILE_EXACT  = "^1(3[0-9]|4[579]|5[0-35-9]|7[0-9]|8[0-9])\\d{8}$";
        /**
         * 正则：电话号码
         */
        public static final String REGEX_TEL           = "^0\\d{2,3}[- ]?\\d{7,8}";
        /**
         * 正则：身份证号码 15 位
         */
        public static final String REGEX_ID_CARD15     = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
        /**
         * 正则：身份证号码 18 位
         */
        public static final String REGEX_ID_CARD18     = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9Xx])$";
        /**
         * 正则：数字 空格
         */
        public static final String REGEX_SHU_BLANK     = "^[\\s\\d]*$";
        /**
         * 正则：小数
         */
        public static final String REGEX_DOUBLE_2     = "^d*(?:.d{0,2})?$";
        /**
         * 正则：正整数/小数
         */
        public static final String REGEX_XIAOSHU       = "^(([1-9]+)|([0-9]+\\.[0-9]{1,2}))$";
        /**
         * 正则：邮箱
         */
        public static final String REGEX_EMAIL         = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        /**
         * 正则：URL
         */
        public static final String REGEX_URL           = "[a-zA-z]+://[^\\s]*";
        /**
         * 正则：汉字
         */
        public static final String REGEX_ZH            = "^[\\u4e00-\\u9fa5]+$";
        /**
         * 正则：用户名，取值范围为 a-z,A-Z,0-9,"_", 汉字，不能以 "_" 结尾,用户名必须是 6-20 位
         */
        public static final String REGEX_USERNAME      = "^[\\w\\u4e00-\\u9fa5]{6,20}(?<!_)$";
        /**
         * 正则：yyyy-MM-dd 格式的日期校验，已考虑平闰年
         */
        public static final String REGEX_DATE          = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$";
        /**
         * 正则：IP 地址
         */
        public static final String REGEX_IP            = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";

        /**
         * 正则：双字节字符(包括汉字在内)
         */
        public static final String REGEX_DOUBLE_BYTE_CHAR     = "[^\\x00-\\xff]";
        /**
         * 正则：QQ 号
         */
        public static final String REGEX_TENCENT_NUM          = "[1-9][0-9]{4,}";
        /**
         * 正则：中国邮政编码
         */
        public static final String REGEX_ZIP_CODE             = "[1-9]\\d{5}(?!\\d)";

    }

    /**
     * 验证手机号（精确）
     * @param input 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isPhone11(CharSequence input) {
        return isMatch(RegexConstants.REGEX_PHONE11, input);
    }

    /**
     * 验证手机号（精确）
     * @param input 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isMobileExact(CharSequence input) {
        return isMatch(RegexConstants.REGEX_MOBILE_EXACT, input);
    }

    /**
     * 验证固定电话号码
     * @param input 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isTel(final CharSequence input) {
        return isMatch(RegexConstants.REGEX_TEL, input);
    }

    /**
     * 验证身份证号码 15 位
     * @param input 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isIDCard15(final CharSequence input) {
        return isMatch(RegexConstants.REGEX_ID_CARD15, input);
    }

    /**
     * 验证身份证号码 18 位
     * @param input 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isIDCard18(final CharSequence input) {
        return isMatch(RegexConstants.REGEX_ID_CARD18, input);
    }

    /**
     * 验证邮箱
     * @param input 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isEmail(final CharSequence input) {
        return isMatch(RegexConstants.REGEX_EMAIL, input);
    }

    /**
     * 验证 URL
     * @param input 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isURL(final CharSequence input) {
        return isMatch(RegexConstants.REGEX_URL, input);
    }

    /**
     * 验证汉字
     * @param input 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isZh(final CharSequence input) {
        return isMatch(RegexConstants.REGEX_ZH, input);
    }

    /**
     * 验证用户名
     * <p>取值范围为 a-z,A-Z,0-9,"_",汉字，不能以"_"结尾,用户名必须是 6-20 位</p>
     * @param input 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isUsername(final CharSequence input) {
        return isMatch(RegexConstants.REGEX_USERNAME, input);
    }

    /**
     * 验证 yyyy-MM-dd 格式的日期校验，已考虑平闰年
     * @param input 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isDate(final CharSequence input) {
        return isMatch(RegexConstants.REGEX_DATE, input);
    }

    /**
     * 验证 IP 地址
     * @param input 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isIP(final CharSequence input) {
        return isMatch(RegexConstants.REGEX_IP, input);
    }

    /**
     * 判断是否匹配正则
     * @param regex 正则表达式
     * @param input 要匹配的字符串
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isMatch(@NonNull final String regex, @Nullable final CharSequence input) {
        return input != null && input.length() > 0 && Pattern.matches(regex, input);
    }

    /**
     * 获取正则匹配的部分
     * @param regex 正则表达式
     * @param input 要匹配的字符串
     * @return 正则匹配的部分
     */
    @Nullable
    public static List<String> getMatches(@NonNull final String regex, @Nullable final CharSequence input) {
        if (input == null) return null;
        List<String> matches = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        return matches;
    }

    /**
     * 获取正则匹配分组
     * @param input 要分组的字符串
     * @param regex 正则表达式
     * @return 正则匹配分组
     */
    @Nullable
    public static String[] getSplits(@Nullable final String input, @NonNull final String regex) {
        if (input == null) return null;
        return input.split(regex);
    }

    /**
     * 替换正则匹配的第一部分
     * @param input       要替换的字符串
     * @param regex       正则表达式
     * @param replacement 代替者
     * @return 替换正则匹配的第一部分
     */
    @Nullable
    public static String getReplaceFirst(@Nullable final String input, @NonNull final String regex, @NonNull final String replacement) {
        if (input == null) return null;
        return Pattern.compile(regex).matcher(input).replaceFirst(replacement);
    }

    /**
     * 替换所有正则匹配的部分
     * @param input       要替换的字符串
     * @param regex       正则表达式
     * @param replacement 代替者
     * @return 替换所有正则匹配的部分
     */
    @Nullable
    public static String getReplaceAll(@Nullable final String input, @NonNull final String regex, final String replacement) {
        if (input == null) return null;
        return Pattern.compile(regex).matcher(input).replaceAll(replacement);
    }

    /**
     * 校验银行卡卡号
     * @param cardId
     * @return
     */
    public static boolean checkBankCard(String cardId) {
        char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
        if(bit == 'N'){
            return false;
        }
        return cardId.charAt(cardId.length() - 1) == bit;
    }
    /**
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
     * @param nonCheckCodeCardId
     * @return
     */
    public static char getBankCardCheckCode(String nonCheckCodeCardId){
        if(nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
//如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for(int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if(j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char)((10 - luhmSum % 10) + '0');
    }

}


