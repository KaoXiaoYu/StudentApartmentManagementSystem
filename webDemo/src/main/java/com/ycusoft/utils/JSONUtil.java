package com.ycusoft.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.InputStream;

/**
 * JSON工具类
 * 提供JSON序列化和反序列化功能
 *
 * @author hq
 * @since 2026-05-25
 */
public class JSONUtil {
    /**
     * Jackson ObjectMapper实例（线程安全）
     */
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * 将对象序列化为JSON字符串
     *
     * @param obj 待序列化对象
     * @return JSON字符串
     */
    public static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON序列化失败", e);
        }
    }

    /**
     * 将JSON字符串反序列化为对象
     *
     * @param json  JSON字符串
     * @param clazz 目标对象类型
     * @param <T>   目标类型泛型
     * @return 反序列化后的对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("JSON反序列化失败", e);
        }
    }

    /**
     * 从InputStream读取JSON并反序列化为对象
     *
     * @param inputStream 输入流
     * @param clazz       目标对象类型
     * @param <T>         目标类型泛型
     * @return 反序列化后的对象
     */
    public static <T> T fromJson(InputStream inputStream, Class<T> clazz) {
        try {
            return objectMapper.readValue(inputStream, clazz);
        } catch (Exception e) {
            throw new RuntimeException("从InputStream反序列化JSON失败", e);
        }
    }
}