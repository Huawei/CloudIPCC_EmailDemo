
package com.huawei.prometheus.dao;

import java.io.InputStream;
import java.util.Map;

/**
 * 
 * <p>Title:  </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: 华为技术有限公司</p>
 */
public interface FileOperationDAO
{
    /**
     * 存储文件到Mongo数据库
     * @param m 参数  
     *    key       value 
     *    content    二进制文件内容
     *    fileName   文件名
     *    tableName  表名 oracle时使用
     *    collectionName 集合名 Mongo时使用
     *    id         返回值，记录ID
     * @return 数据库主键ID
     */
    public String saveFile(Map<String, Object> m);
    
    

    
    /**
     * 存储SNS图片文件到Mongo数据库
     * 
     * @param m 参数  
     *    key       value 
     *    content    二进制文件内容
     *    fileName   文件名
     *    collectionName  表名或集合名
     *    contentType  图片格式
     *    id         返回值，记录ID
     * @return 数据库主键ID
     */
    public String saveImageFile(Map<String, Object> m);
    
    /**
     * 删除文件内容
     * @param m
     *    key                 value
     *   collectionName       集合名，Mongo时使用
     *   tableName            表名，oracle时使用
     *   fileName             文件名 
     */
    public void removeImageFile(Map<String, Object> m);    
    
    /**
     * 获取文件内容
     * @param m
     *    key                 value
     *   collectionName       集合名，Mongo时使用
     *   tableName            表名，oracle时使用
     *   id                   文件ID 
     *   content              文件内容， byte[]类型
     * @return 文件内容
     */
    public InputStream getFile(Map<String, Object> m);
    
    
    /**
     * 获取文件的byte数组格式
     * @param m 参数 同getFile方法
     * @return 文件内容
     */
    public byte[] getFileByteArray(Map<String, Object> m);
    
    /**
     * 获取文件内容
     * @param m
     *    key                 value
     *   collectionName       集合名，Mongo时使用
     *   tableName            表名，oracle时使用
     *   id                   文件ID 
     * @return 文件名
     */
    public String getImageFileName(Map<String, Object> m);
    
    /**
     * 获取文件内容
     * @param m
     *    key                 value
     *   collectionName       集合名，Mongo时使用
     *   tableName            表名，oracle时使用
     *   fileName             文件名 
     * @return 文件名
     */
    public String getImageFileIdByFileName(Map<String, Object> m);
   
}
