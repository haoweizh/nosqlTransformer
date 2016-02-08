package com.qnvip.util;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import com.qnvip.util.api.ApiConfig;

public class RSA {

	// public static final String PUBLICKEY =
	// "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC5LymfEiQr52ytXpjRGK0gsBgFmxEztcveGRTNWqoD73Ugb7IfzGMSse4YMLUORFxbe9VNeLDOTA8nea4hcc/7cP1QxOv9ofNiSE16ydcOE2XhTQ1UIbi3ca/sVO7y6/cxyl+mIhk0+X70rc9oL+5TBntGf+G2Dx3xZ+cuDAJVkwIDAQAB";//公钥
	// public static final String PRIVATEKEY =
	// "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBALkvKZ8SJCvnbK1emNEYrSCwGAWbETO1y94ZFM1aqgPvdSBvsh/MYxKx7hgwtQ5EXFt71U14sM5MDyd5riFxz/tw/VDE6/2h82JITXrJ1w4TZeFNDVQhuLdxr+xU7vLr9zHKX6YiGTT5fvStz2gv7lMGe0Z/4bYPHfFn5y4MAlWTAgMBAAECgYEAkvQJPHFiAPEyfdXuwSeq46C6P2CR4w4mTQZsSpXjCyJwOSBKIw/HoyNNxSaJ11uKSxLW7xaSf/M9p17ZPzMZDp6w/ahYKjN72wBisgw28RZ4R/mG9gAUAv5432nDwrPRREjpSZvUuJY9NyV4tTln/xHneeLD7qOmPYW2vHMKFOECQQDmySGSieBp4KIVHPMGDnxHxM8wuKlsUZZT6/EwMggs++Lf/yvf72SIZ2dSTdvA4q2iak3mnyF9Rd/rxzW65pBjAkEAzWqZlVMc5BY0NI87A9L6mzrtr4z0+5OkdCjU/x/7sis1MKvJtymexRbMEg82VJEV35s6XGZ2d13v+67cqKz1EQJBAN2POFjbXwOmcVR9n14wJ+YqrpJipdxkk0JsH/eaALlW9J8A9VkeFVDSnKGqN498zx8+mR0PpnIC1A8iB5Yv5WsCQQDDNTKOvVUPTCqAaDzk/XI6Yl3OHl4RVdVKriL78CD4yCbcAeTHsz22flugLeSrqNToklN7y8N43ERbIWmAwhyBAkBaud2dPbBeOWmFOubE6I1kubxqeP6yPh9dDPBt0x3D7H2o+1RJAqMuv9+Cg8E5pI4Qfok0j+xV3CmcgSzb28Km";//私钥

	public static final String KEY_ALGORITHM = "RSA";
	public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

	public static void main(String[] args) throws Exception {
		System.err.println("公钥加密——私钥解密");
		String inputStr = "pay=aa&colse=111.11&cardNo=111&bank=asdfasdfads";

		String encodedData = encryptByPublicKey(inputStr, ApiConfig.PUBLIC_KEY);
		String outputStr = decryptByPrivateKey(encodedData, ApiConfig.PRIVATE_KEY);
		System.err.println("加密前: " + inputStr + "\n\r" + "解密后: " + outputStr);

		System.err.println("私钥加密——公钥解密");

		encodedData = encryptByPrivateKey(inputStr, ApiConfig.PRIVATE_KEY);
		outputStr = decryptByPublicKey(encodedData, ApiConfig.PUBLIC_KEY);

		System.err.println("加密前: " + inputStr + "\n\r" + "解密后: " + outputStr);

		System.err.println("私钥签名——公钥验证签名");
		// 产生签名
		String sign = sign(inputStr, ApiConfig.PRIVATE_KEY);
		System.err.println("签名:\r" + sign);

		// 验证签名
		boolean status = verify(inputStr, sign, ApiConfig.PUBLIC_KEY);
		System.err.println("状态:\r" + status);

	}

