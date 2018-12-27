
package com.huawei.prometheus.comm;


/**
 * <p>Title: 日志格式化类 </p>
 * <p>Description: </p>
 * <pre></pre>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: 华为技术有限公司</p>
 */
public class LogUtils
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
     * number 3
     */
    private static final int NUM_3 = 3;

 
   
   
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
     * 格式化 email 地址(隐藏中间1/3个字符)
     * @param email email address
     * @return      result
     */
    public static String formatEmail(String email) 
    {
        if (email == null || email.isEmpty()) 
        {
            return encodeForLog(email);
        }
        
        int index = email.indexOf('@');
        if (index < NUM_1)
        {
            return encodeForLog(email);
        }
        
        String name = email.substring(0, index);
        StringBuilder sb = new StringBuilder();
        if (index == NUM_1 || index == NUM_2)
        {
            sb.append('*').append(email.substring(NUM_1));
        }
        else 
        {
            // 隐藏中间1/3个字符
            int startIndex = name.length() / NUM_3;    // 左边数1/3
            int endIndex = name.length() - startIndex; // 右边数1/3
            sb.append(name.substring(0, startIndex));
            for (int i = startIndex; i < endIndex; i++)
            {
                sb.append('*');
            }
            sb.append(email.substring(endIndex));
        }
        return encodeForLog(sb.toString());
    }
}
