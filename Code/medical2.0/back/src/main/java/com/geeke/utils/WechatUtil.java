package com.geeke.utils;



import com.alibaba.fastjson.JSONObject;
// vo实体类参数

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.util.*;

@Slf4j(topic = "WechatUtils")
@Component
public class WechatUtil {
	private static final String appId = "wx3558376f22f9954d";
	private static final String secret = "da49a1076e9a418f055aaf9101e4b2e9";

	/**
	 * 获取小程序code换取openid、session_key
	 *
	 * @param code
	 * @return
	 */
	public static JSONObject getOpenId(String code) {

		String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId
				+ "&secret=" + secret + "&js_code=" + code + "&grant_type=authorization_code";
		PrintWriter out = null;
		BufferedReader in = null;
		String line;
		StringBuffer stringBuffer = new StringBuffer();
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();

			// 设置通用的请求属性 设置请求格式
			//设置返回类型
			conn.setRequestProperty("contentType", "text/plain");
			//设置请求类型
			conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
			//设置超时时间
			conn.setConnectTimeout(1000);
			conn.setReadTimeout(1000);
			conn.setDoOutput(true);
			conn.connect();
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应    设置接收格式
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream(), "UTF-8"));
			while ((line = in.readLine()) != null) {
				stringBuffer.append(line);
			}
			JSONObject jsonObject = JSONObject.parseObject(stringBuffer.toString());
			return jsonObject;

		} catch (Exception e) {
			e.printStackTrace();
		}
		//使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	public static JSONObject getToken() {
		String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appId+"&secret="+secret;
		PrintWriter out = null;
		BufferedReader in = null;
		String line;
		StringBuffer stringBuffer = new StringBuffer();
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();

			// 设置通用的请求属性 设置请求格式
			//设置返回类型
			conn.setRequestProperty("contentType", "text/plain");
			//设置请求类型
			conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
			//设置超时时间
			conn.setConnectTimeout(1000);
			conn.setReadTimeout(1000);
			conn.setDoOutput(true);
			conn.connect();
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应    设置接收格式
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream(), "UTF-8"));
			while ((line = in.readLine()) != null) {
				stringBuffer.append(line);
			}
			JSONObject jsonObject = JSONObject.parseObject(stringBuffer.toString());
			return jsonObject;

		} catch (Exception e) {
			e.printStackTrace();
		}
		//使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}


	//获取手机号
	public static JSONObject getPhoneNumber(String code,String token){
		String url = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token="+token;
		PrintWriter out = null;
		BufferedReader in = null;
		String line;
		StringBuffer stringBuffer = new StringBuffer();
		RestTemplate restTemplate = new RestTemplate();
		Map<String, Object> data = new HashMap<>();
		data.put("code", code);
		String response = restTemplate.postForObject(url, data, String.class);
		//System.out.println(response);
		JSONObject jsonObject = JSONObject.parseObject(response);
		return jsonObject;
	}
}

//public static Map<String, Object> getPhoneNumber(TestEntityVO vo) {
//		Map<String,Object> map=new HashMap<>();
//		String openid= vo.getWechatOpenId();
//		String session_key = vo.getSessionKey();
//		if (!EmptyUtils.isEmpty(openid)) {
//
//			if(EmptyUtils.isEmpty(session_key)){
//				return null;
//			}
//			map.put("openid",openid);
//			// 被加密的数据
//			byte[] dataByte = Base64.decode(vo.getEncryptedData());
//			// 加密秘钥
//			byte[] keyByte = Base64.decode(session_key);
//			// 偏移量
//			byte[] ivByte = Base64.decode(vo.getIv());
//			try {
//				// 如果密钥不足16位，那么就补足. 这个if 中的内容很重要
//				int base = 16;
//				String result = null;
//				if (keyByte.length % base != 0) {
//					int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
//					byte[] temp = new byte[groups * base];
//					Arrays.fill(temp, (byte) 0);
//					System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
//					keyByte = temp;
//				}
//				// 初始化
//				Security.addProvider(new BouncyCastleProvider());
//				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
//				SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
//				AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
//				parameters.init(new IvParameterSpec(ivByte));
//				// 初始化
//				cipher.init(Cipher.DECRYPT_MODE, spec, parameters);
//				byte[] resultByte = cipher.doFinal(dataByte);
//				if (null != resultByte && resultByte.length > 0) {
//					result = new String(resultByte, "UTF-8");
//					JSONObject jsonObject = JSONObject.parseObject(result);
//					map.put("param",jsonObject);
//					return map;
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		return null;
//	}