	/**
	 * 用私钥对信息生成数字签名
	 * 
	 * @param data 加密数据
	 * @param privateKey 私钥
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String sign(String data, String privateKey) throws Exception {
		// 解密由base64编码的私钥
		byte[] keyBytes = Coder.decryptBASE64(privateKey);

		// 构造PKCS8EncodedKeySpec对象
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);

		// KEY_ALGORITHM 指定的加密算法
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

		// 取私钥匙对象
		PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

		// 用私钥对信息生成数字签名
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(priKey);
		signature.update(data.getBytes());

		return Coder.encryptBASE64(signature.sign());
	}

	/**
	 * 校验数字签名
	 * 
	 * @param data 加密数据
	 * @param publicKey 公钥
	 * @param sign 数字签名
	 * 
	 * @return 校验成功返回true 失败返回false
	 * @throws Exception
	 * 
	 */
	public static boolean verify(String data, String sign, String publicKey) throws Exception {

		// 解密由base64编码的公钥
		byte[] keyBytes = Coder.decryptBASE64(publicKey);

		// 构造X509EncodedKeySpec对象
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

		// KEY_ALGORITHM 指定的加密算法
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

		// 取公钥匙对象
		PublicKey pubKey = keyFactory.generatePublic(keySpec);

		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initVerify(pubKey);
		signature.update(data.getBytes());

		// 验证签名是否正常
		return signature.verify(Coder.decryptBASE64(sign));
	}

	/**
	 * 解密<br>
	 * 用私钥解密
	 * 
	 * @param data
	 * @param KEY
	 * @return
	 * @throws Exception
	 */
	public static String decryptByPrivateKey(String data, String privateKey) throws Exception {
		// 对密钥解密
		byte[] keyBytes = Coder.decryptBASE64(privateKey);

		// 取得私钥
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key key = keyFactory.generatePrivate(pkcs8KeySpec);

		// 对数据解密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, key);

		return new String(cipher.doFinal(Coder.decryptBASE64(data)));
	}

	/**
	 * 解密<br>
	 * 用公钥解密
	 * 
	 * @param data
	 * @param KEY
	 * @return
	 * @throws Exception
	 */
	public static String decryptByPublicKey(String data, String publicKey) throws Exception {
		// 对密钥解密
		byte[] keyBytes = Coder.decryptBASE64(publicKey);

		// 取得公钥
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key key = keyFactory.generatePublic(x509KeySpec);

		// 对数据解密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, key);

		return new String(cipher.doFinal(Coder.decryptBASE64(data)));
	}

	/**
	 * 加密<br>
	 * 用公钥加密
	 * 
	 * @param data
	 * @param KEY
	 * @return
	 * @throws Exception
	 */
	public static String encryptByPublicKey(String data, String publicKey) throws Exception {
		// 对公钥解密
		byte[] keyBytes = Coder.decryptBASE64(publicKey);

		// 取得公钥
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key key = keyFactory.generatePublic(x509KeySpec);

		// 对数据加密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, key);

		return Coder.encryptBASE64(cipher.doFinal(data.getBytes()));
	}

	/**
	 * 加密<br>
	 * 用私钥加密
	 * 
	 * @param data
	 * @param KEY
	 * @return
	 * @throws Exception
	 */
	public static String encryptByPrivateKey(String data, String privateKey) throws Exception {
		// 对密钥解密
		byte[] keyBytes = Coder.decryptBASE64(privateKey);

		// 取得私钥
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key key = keyFactory.generatePrivate(pkcs8KeySpec);

		// 对数据加密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, key);

		return Coder.encryptBASE64(cipher.doFinal(data.getBytes()));
	}

	/**
	 * 初始化密钥
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private static void initKey() throws Exception {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		keyPairGen.initialize(1024);

		KeyPair keyPair = keyPairGen.generateKeyPair();

		// 公钥
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		System.out.println("pubilcKey");
		System.out.println(Coder.encryptBASE64(publicKey.getEncoded()));
		// 私钥
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		System.out.println("privateKey");
		System.out.println(Coder.encryptBASE64(privateKey.getEncoded()));
	}
}
