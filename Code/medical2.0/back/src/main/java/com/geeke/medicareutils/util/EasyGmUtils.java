package com.geeke.medicareutils.util;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.engines.SM4Engine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;

/**
 * @Description  TODO
 * @Author Hzx
 * @Date 2024/10/31
 */
public class EasyGmUtils {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final int RS_LEN = 32; // Adjust this based on the required length
    private static final X9ECParameters x9ECParameters = GMNamedCurves.getByName("sm2p256v1");
    private static final ECDomainParameters ecDomainParameters = new ECDomainParameters(x9ECParameters.getCurve(), x9ECParameters.getG(), x9ECParameters.getN());


    public static byte[] signSm3WithSm2(byte[] msg, byte[] userId, byte[] privateKeyBytes) {
        ECPrivateKeyParameters privateKeyParameters = getPrivateKeyFromD(new BigInteger(1, privateKeyBytes));
        return rsAsn1ToPlainByteArray(signSm3WithSm2Asn1Rs(msg, userId, privateKeyParameters));
    }



    public static byte[] signSm3WithSm2Asn1Rs(byte[] msg, byte[] userId, AsymmetricKeyParameter privateKey) {
        try {
            // 创建 SM3 摘要
            SM3Digest digest = new SM3Digest();
            // 创建 SM2 签名器
            Signer signer = new SM2Signer();

            // 初始化签名器
            signer.init(true, new ParametersWithRandom(privateKey, new SecureRandom()));

            // 更新签名内容
            signer.update(msg, 0, msg.length);

            // 生成签名
            return signer.generateSignature();
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常信息
            return null;
        }
    }

    public static boolean verifySm3WithSm2(byte[] msg, byte[] userId, byte[] rs, byte[] publicKeyBytes) {
        if (rs == null || msg == null || userId == null) return false;
        if (rs.length != RS_LEN * 2) return false;

        if (publicKeyBytes.length != 64 && publicKeyBytes.length != 65) throw new IllegalArgumentException("Error key length");
        BigInteger x, y;
        if (publicKeyBytes.length > 64) {
            x = fromUnsignedByteArray(publicKeyBytes, 1, 32);
            y = fromUnsignedByteArray(publicKeyBytes, 33, 32);
        } else {
            x = fromUnsignedByteArray(publicKeyBytes, 0, 32);
            y = fromUnsignedByteArray(publicKeyBytes, 32, 32);
        }
        ECPublicKeyParameters publicKey = getPublicKeyFromXY(x, y);
        return verifySm3WithSm2Asn1Rs(msg, userId, rsPlainByteArrayToAsn1(rs), publicKey);
    }

    public static BigInteger fromUnsignedByteArray(byte[] array, int offset, int length) {
        byte[] subArray = array;
        if (offset != 0 || length != array.length) {
            subArray = new byte[length];
            System.arraycopy(array, offset, subArray, 0, length);
        }
        return new BigInteger(1, subArray);
    }

    public static boolean verifySm3WithSm2Asn1Rs(byte[] msg, byte[] userId, byte[] sign, AsymmetricKeyParameter publicKey) {
        try {
            // 初始化 SM2 签名器
            SM2Signer signer = new SM2Signer();
            signer.init(false, publicKey); // false 表示初始化为验证模式
            signer.update(msg, 0, msg.length); // 更新消息

            // 验证签名
            return signer.verifySignature(sign);
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常信息
            return false; // 验证失败
        }
    }

    private static byte[] changeC1C2C3ToC1C3C2(byte[] c1c2c3) {
        int c1Len = (x9ECParameters.getCurve().getFieldSize() + 7) / 8 * 2 + 1;
        final int c3Len = 32; // SM3 digest size
        byte[] result = new byte[c1c2c3.length];
        System.arraycopy(c1c2c3, 0, result, 0, c1Len); // c1
        System.arraycopy(c1c2c3, c1c2c3.length - c3Len, result, c1Len, c3Len); // c3
        System.arraycopy(c1c2c3, c1Len, result, c1Len + c3Len, c1c2c3.length - c1Len - c3Len); // c2
        return result;
    }

    private static byte[] changeC1C3C2ToC1C2C3(byte[] c1c3c2) {
        int c1Len = (x9ECParameters.getCurve().getFieldSize() + 7) / 8 * 2 + 1;
        final int c3Len = 32; // SM3 digest size
        byte[] result = new byte[c1c3c2.length];
        System.arraycopy(c1c3c2, 0, result, 0, c1Len); // c1
        System.arraycopy(c1c3c2, c1Len + c3Len, result, c1Len, c1c3c2.length - c1Len - c3Len); // c2
        System.arraycopy(c1c3c2, c1Len, result, c1c3c2.length - c3Len, c3Len); // c3
        return result;
    }

    public static byte[] sm2Decrypt(byte[] data, AsymmetricKeyParameter key) {
        return sm2DecryptOld(changeC1C3C2ToC1C2C3(data), key);
    }

