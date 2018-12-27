package com.huawei.adapter.common.business;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.adapter.bean.AccessCodeInfo;
import com.huawei.adapter.common.data.BusinessConfigData;
import com.huawei.adapter.common.util.utils.LogUtils;

/**
 * 
 * <p>Title: 解析accesscode.xml </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class AccessConfigReader
{
	private static final Logger LOG = LoggerFactory.getLogger(AccessConfigReader.class);

	/**
     * 解析accesscodes.xml
     * 
     * @param filePath
     *            accesscodes.xml的地址
     */
    @SuppressWarnings("unchecked")
    public static boolean readAccessXML(String filePath)
    {
        SAXReader reader = new SAXReader();
        File file = new File(filePath);
        Document document;
        try
        {
            document = reader.read(file);
        }
        catch (DocumentException e)
        {
            LOG.error("readAccessXML failed.\r\n {}", LogUtils.encodeForLog(e.getMessage()));
            return false;
        }

        Element root = document.getRootElement(); // 获取根节点
        List<Element> vdnElements = root.elements();

        Map<String, AccessCodeInfo> map = new HashMap<String, AccessCodeInfo>();
        int length = vdnElements.size();
        Element vdnChild;
        List<Element> accessChilds;
        int vdnId;
        Attribute vdnAttr;
        for (int i = 0; i < length; i++)
        {
            vdnChild = vdnElements.get(i);
            vdnAttr = vdnChild.attribute("id");
            if (null == vdnAttr)
            {
                // 没有VDNID
                LOG.error("readAccessXML failed. vdnId is null");
                return false;
            }
            try
            {
                vdnId = Integer.valueOf(vdnAttr.getValue());
            }
            catch (NumberFormatException e)
            {
                LOG.error("readAccessXML failed. vdnId is not number. \r\n {}", LogUtils.encodeForLog(e.getMessage()));
                return false;
            }
            accessChilds = vdnChild.elements();

            parseAccess(vdnId, map, accessChilds);
        }
        BusinessConfigData.setAccessMap(map);
        return true;
    }

    /**
     * 解析access节点
     * 
     * @param vdnId
     * @param map
     * @param accessChilds
     * @param prefixList
     * @return
     */
    private static void parseAccess(int vdnId, Map<String, AccessCodeInfo> map, List<Element> accessChilds)
    {
        Element accessChild;
        AccessCodeInfo info;
        String accesscode;// 接入码
        for (int i = 0; i < accessChilds.size(); i++)
        {
            accessChild = accessChilds.get(i);
            accesscode = accessChild.elementTextTrim("accessCode");
            info = new AccessCodeInfo(vdnId, accesscode);
            map.put(accesscode, info);
        }
    }
}
