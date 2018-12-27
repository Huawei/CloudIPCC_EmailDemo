
package com.huawei.adapter.common.util.utils;


import java.io.IOException;
import java.io.StringWriter;



import java.util.regex.Pattern;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * <p>Title: 字符串处理工具类(String Processing Tools)</p>
 * <p>Description:  字符串处理工具类(String Processing Tools)</p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @version V1.0 2014年9月2日
 * @since
 */
public abstract class StringUtils
{
    
    private static final String PHONE_PATTERN = "[0-9]{1,24}";
    

    
    /**
     * 判断字符串是否为null或者空字符串（不含空格）。
     * Determine whether the string is null or empty string (no spaces).
     * @param str 字符串变量(String input)
     * @return true/false
     */
    public static boolean isNullOrEmpty(String str)
    {
        return str == null || str.isEmpty();
    }
    
    /**
     * 判断字符串是否为null或者为空字符串（含空格）。
     * Determine whether the string is null or empty string (including spaces).
     * @param str 字符串变量(String Input)
     * @return true/false
     */
    public static boolean isNullOrBlank(String str)
    {
        return str == null || str.trim().isEmpty();
    }
    
    
    /**
     * 对象转json字符串
     * Object to json
     * @param object 对象(object)
     * @return json json字符串(Json String)
     * @throws IOException
     */
    public static String beanToJson(Object object) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        JsonGenerator gen = new JsonFactory().createJsonGenerator(writer);
        mapper.writeValue(gen, object);
        gen.close();
        String json = writer.toString();
        writer.close();
        return json;
    }
    
    /**
     * 匹配电话号码
     * @param phone
     * @return
     */
    public static boolean isPhoneNumber(String phone)
    {
        if (isNullOrBlank(phone))
        {
            return false;
        }
        return Pattern.matches(PHONE_PATTERN, phone);
    }
    
}
