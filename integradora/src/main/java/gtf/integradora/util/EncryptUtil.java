
package gtf.integradora.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptUtil {
    private static final String SECRET_KEY = "1234567890123456"; // 16 caracteres
    private static final String ALGORITHM = "AES";

    public static String encrypt(String plainText) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(plainText.getBytes());
        String encoded = Base64.getEncoder().encodeToString(encrypted);
        return "ENC(" + encoded + ")";
    }
    
    public static String decrypt(String encryptedText) throws Exception {
        if (encryptedText.startsWith("ENC(") && encryptedText.endsWith(")")) {
            encryptedText = encryptedText.substring(4, encryptedText.length() - 1); // quitar ENC(...)
        }
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decoded = Base64.getDecoder().decode(encryptedText);
        byte[] original = cipher.doFinal(decoded);
        return new String(original);
    }
}