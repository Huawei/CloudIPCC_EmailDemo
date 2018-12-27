package com.huawei.adapter.common.config;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.huawei.adapter.common.util.utils.LogUtils;

/**
 * 
 * <p>Title:  加密解密工具类</p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public final class AESEncryptor
{
	/**
     * Logger for this class
     */
    private static Logger logger = LoggerFactory.getLogger(AESEncryptor.class);

    /**
     * 128
     */
    private static final int NUMBER_128 = 128;

    /**
     * 编码格式
     */
    private static final String UTF_8 = "utf-8";

    /** 
     * 全局密钥
     */
    private String secretKey = "";

    /**
	 * 盐值的字节大小
	 */
    private static final int SALT_BYTE_SIZE = 16;

    /**
     * 连接字符串
     */
    private static final char SPLIT = ';';

    /**
     *  导出密钥的迭代次数
     */
    private static int DK_ITER_COUNT = 50000;

    /**
     * 构造函数
     */
    public AESEncryptor(String secretKey, String salt)
    {
        int iterCount = 0;
        try
        {
            //获取迭代次数
            iterCount = Integer.valueOf(RootKeyManager
                    .getValueFromKeysMap("CRYPT_PKBDF2_ITERATION_COUNT"));
        }
        catch (NumberFormatException e)
        {
            iterCount = DK_ITER_COUNT;
            logger.error("CRYPT_PKBDF2_ITERATION_COUNT is invalid");
        }
        this.secretKey = EncryptUtils.encryptWithPBKDF2WithSHA256(secretKey, salt, iterCount);
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
            SecretKeySpec key = new SecretKeySpec(getAESKey(), RootKeyManager.getValueFromKeysMap("CRYPT_AES_KEY_ALGORITHM"));
            Cipher cipher = Cipher.getInstance(RootKeyManager.getValueFromKeysMap("CRYPT_CIPHER_TRANSFORMATION"));
            IvParameterSpec iv = new IvParameterSpec(salt);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            ciphertext = cipher.doFinal(plaintext);
        }
        catch (RuntimeException e) 
        {
        	logger.error("encryptAES error. {}", LogUtils.encodeForLog(e.getMessage()));
            throw new RuntimeException("encryptAES failed.");
        }
        catch (Exception e) 
        {
        	logger.error("encryptAES error. {}", LogUtils.encodeForLog(e.getMessage()));
            throw new RuntimeException("encryptAES failed.");
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
            SecretKeySpec key = new SecretKeySpec(getAESKey(), RootKeyManager.getValueFromKeysMap("CRYPT_AES_KEY_ALGORITHM"));
            Cipher cipher = Cipher.getInstance(RootKeyManager.getValueFromKeysMap("CRYPT_CIPHER_TRANSFORMATION"));
            IvParameterSpec iv = new IvParameterSpec(salt);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            plaintext = cipher.doFinal(ciphertext);
        } 
        catch (RuntimeException e) 
        {
        	logger.error("decryptAES error. {}", LogUtils.encodeForLog(e.getMessage()));
            throw new RuntimeException("decryptAES failed.");
        }
        catch (Exception e) 
        {
        	logger.error("decryptAES error. {}", LogUtils.encodeForLog(e.getMessage()));
            throw new RuntimeException("decryptAES failed.");
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
        KeyGenerator kgen = KeyGenerator.getInstance(RootKeyManager.getValueFromKeysMap("CRYPT_AES_KEY_ALGORITHM"));
        SecureRandom secureRandom = SecureRandom.getInstance(RootKeyManager.getValueFromKeysMap("CRYPT_AES_KEY_SECURERANDOM_ALGORITHM"));
        secureRandom.setSeed(secretKey.getBytes(UTF_8)); 
        int contentLength = 0;
        try
        {
            //AES加密算法的key的长度
            contentLength = Integer.valueOf(RootKeyManager
                    .getValueFromKeysMap("CRYPT_AES_KEY_CONTENT_LENGTH"));
        }
        catch (NumberFormatException e)
        {
            contentLength = NUMBER_128;
            logger.error("CRYPT_AES_KEY_CONTENT_LENGTH is invalid");
        } 
        kgen.init(contentLength, secureRandom);
        SecretKey tmpSecretKey = kgen.generateKey();
        return tmpSecretKey.getEncoded();
    }

    /**
	 * 获取盐值
	 * @return 返回随机数字
	 */
	public static String getSalt()
	{
	    int contentLength = 0;
        try
        {
            //获取盐值的长度
            contentLength = Integer.valueOf(RootKeyManager
                    .getValueFromKeysMap("CRYPT_SALT_BYTE_SIZE"));
        }
        catch (NumberFormatException e)
        {
            contentLength = SALT_BYTE_SIZE;
            logger.error("CRYPT_SALT_BYTE_SIZE is invalid");
        } 
        byte[] salt = new byte[contentLength];
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
