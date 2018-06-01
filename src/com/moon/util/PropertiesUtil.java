package com.moon.util;

import java.io.*;
import java.util.Enumeration;
import java.util.Properties;

/**
 * 属性工具类
 * @author yujiangtao
 * @date 2018/5/28 21:13
 */
public class PropertiesUtil {

    /**
     * 获取指定属性文件的指定键的值
     * @param propFileName 属性文件的位置
     * @param propKey 属性文件的键
     * @return
     */
    public static String getPropertiesValue(String propFileName, String propKey) {
        Properties properties = new Properties();
        InputStream in = null;
        String propVal = null;
        try {
            // 读取配置文件
            in = new FileInputStream(new File(System.getProperty("user.dir") + "/src/com/" + propFileName));
            // 使用properties对象加载输入流
            properties.load(in);
            // 获取key对应的value值
            propVal = properties.getProperty(propKey);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return propVal;
    }

    /**
     * 设置属性文件对应键的值，并持久化
     * @param propFileName 属性文件名
     * @param propKey 属性键
     * @param propVal 属性值
     */
    public static void setPropertiesValue(String propFileName, String propKey, String propVal) {
        Properties properties = new Properties();
        InputStream in = null;
        FileWriter fw = null;
        try {
            // 读取配置文件
            in = new FileInputStream(new File(System.getProperty("user.dir") + "/src/com/" + propFileName));
            // 使用properties对象加载输入流
            properties.load(in);
            Enumeration<?> enumeration = properties.propertyNames();
            while(enumeration.hasMoreElements()) {
                String key = (String) enumeration.nextElement();
                if(propKey.equals(key)) {
                    // 获取key对应的value值
                    properties.setProperty(propKey, propVal);
                } else {
                    properties.setProperty(key, properties.getProperty(key));
                }
            }
            File file = new File(System.getProperty("user.dir") + "/src/com/" + propFileName);
            fw = new FileWriter(file, false);
            // 写出属性文件
            properties.store(fw, null);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fw != null) {
                try {
                    fw.flush();
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取默认属性文件的键的值
     * @param propKey 属性键
     * @return
     */
    public static String getDefaultPropertiesValue(String propKey) {
        return getPropertiesValue("snake.properties", propKey);
    }

    /**
     * 设置默认属性文件的对应键的值，并持久化
     * @param propKey 属性键
     * @param propVal 属性值
     */
    public static void setDefaultPropertiesValue( String propKey, String propVal) {
        setPropertiesValue("snake.properties", propKey, propVal);
    }

    public static void main(String[] args) {
        PropertiesUtil.setPropertiesValue("snake.properties", "history.max.score", "30");
        //System.out.println(PropertiesUtil.getPropertiesValue("snake.properties", "history.max.score"));
    }
}
