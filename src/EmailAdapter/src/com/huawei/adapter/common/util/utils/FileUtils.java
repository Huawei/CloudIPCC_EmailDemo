
package com.huawei.adapter.common.util.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public abstract class FileUtils
{
    /**
     * 判断是否是安全目录
     * @return
     */
    public static boolean isInSecureDir(File file)
    {
        String canPath;
        try
        {
            canPath = file.getCanonicalPath();
        }
        catch (IOException e)
        {
            return false;
        }
        String absPath = file.getAbsolutePath();
        if (canPath.equalsIgnoreCase(absPath))
        {
            return true;
        }
        return false;
    }
    
    /**
     * 判断是否是安全目录
     * @return
     */
    public static boolean isRegularFile(Path filePath)
    {
        BasicFileAttributes attr;
        try
        {
            attr = Files.readAttributes(filePath, 
                    BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
            return attr.isRegularFile();
        }
        catch (IOException e)
        {
            return false;
        }
    }
}
