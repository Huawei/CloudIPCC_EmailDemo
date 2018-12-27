package com.huawei.adapter.common.util.utils;



import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ContainerNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>Title:  使用jackson的json转换帮助类</p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: 华为技术有限公司</p>
 * @since
 */
public abstract class JsonUtil
{
    /**
     * 记录日志的Logger对象。
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);
    
    private static ObjectMapper objectMapper = new ObjectMapper();
   
    
    /**
     * getBean时， 忽略多余字段。
     */
    static
    {
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
     
    
    /**     
     * 转化bean为json字符串   
     * @param o 对象  
     * @return bean json字符串
     */    
    public static String getJsonString(Object o) 
    {   
        String jsonStr = null;
        JsonGenerator jsonGenerator = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
        try
        {
            jsonGenerator = objectMapper.getJsonFactory().createJsonGenerator(baos, JsonEncoding.UTF8);
            jsonGenerator.writeObject(o);
            jsonStr = baos.toString("UTF-8");
        }
        catch (IOException e)
        {
            LOGGER.error("getJsonString fail, error message is \r\n {}.",
                    LogUtils.encodeForLog(e.getMessage()));
        }  
        finally
        {
            try
            {
                baos.close();
            }
            catch (IOException e)
            {
                LOGGER.error("getJsonString fail, error message is \r\n {}." , LogUtils.encodeForLog(e.getMessage()));
            }
            try 
            {
                if (jsonGenerator != null)
                {
                    jsonGenerator.close();
                }
            }
            catch (IOException e)
            {
                LOGGER.error(LogUtils.encodeForLog(e.getMessage()));
            }
        }
        return jsonStr;
    }
    
    
    
    /**
     * 从json字符串获取bean
     * @param <T> 类型
     * @param json json字符串
     * @param beanClass bean的class类
     * @return bean
     */
    public static <T> T getBean(String json, Class<T> beanClass)
    {
        T bean = null;
        
        if (StringUtils.isNullOrEmpty(json))
        {
            return null;
        }
        
        try
        {
            
            bean = objectMapper.readValue(json, beanClass);
        }
        catch (IOException e)
        {
            LOGGER.error("getBean beanClass fail, error message is {}.", 
                    LogUtils.encodeForLog(beanClass.getClass().getCanonicalName()),
                    LogUtils.encodeForLog(e.getMessage()));
        }
        return bean;
    }
   
    /**
     * 从json字符串获取bean
     * @param <T> 类型
     * @param json json字符串
     * @param beanClass bean 列表的class类
     * @return bean列表
     */
    public static <T> T[] getArray(String json, Class<T[]> beanClass)
    {
        T[] arr = null; 
        try
        {
            arr = objectMapper.readValue(json, beanClass);
        }
        catch (IOException e)
        {
            LOGGER.error("getBean fail, error message is {}.", LogUtils.encodeForLog(e.getMessage()));
        }
        return arr;
    }
    
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getMapFromJsonStr(String json)
    {
        try
        {
            return objectMapper.readValue(json, Map.class);
        }
        catch (IOException e)
        {
            LOGGER.error("getMapFromJsonStr fail, error message is {}.", LogUtils.encodeForLog(e.getMessage()));
        }
        return new HashMap<String, Object>();
    }
    
    /**
     * 从json格式的字符串中取出对应的value
     * @param json json字符串
     * @param key 欲取的key
     * @return value
     */
    public static String getValueFromJsonStr(String json, String key)
    {
        try
        {       
            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode keyNode = rootNode.findValue(key);
            if (keyNode != null)
            {
                if (keyNode instanceof ContainerNode)
                {
                    return keyNode.toString();
                }
                else
                {
                    return keyNode.asText();
                }
            }
        }
        catch (IOException e)
        {
            LOGGER.error("getValueFromJsonStr failed, json:{}, key:{}, error message is {}.", new Object[]{
                    LogUtils.encodeForLog(json), LogUtils.encodeForLog(key),
                    LogUtils.encodeForLog(e.getMessage())});
        }
        return  null;
    }
}
