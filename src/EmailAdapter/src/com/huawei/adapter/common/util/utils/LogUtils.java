
package com.huawei.adapter.common.util.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * <p>Title: 日志工具 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public abstract class LogUtils
{

    
    /**
     * number 1
     */
    private static final int NUM_1 = 1;
    
    /**
     * number 2
     */
    private static final int NUM_2 = 2;
    
    /**
     * 4位的号码长度
     */
    private final static int PHONE_LEN_4 = 4;
    
    public static String formatUserName(String userName)
    {
        return formatPhoneNumber(userName);
    }
    
    /**
     * 格式化电话号码
     * @param phoneNumber phoneNumber
     * @return      phone.length<8 : phone, phone.length>=8 : 用 '*' 替换中间4位
     */
    public static String formatPhoneNumber(String phoneNumber)
    {
        if (null == phoneNumber || phoneNumber.isEmpty())
        {
            return "";
        }
        
        if (phoneNumber.length() <= PHONE_LEN_4)
        {
            return "****";
        }
        else
        {
            int length = phoneNumber.length();
            int begin = length / PHONE_LEN_4;
            int end = begin + PHONE_LEN_4;
            StringBuffer tempValue = new StringBuffer();
            tempValue.append(phoneNumber.substring(0, begin));
            tempValue.append("****");
            tempValue.append(phoneNumber.substring(end, length));
            return tempValue.toString();
        }
    }
    
  
    
    
    /**
     * 对用户输入内容进行编码
     * @param obj obj
     * @return    result
     */
    public static String encodeForLog(Object obj)
    {
        if (obj == null)
        {
            return "null";
        }
        String msg = obj.toString();
        int length = msg.length();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
        {
            char ch = msg.charAt(i);
            
            // 将\r\n替换成'_'
            if (ch == '\r' || ch == '\n')
            {
                ch = '_';
            }
            sb.append(Character.valueOf(ch));
        }
        return sb.toString();
    }
  
    
    /**
     * 格式化 map 对象, 对其中的主叫被叫进行 '*' 号处理
     * @param map map
     * @return    json string
     */
    @SuppressWarnings("unchecked")
    public static String formatICSEventMap(Map<String, Object> map) 
    {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        if (map == null)
        {
            sb.append('}');
            return sb.toString();
        }
        
        String[] phoneFields = {"caller", "called", "otherPhone", "number"};
        
        String[] otherFields = {"chatContent", "confInfo"};
        
        Iterator<Entry<String, Object>> it = map.entrySet().iterator();
        Entry<String, Object> entry;
        String key;
        Object value;
        while (it.hasNext())
        {
            entry = it.next();
            key = entry.getKey();
            value = entry.getValue();
            if (value instanceof Map)
            {
                value = formatICSEventMap((Map<String, Object>)value);
            }
            if (existInArray(phoneFields, key))
            {
                // 对主叫被叫的号码进行 '*' 处理
                value = formatPhoneNumber(String.valueOf(value));
            }
            
            if (existInArray(otherFields, key))
            {
                //对文字交谈内容进行 '*' 处理
                value = String.valueOf("******");
            }
            sb.append(key).append("=").append(value);
            sb.append(", ");
        }
        
        // 删除最后一个 ','
        int length = sb.length();
        if (length >= NUM_2 && sb.charAt(length - NUM_1) == ' ' && sb.charAt(length - NUM_2) == ',')
        {
            sb.deleteCharAt(length - NUM_2);
        }
        sb.append('}');
        return sb.toString();
    }
    
    /**
     * 判断 value 是否在 array 中
     * @param array
     * @param value
     * @return
     */
    private static boolean existInArray(String[] array, String value)
    {
        if (array == null || array.length == 0)
        {
            return false;
        }
        
        boolean exist = false;
        for (String tmpValue : array)
        {
            if (tmpValue != null && tmpValue.equals(value))
            {
                exist = true;
                break;
            }
        }
        return exist;
    }
}
