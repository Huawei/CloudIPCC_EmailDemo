package com.huawei.adapter.icsserver.bean;

import java.util.List;

/**
 * 
 * <p>Title:  技能队列统计信息查询对象</p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @version V1.0 2015年7月25日
 * @since
 */
public class StatisticParam
{
	/**
	 * vdnId
	 */
	private int vdnId;
	
	/**
	 * 待查询的接入码
	 */
	private List<String> accessCodes;

	public int getVdnId()
	{
		return vdnId;
	}

	public void setVdnId(int vdnId)
	{
		this.vdnId = vdnId;
	}

	public List<String> getAccessCodes()
	{
		return accessCodes;
	}

	public void setAccessCodes(List<String> accessCodes)
	{
		this.accessCodes = accessCodes;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("vdnId:").append(vdnId).append(",");
		sb.append("accessCodes").append(accessCodes).append("}");
		return sb.toString();
	}
}
