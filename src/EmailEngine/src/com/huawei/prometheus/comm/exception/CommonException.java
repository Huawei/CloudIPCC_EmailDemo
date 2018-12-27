package com.huawei.prometheus.comm.exception;

/**
 * 
 * <p>Title:  用于通用异常输出</p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @author j00204006
 * @version V1.0 2014年11月12日
 * @since
 */
public class CommonException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CommonException()
	{
		
	}
	
	public CommonException(String message)
	{
		super(message);
	}
	
	public CommonException(Throwable throwable)
	{
		super(throwable);
	}
	
	public CommonException(String message, Throwable throwable)
	{
		super(message, throwable);
	}
}
