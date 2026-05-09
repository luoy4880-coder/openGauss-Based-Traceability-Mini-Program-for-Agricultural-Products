package com.yujia.backend.common.util;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AddressParser {

    private static final Pattern PROVINCE_PATTERN = Pattern.compile("([\\u4e00-\\u9fa5]{2,}(?:省|自治区|特别行政区)|北京市|天津市|上海市|重庆市)");
    private static final Pattern CITY_PATTERN = Pattern.compile("([\\u4e00-\\u9fa5]{2,}(?:市|州|盟|地区))");
    private static final Pattern DISTRICT_PATTERN = Pattern.compile("([\\u4e00-\\u9fa5]{2,}(?:区|县|市|旗))");

    private AddressParser() {
    }

    public static ParsedAddress parse(String text) {
        String source = text == null ? "" : text.trim();
        ParsedAddress address = new ParsedAddress();
        if (!StringUtils.hasText(source)) {
            return address;
        }

        address.setProvince(findFirst(PROVINCE_PATTERN, source));
        String afterProvince = removePrefixThrough(source, address.getProvince());
        address.setCity(findFirst(CITY_PATTERN, StringUtils.hasText(afterProvince) ? afterProvince : source));
        String afterCity = removePrefixThrough(StringUtils.hasText(afterProvince) ? afterProvince : source, address.getCity());
        address.setDistrict(findFirst(DISTRICT_PATTERN, StringUtils.hasText(afterCity) ? afterCity : source));
        return address;
    }

    private static String findFirst(Pattern pattern, String source) {
        Matcher matcher = pattern.matcher(source);
        return matcher.find() ? matcher.group(1) : null;
    }

    private static String removePrefixThrough(String source, String matched) {
        if (!StringUtils.hasText(source) || !StringUtils.hasText(matched)) {
            return source;
        }
        int index = source.indexOf(matched);
        if (index < 0) {
            return source;
        }
        return source.substring(index + matched.length());
    }

    public static class ParsedAddress {
        private String province;
        private String city;
        private String district;

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }
    }
}
