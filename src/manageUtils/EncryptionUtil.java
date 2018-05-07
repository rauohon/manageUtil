package manageUtils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class EncryptionUtil implements PasswordEncoder{
	// Spring Security가 기본적을 제공하는 암호화 기법 : SHA 기반
	private PasswordEncoder passwordEncoder;

	public EncryptionUtil() {
		this.passwordEncoder = new BCryptPasswordEncoder();
	}

	public EncryptionUtil(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	// Triple DES Encoding and Decoding + AES Encoding and Decoding
	// key값 리턴
	private String key(String hint) {
		// 24자리(24바이트)만 key 값으로 입력 가능
		char[] compareValue = ("k1cj4w3ib@9lhvsd!7x0aqtm#rg2y$6epu5zn8fo").toCharArray();
		char[] addRootKey = ("rauOhonSTHgom8812").toCharArray();

		String keyValue = "";
		char[] hintchar = hint.toCharArray();
		// 파라미터로 전달 받은 rootKey를 char[]에 저장
		char[] keyValueArray = new char[24];
		for(int i=0; i<hintchar.length;i++){
			keyValueArray[i] = hintchar[i];
		}
		 
		// rootKey의 값이 24bytes가 안될 경우 모자란 bytes만큼 임의의 값을 대입
		for(int i=0; i < 24 - hint.length(); i++){
			keyValueArray[hint.length()+i] = addRootKey[i]; 
		}
		
		// keyValueArray에 저장된 값을 keyValue의 값과 비교하여 일치하는 index값을 idx에 저장
		for(int i=0; i<keyValueArray.length; i++){
			for(int j=0; j<compareValue.length; j++){ 
				if (keyValueArray[i] == compareValue[j]){
					keyValue = compareValue[j] + keyValue;
					break;
				} 
			}
		}
		//System.out.println(keyValue);
		return keyValue;
	}

	/** Tripple DES
	 * 지정된 비밀키를 가지고 오는 메서드 (TripleDES) require Key Size : 24 bytes
	 * 임의의 key값으로 암호화와 복호화 가능 
	 */
	private Key getKey(String keyValue) throws Exception {
		//System.out.println(keyValue);
		DESedeKeySpec desKeySpec = new DESedeKeySpec(keyValue.getBytes());
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
		Key key = keyFactory.generateSecret(desKeySpec);
		return key;
	}

	// AES 256 
	private HashMap<String, Object> AES256Key(String hint) throws UnsupportedEncodingException {
		String iv;
	    Key key;
	    HashMap<String, Object> mapAES = new HashMap<String, Object>();
	    
        iv = hint.substring(0, 16);
        mapAES.put("iv", iv);
        
        byte[] keyBytes = new byte[16];
        byte[] b = hint.getBytes("UTF-8");
        int len = b.length;
        if (len > keyBytes.length) {
            len = keyBytes.length;
        }
        //System.arraycopy(b, 0, keyBytes, 0, len);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        
        key = keySpec;
        mapAES.put("key", key);
        return mapAES;
    }

	/* 
	 * Spring Security가 제공하는 SHA기반의 암호화 호출 메서드
	 * originPassword : Encryption이 적용되지 않은 암호
	 */
	@Override
	public String encode(CharSequence originPassword) {
		return passwordEncoder.encode(originPassword);
	}

	/* 
	 * 클라이언트에서 온 원본 비밀번호와 TripleDes 암호화된 String과 비교하여
	 * boolean을 리턴한다. 
	 */
	@Override
	public boolean matches(CharSequence originPassword, String encodedPassword) {
		return passwordEncoder.matches(originPassword, encodedPassword);
	}
	
	/**
	 * DES Encryption
	 * 
	 * @param data
	 *            비밀키 암호화를 희망하는 문자열
	 *  @param hint
	 *            암호화에 사용되어지는 keyValue문자열을 만드는데 사용할 사용자 지정 문자
	 * @return String 암호화된 DATA
	 * @exception Exception
	 */
	public String TripleDesEncoding(String data, String hint) throws Exception {
		if (data == null || data.length() == 0) {	return "";}

		String instance = (key(hint).length() == 24) ? "DESede/ECB/PKCS5Padding"
				: "DES/ECB/PKCS5Padding";
		javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(instance);
		cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, getKey(key(hint)));
		String amalgam = data;

		byte[] inputBytes1 = amalgam.getBytes("UTF8");
		byte[] outputBytes1 = cipher.doFinal(inputBytes1);

		Base64.encodeBase64(outputBytes1);

		String encryptionData = new String(Base64.encodeBase64(outputBytes1));
		return encryptionData;
	}

	/**
	 * DES Decryption
	 * 
	 * @param encryptionData
	 *            비밀키 복호화를 희망하는 문자열
	 * @param hint
	 *    		   복호화에 사용되어지는 keyValue문자열을 만드는데 사용할 사용자 지정 문자 
	 * @return String 복호화된 DATA
	 * @exception Exception
	 */
	public String TripleDesDecoding(String encryptionData, String hint) throws Exception {
		if (encryptionData == null || encryptionData.length() == 0)
			return "";

		String instance = (key(hint).length() == 24) ? "DESede/ECB/PKCS5Padding"
				: "DES/ECB/PKCS5Padding";
		javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(instance);
		cipher.init(javax.crypto.Cipher.DECRYPT_MODE, getKey(key(hint)));

		byte[] inputBytes1 = Base64.decodeBase64(encryptionData);
		byte[] outputBytes2 = cipher.doFinal(inputBytes1);

		String decryptionData = new String(outputBytes2, "UTF8");
		return decryptionData;
	}

	 // AES256 암호화
    public String aesEncode(String str, String hint) throws java.io.UnsupportedEncodingException, 
                                                    NoSuchAlgorithmException, 
                                                    NoSuchPaddingException, 
                                                    InvalidKeyException, 
                                                    InvalidAlgorithmParameterException, 
                                                    IllegalBlockSizeException, 
                                                    BadPaddingException {
    	
    	String keyValue = key(hint);
    	HashMap<String, Object> mapAES = AES256Key(keyValue);
    	
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, (Key)mapAES.get("key"), new IvParameterSpec(mapAES.get("iv").toString().getBytes()));
 
        byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
        String enStr = new String(Base64.encodeBase64(encrypted));
 
        return enStr;
    }
 
    //AES256 복호화
    public String aesDecode(String str, String hint) throws java.io.UnsupportedEncodingException,
                                                        NoSuchAlgorithmException,
                                                        NoSuchPaddingException, 
                                                        InvalidKeyException, 
                                                        InvalidAlgorithmParameterException,
                                                        IllegalBlockSizeException, 
                                                        BadPaddingException {
    	String keyValue = key(hint);
    	HashMap<String, Object> mapAES = AES256Key(keyValue);
    	
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, (Key)mapAES.get("key"), new IvParameterSpec(mapAES.get("iv").toString().getBytes("UTF-8")));
 
        byte[] byteStr = Base64.decodeBase64(str.getBytes());
 
        return new String(c.doFinal(byteStr),"UTF-8");
    }

}
