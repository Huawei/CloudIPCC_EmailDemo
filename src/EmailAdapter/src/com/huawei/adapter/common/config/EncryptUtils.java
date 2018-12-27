
package com.huawei.adapter.common.config;

import java.io.UnsupportedEncodingException;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.PKCS5S1ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.codec.binary.Base64;
import com.huawei.adapter.common.util.utils.LogUtils;

/**
 * 
 * <p>Title: 不可逆加密  </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class EncryptUtils
{
	private static final Logger LOGGER = LoggerFactory.getLogger(EncryptUtils.class);

    /**
     *  导出密钥的字节长度
     */
    private static int DK_LENGTH = 256;

    /**
     * 编码格式
     */
    private static final String UTF_8 = "utf-8";

    /**
     * 使用SHA256的PBKDF2进行加密
     * @param plaintext 明文
     * @param salt      盐值
     * @return          密文
     */
    public static String encryptWithPBKDF2WithSHA256(String plaintext, String salt, int count)
    {
        // 1. 校验参数
        plaintext = (plaintext != null) ? plaintext : "";

        // 2. 加密
        String ciphertext = "";

        int contentLength = 0;
        try
        {
            // 获取密钥的字节长度
            contentLength = Integer.valueOf(RootKeyManager.getValueFromKeysMap("CRYPT_PKBDF2_ENCRYPT_LENGTH"));
        }
        catch (NumberFormatException e)
        {
            contentLength = DK_LENGTH;
            LOGGER.error("CRYPT_PKBDF2_ENCRYPT_LENGTH is invalid");
        }

        try 
        {
            PKCS5S1ParametersGenerator generator = new PKCS5S1ParametersGenerator(new SHA256Digest());
            generator.init(PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(plaintext.toCharArray()), 
                    salt.getBytes(UTF_8), count);
            KeyParameter key = (KeyParameter)generator.generateDerivedMacParameters(contentLength);
            ciphertext = new String(Base64.encodeBase64(key.getKey()), UTF_8);
        }
        catch (UnsupportedEncodingException e)
        {
            LOGGER.error("encryptWithPBKDF2 failed, {}", LogUtils.encodeForLog(e.getMessage()));
        }
        return ciphertext;
    }

}
