package com.huawei.prometheus.comm;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>Title:  加密解密工具类 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: 华为技术有限公司</p>
 */
public final class StandardEncryptor
{
	/**
     * Logger for this class
     */
    private static Logger logger = LoggerFactory.getLogger(StandardEncryptor.class);
    
    // 导出密钥的迭代次数
    private static int DK_ITER_COUNT = 50000;
    
    // 导出密钥的字节长度
    private static int DK_LENGTH = 256;
    
    /**
     * 128
     */
    private static final int NUMBER_128 = 128;
    
    /**
     * 编码格式
     */
    private static final String UTF_8 = "utf-8";
    
    // 密钥
    private byte[] secretKey = null;

    
    /**
	 * 盐值的字节大小
	 */
    private static final int SALT_BYTE_SIZE = 16;
    
    /**
     * 连接字符串
     */
    private static final char SPLIT = ';';
    
    
    /**
     * 构造函数
     */
    public StandardEncryptor(String secretKey)
    {

   	   try 
        {
            this.secretKey = encryptPBKDF2(secretKey, "uOQjK0S/J5n1X0Yjv+tGFg==");
        }
        catch (UnsupportedEncodingException e)
        {
            logger.error("StandardEncryptor init failed, not support {} encoding.", UTF_8);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    /**
     * 使用PBKDF2算法进行加密
     * @param text 明文 
     * @param salt 盐值
     * @return     密文
     * @throws UnsupportedEncodingException if not support UTF-8
     */
    public static byte[] encryptPBKDF2(String text, String salt) throws UnsupportedEncodingException
    {
        PKCS5S2ParametersGenerator generator = new PKCS5S2ParametersGenerator();
        generator.init(PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(text.toCharArray()), 
                salt.getBytes(UTF_8), DK_ITER_COUNT);
        KeyParameter key = (KeyParameter)generator.generateDerivedMacParameters(DK_LENGTH);
        byte[] keyBytes = key.getKey();
        return keyBytes;
    }
    
    /**
     * AES 加密
     * @param plaintext 明文
     * @return 密文
     */
    public String encryptAES(String plaintext)
    {
        if (plaintext == null || plaintext.isEmpty())
        {
            return plaintext;
        }
        String encryptPwd = null;
        String salt = getSalt();
        try
        {
            encryptPwd = encryptAES(plaintext.getBytes(UTF_8), Base64.decodeBase64(salt.getBytes(UTF_8)));
        }
        catch (UnsupportedEncodingException e)
        {
            logger.error("encryptAES error. {}", LogUtils.encodeForLog(e.getMessage()));
        }
        return salt + SPLIT + encryptPwd;
    }
    
    /**
     * AES 解密
     * @param ciphertext 密文
     * @return           明文
     */
    public String decryptAES(String ciphertext)
    {
        if (ciphertext == null || ciphertext.isEmpty())
        {
            return ciphertext;
        }
        String decryptPwd = null;
        try
        {
        	// 2. 将密文拆分成盐值和密码
        	byte []salt = null;
            String oldPass = "";
            int commaIdx = ciphertext.indexOf(SPLIT);
            if (commaIdx == -1)
            {
                salt = null;
                oldPass = ciphertext;
            }
            else
            {
            	salt = Base64.decodeBase64(ciphertext.substring(0, commaIdx).getBytes(UTF_8));
                oldPass = ciphertext.substring(commaIdx + 1);
            }
            
            decryptPwd = decryptAES(oldPass.getBytes(UTF_8), salt);
        }
        catch (UnsupportedEncodingException e)
        {
            logger.error("decryptAES error. {}", LogUtils.encodeForLog(e.getMessage()));
        }
        return decryptPwd;
    }
    
    /**
     * AES 加密
     * @param  plaintext 明文
     * @param salt 盐值
     * @return           密文
     * @throws UnsupportedEncodingException 
     */
    private String encryptAES(byte[] plaintext, byte[] salt) throws UnsupportedEncodingException
    {
        byte[] ciphertext = null;
      
        try 
        {
            SecretKeySpec key = new SecretKeySpec(getAESKey(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(salt);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            ciphertext = cipher.doFinal(plaintext);
        }
        catch (RuntimeException e) 
        {
        	logger.error("encryptAES error. {}", LogUtils.encodeForLog(e.getMessage()));
            throw new RuntimeException("encryptAES " + new String(plaintext, UTF_8) + " failed.");
        }
        catch (Exception e) 
        {
        	logger.error("encryptAES error. {}", LogUtils.encodeForLog(e.getMessage()));
            throw new RuntimeException("encryptAES " + new String(plaintext, UTF_8) + " failed.");
        }
        
        return new String(Base64.encodeBase64(ciphertext), UTF_8);
    }
    
    /**
     * AES 解密
     * @param ciphertext 密文
     * @param salt 盐值
     * @return           明文
     * @throws UnsupportedEncodingException 
     */
    private String decryptAES(byte[] ciphertext, byte[] salt) throws UnsupportedEncodingException
    {
        ciphertext = Base64.decodeBase64(ciphertext);
        byte[] plaintext = null;
        try 
        {
    		SecretKeySpec key = new SecretKeySpec(getAESKey(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(salt);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            plaintext = cipher.doFinal(ciphertext);
        } 
        catch (RuntimeException e) 
        {
        	logger.error("decryptAES error. {}", LogUtils.encodeForLog(e.getMessage()));
            ciphertext = Base64.encodeBase64(ciphertext);
            throw new RuntimeException("decryptAES " + new String(ciphertext, UTF_8) + " failed.");
        }
        catch (Exception e) 
        {
        	logger.error("decryptAES error. {}", LogUtils.encodeForLog(e.getMessage()));
            ciphertext = Base64.encodeBase64(ciphertext);
            throw new RuntimeException("decryptAES " + new String(ciphertext, UTF_8) + " failed.");
        }
        
        return new String(plaintext, UTF_8);
    }
    
    /**
     * 获取 AES 加密算法的 KEY
     * @return key
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException 
     */
    private byte[] getAESKey() throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(secretKey); 
        kgen.init(NUMBER_128, secureRandom);
        SecretKey tmpSecretKey = kgen.generateKey();
        return tmpSecretKey.getEncoded();
    }
    
    

    
    /**
	 * 获取盐值
	 * @return 返回随机数字
	 */
	private static String getSalt()
	{
		byte[] salt = new byte[SALT_BYTE_SIZE];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);
        try
		{
			return new String(Base64.encodeBase64(salt), UTF_8);
		}
        catch (UnsupportedEncodingException e)
		{
			logger.error("getSalt failed, error is {}", LogUtils.encodeForLog(e.getMessage()));
			return null;
		}
	}
	
	
}
