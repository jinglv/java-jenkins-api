package com.jenkins.api.tools;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Json解析工具类
 *
 * @author jinglv
 * @date 2021/02/12
 */
public class JsonParserTools {
    public static final Logger logger = LoggerFactory.getLogger(JsonParserTools.class);


    /**
     * 解析Json内容
     *
     * @param jsonString String
     * @param jsonId     String
     * @return jsonValue String
     */
    public static String getJsonValue(String jsonString, String jsonId) {
        logger.info("解析Json字符串:{}", jsonString);
        logger.info("解析Json关键字:{}", jsonId);
        String jsonValue = "";
        if (jsonString == null || jsonString.trim().length() < 1) {
            return null;
        }
        try {
            JSONObject obj = new JSONObject(jsonString);
            jsonValue = obj.getString(jsonId);
        } catch (Exception e) {
            logger.error("解析字符串失败：{}", e.getMessage());
            e.printStackTrace();
        }
        logger.info("解析完成的json结果：{}", jsonValue);
        return jsonValue;
    }

    /**
     * 解析Json内容
     *
     * @param jsonString String
     * @param jsonId     String
     * @return Boolean
     */
    public static Boolean getJsonBoolean(String jsonString, String jsonId) {
        logger.info("解析Json字符串:{}", jsonString);
        logger.info("解析Json关键字:{}", jsonId);
        Boolean jsonValue = null;
        if (jsonString == null || jsonString.trim().length() < 1) {
            return false;
        }
        try {
            JSONObject obj = new JSONObject(jsonString);
            jsonValue = obj.getBoolean(jsonId);
        } catch (Exception e) {
            logger.error("解析字符串失败：{}", e.getMessage());
            e.printStackTrace();
        }
        logger.info("解析完成的json结果：{}", jsonValue);
        if (jsonValue == null) {
            return false;
        }
        return jsonValue;
    }
}
