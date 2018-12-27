package com.huawei.adapter.common.util.exception;

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
	
	@Override
    public String getMessage()
    {      
        Throwable ourCause = this.getCause();
        if (ourCause != null)
        {
            StackTraceElement[] stackTrace = ourCause.getStackTrace();
            StringBuilder sb = new StringBuilder();
            sb.append("{[Exception Message : ");
            sb.append(super.getMessage());
            sb.append("], [Exception Location : ");
            if (stackTrace.length > 0)
            {
                sb.append(stackTrace[0].toString());
            }
            sb.append("]}");
            
            return sb.toString();
        }
        else
        {
            return "The Exception's Cause is null, so can not get stack";
        }
    }

}


