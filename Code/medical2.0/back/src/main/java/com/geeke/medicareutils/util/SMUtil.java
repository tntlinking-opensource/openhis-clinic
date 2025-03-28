package com.geeke.medicareutils.util;

import com.alibaba.fastjson.JSONObject;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SMUtil {

    /**
     * 加密
     *
     * @param data
     * @param appId
     * @param appSecret
     * @return
     */
    public static String encrypt(String data, String appId, String appSecret) {
        // 用 appId 加密 appSecret 获取新秘钥
        byte[] appSecretEncData = EasyGmUtils.sm4Encrypt(appId.substring(0, 16).getBytes(StandardCharsets.UTF_8), appSecret.getBytes(StandardCharsets.UTF_8));
        // 新秘钥串
        byte[] secKey = Hex.toHexString(appSecretEncData).toUpperCase().substring(0, 16).getBytes(StandardCharsets.UTF_8);
        // 加密数据
        return Hex.toHexString(EasyGmUtils.sm4Encrypt(secKey, data.getBytes(StandardCharsets.UTF_8))).toUpperCase();
    }

    /**
     * 解密
     *
     * @param data
     * @param appId
     * @param appSecret
     * @return
     */
    public static String decrypt(String data, String appId, String appSecret) {
        byte[] appSecretEncDataDecode = EasyGmUtils.sm4Encrypt(appId.substring(0, 16).getBytes(StandardCharsets.UTF_8), appSecret.getBytes(StandardCharsets.UTF_8));
        byte[] secKeyDecode = Hex.toHexString(appSecretEncDataDecode).toUpperCase().substring(0, 16).getBytes(StandardCharsets.UTF_8);
        return new String(EasyGmUtils.sm4Decrypt(secKeyDecode, Hex.decode(data)), StandardCharsets.UTF_8);
    }

    /**
     * 签名
     *
     * @param jsonObject
     * @param appSecret
     * @param privateKey
     * @return
     */
    public static String sign(JSONObject jsonObject, String appSecret, String privateKey) {
        // 获取签名串
        byte[] signText = SignUtil.getSignText(jsonObject, appSecret).getBytes(StandardCharsets.UTF_8);
        byte[] userId = appSecret.getBytes(StandardCharsets.UTF_8);
        byte[] prvkey = Base64.getDecoder().decode(privateKey);
        return Base64.getEncoder().encodeToString(EasyGmUtils.signSm3WithSm2(signText, userId, prvkey));
    }

    /**
     * 验签
     *
     * @param jsonObject
     * @param appSecret
     * @param publicKey
     * @param responseSign
     * @return
     */
    public static Boolean verify(JSONObject jsonObject, String appSecret, String publicKey, String responseSign) {
        // 验签
        byte[] msg = SignUtil.getSignText(jsonObject, appSecret).getBytes(StandardCharsets.UTF_8);
        byte[] userIdDecode = appSecret.getBytes(StandardCharsets.UTF_8);
        byte[] pubkey = Base64.getDecoder().decode(publicKey);
        byte[] signData = Base64.getDecoder().decode(responseSign);
        return EasyGmUtils.verifySm3WithSm2(msg, userIdDecode, signData, pubkey);
    }

    /**
     * 签名
     *
     * @param jsonString
     * @param appSecret
     * @param privateKey
     * @return
     */
    public static String sign(String jsonString, String appSecret, String privateKey) {
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        return sign(jsonObject, appSecret, privateKey);
    }

    /**
     * 验签
     *
     * @param jsonString
     * @param appSecret
     * @param publicKey
     * @param responseSign
     * @return
     */
    public static Boolean verify(String jsonString, String appSecret, String publicKey, String responseSign) {
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        return verify(jsonObject, appSecret, publicKey, responseSign);
    }
}
