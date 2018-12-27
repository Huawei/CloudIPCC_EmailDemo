
package com.huawei.prometheus.comm;



/**

 * <p>Title:  字符串的工具类 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: 华为技术有限公司</p>
 */
public final class StringUtils
{

    
    /**
     * 是否为null或空字符串
     * @param str 字符串
     * @return 为null或空返回true
     */
    public static boolean isNullOrEmpty(String str)
    {
        return str == null || str.isEmpty();
    }
    
    /**
     * 判断字符串是否为null或者为空字符串（含空格）。
     * @param str 字符串变量
     * @return true/false
     */
    public static boolean isNullOrBlank(String str)
    {
        return str == null || str.trim().isEmpty();
    }
    
    
    
    
    
}