    public static byte[] sm2Encrypt(byte[] data, AsymmetricKeyParameter key) {
        return changeC1C2C3ToC1C3C2(sm2EncryptOld(data, key));
    }

    public static byte[] sm2EncryptOld(byte[] data, AsymmetricKeyParameter pubkey) {
        try {
            SM2Engine sm2Engine = new SM2Engine();
            sm2Engine.init(true, new ParametersWithRandom(pubkey, new SecureRandom()));
            return sm2Engine.processBlock(data, 0, data.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] sm2DecryptOld(byte[] data, AsymmetricKeyParameter key) {
        try {
            SM2Engine sm2Engine = new SM2Engine();
            sm2Engine.init(false, key);
            return sm2Engine.processBlock(data, 0, data.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

//    public static byte[] sm3(byte[] bytes) {
//        try {
//            SM3Digest digest = new SM3Digest();
//            digest.blockUpdate(bytes, 0, bytes.length);
//            byte[] result = new byte[digest.getDigestSize()];
//            digest.doFinal(result, 0);
//            return result;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
    // SM4 解密 (CBC)
    public static byte[] sm4DecryptCBC(byte[] keyBytes, byte[] cipher, byte[] iv) {
        if (keyBytes.length != 16) throw new IllegalArgumentException("err key length");
        if (cipher.length % 16 != 0) throw new IllegalArgumentException("err data length");

        try {
            PaddedBufferedBlockCipher cipherInstance = new PaddedBufferedBlockCipher(new CBCBlockCipher(new SM4Engine()));
            KeyParameter key = new KeyParameter(keyBytes);
            if (iv == null) iv = zeroIv("SM4");
            cipherInstance.init(false, new ParametersWithIV(key, iv));
            byte[] output = new byte[cipherInstance.getOutputSize(cipher.length)];
            int length = cipherInstance.processBytes(cipher, 0, cipher.length, output, 0);
            length += cipherInstance.doFinal(output, length);
            return Arrays.copyOf(output, length);
        } catch (CryptoException e) {
            // Log the error (use your logging framework)
            return null;
        }
    }

    // SM4 加密 (CBC)
    public static byte[] sm4EncryptCBC(byte[] keyBytes, byte[] plain, byte[] iv) {
        if (keyBytes.length != 16) throw new IllegalArgumentException("err key length");
        if (plain.length % 16 != 0) throw new IllegalArgumentException("err data length");

        try {
            PaddedBufferedBlockCipher cipherInstance = new PaddedBufferedBlockCipher(new CBCBlockCipher(new SM4Engine()));
            KeyParameter key = new KeyParameter(keyBytes);
            if (iv == null) iv = zeroIv("SM4");
            cipherInstance.init(true, new ParametersWithIV(key, iv));
            byte[] output = new byte[cipherInstance.getOutputSize(plain.length)];
            int length = cipherInstance.processBytes(plain, 0, plain.length, output, 0);
            length += cipherInstance.doFinal(output, length);
            return Arrays.copyOf(output, length);
        } catch (CryptoException e) {
            // Log the error (use your logging framework)
            return null;
        }
    }

    // SM4 加密 (ECB)
    public static byte[] sm4EncryptECB(byte[] keyBytes, byte[] plain) {
        if (keyBytes.length != 16) throw new IllegalArgumentException("err key length");
        if (plain.length % 16 != 0) throw new IllegalArgumentException("err data length");

        try {
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "SM4");
            Cipher cipher = Cipher.getInstance("SM4/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            return cipher.doFinal(plain);
        } catch (Exception e) {
            // Log the error (use your logging framework)
            return null;
        }
    }
    // SM4 解密 (ECB)
    public static byte[] sm4DecryptECB(byte[] keyBytes, byte[] cipher) {
        if (keyBytes.length != 16) throw new IllegalArgumentException("err key length");
        if (cipher.length % 16 != 0) throw new IllegalArgumentException("err data length");

        try {
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "SM4");
            Cipher cipher1 = Cipher.getInstance("SM4/ECB/PKCS5Padding");
            cipher1.init(Cipher.DECRYPT_MODE, keySpec);
            return cipher1.doFinal(cipher);
        } catch (Exception e) {
            // Log the error (use your logging framework)
            return null;
        }
    }
    public static ECPrivateKeyParameters getPrivateKeyFromD(BigInteger d) {
        return new ECPrivateKeyParameters(d, ecDomainParameters);
    }

    public static ECPublicKeyParameters getPublicKeyFromXY(BigInteger x, BigInteger y) {
        return new ECPublicKeyParameters(x9ECParameters.getCurve().createPoint(x, y), ecDomainParameters);
    }
    public static byte[] sm4Encrypt(byte[] keyBytes, byte[] plain) {
        if (keyBytes.length != 16) {
            throw new IllegalArgumentException("err key length");
        }

        try {
            KeyParameter key = new KeyParameter(keyBytes);
            PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new SM4Engine());
            cipher.init(true, key);

            byte[] output = new byte[cipher.getOutputSize(plain.length)];
            int len = cipher.processBytes(plain, 0, plain.length, output, 0);
            len += cipher.doFinal(output, len);
            byte[] finalOutput = new byte[len];
            System.arraycopy(output, 0, finalOutput, 0, len);
            return finalOutput;
        } catch (CryptoException e) {
            throw new RuntimeException("Encryption error", e);
        }
    }

    public static byte[] sm4Decrypt(byte[] keyBytes, byte[] cipher) {
        if (keyBytes.length != 16) {
            throw new IllegalArgumentException("err key length");
        }
        if (cipher.length % 16 != 0) {
            throw new IllegalArgumentException("err data length");
        }

        try {
            KeyParameter key = new KeyParameter(keyBytes);
            PaddedBufferedBlockCipher cipher1 = new PaddedBufferedBlockCipher(new SM4Engine());
            cipher1.init(false, key);

            byte[] output = new byte[cipher1.getOutputSize(cipher.length)];
            int len = cipher1.processBytes(cipher, 0, cipher.length, output, 0);
            len += cipher1.doFinal(output, len);
            byte[] finalOutput = new byte[len];
            System.arraycopy(output, 0, finalOutput, 0, len);
            return finalOutput;
        } catch (CryptoException e) {
            throw new RuntimeException("Decryption error", e);
        }
    }

























        private static byte[] bigIntToFixedLengthBytes(BigInteger rOrS) {
        // For sm2p256v1, n is 00fffffffeffffffffffffffffffffffff7203df6b21c6052b53bbf40939d54123,
        // r and s are the result of mod n, so they should be less than n and have length <= 32
        byte[] rs = rOrS.toByteArray();
        if (rs.length == RS_LEN) {
            return rs;
        } else if (rs.length == RS_LEN + 1 && rs[0] == 0) {
            return Arrays.copyOfRange(rs, 1, RS_LEN + 1);
        } else if (rs.length < RS_LEN) {
            byte[] result = new byte[RS_LEN];
            Arrays.fill(result, (byte) 0);
            System.arraycopy(rs, 0, result, RS_LEN - rs.length, rs.length);
            return result;
        } else {
            throw new IllegalArgumentException("Error rs: " + Arrays.toString(rs));
        }
    }
    /**
     * Converts ASN.1 format rs to a plain byte array (concatenated r || s).
     *
     * @param rsDer rs in ASN.1 format
     * @return sign result in plain byte array
     */
    private static byte[] rsAsn1ToPlainByteArray(byte[] rsDer) {
        ASN1Sequence seq = ASN1Sequence.getInstance(rsDer);
        byte[] r = bigIntToFixedLengthBytes(ASN1Integer.getInstance(seq.getObjectAt(0)).getValue());
        byte[] s = bigIntToFixedLengthBytes(ASN1Integer.getInstance(seq.getObjectAt(1)).getValue());
        byte[] result = new byte[RS_LEN * 2];
        System.arraycopy(r, 0, result, 0, r.length);
        System.arraycopy(s, 0, result, RS_LEN, s.length);
        return result;
    }

    /**
     * Converts a plain byte array (concatenated r || s) to ASN.1 format.
     *
     * @param sign in plain byte array
     * @return rs result in ASN.1 format
     */
    private static byte[] rsPlainByteArrayToAsn1(byte[] sign) {
        if (sign.length != RS_LEN * 2) throw new IllegalArgumentException("Error rs.");
        BigInteger r = new BigInteger(1, Arrays.copyOfRange(sign, 0, RS_LEN));
        BigInteger s = new BigInteger(1, Arrays.copyOfRange(sign, RS_LEN, RS_LEN * 2));
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(r));
        v.add(new ASN1Integer(s));
        try {
            return new DERSequence(v).getEncoded();
        } catch (IOException e) {
            // Handle error accordingly
            e.printStackTrace();
            return null;
        }
    }
    public final static  String SM4_ECB_NOPADDING = "SM4/ECB/NoPadding";
    public final   String SM4_CBC_NOPADDING = "SM4/CBC/NoPadding";
    public final   String SM4_CBC_PKCS7PADDING = "SM4/CBC/PKCS7Padding";

    public static byte[] zeroIv(String algo) {
        try {
            // 创建适当的加密器，根据算法名称选择相应的引擎
            PaddedBufferedBlockCipher cipher;
            if (algo.equals("AES/ECB/PKCS7Padding")) {
                cipher = new PaddedBufferedBlockCipher(new AESEngine());
                // 添加其他算法支持，如DES, SM4等
            } else {
                throw new IllegalArgumentException("Unsupported algorithm: " + algo);
            }
            // 获取块大小并创建零IV
            int blockSize = cipher.getBlockSize();
            byte[] iv = new byte[blockSize];
            Arrays.fill(iv, (byte) 0);
            return iv;
        } catch (Exception e) {
            // 处理异常，例如记录错误
            e.printStackTrace(); // 可以替换为日志记录
            return null;
        }
    }

}

