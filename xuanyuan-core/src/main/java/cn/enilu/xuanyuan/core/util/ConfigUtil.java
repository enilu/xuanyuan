package cn.enilu.xuanyuan.core.util;

import java.util.Properties;

/**
 * descript
 *
 * @Author enilu
 * @Date 2021/4/26 12:17
 * @Version 1.0
 */
public class ConfigUtil {
    static  Properties properties = null;
    public static void init(Properties input){
        properties = input;
    }
    public static String get(String key){
        return properties.getProperty(key);
    }
}
