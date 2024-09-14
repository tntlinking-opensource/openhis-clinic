package com.geeke.utils;

import java.security.SecureRandom;

import com.baidu.fsg.uid.UidGenerator;

/**
 * 封装各种生成唯一性ID算法的工具类.
 * @author lys
 * @version 2018-12-27
 */

public class IdGen {

	private static SecureRandom random = new SecureRandom();

	private static UidGenerator uidGenerator;

	/**
	 * 封装JDK自带的UUID, 通过Random数字生成, 中间无-分割.
	 */
	public static String uuid() {
		// return UUID.randomUUID().toString().replaceAll("-", "");
		return Long.toString(uidGenerator.getUID());
	}
	
	/**
	 * 使用SecureRandom随机生成Long. 
	 */
	public static long randomLong() {
		return Math.abs(random.nextLong());
	}

	/**
	 * 基于Base62编码的SecureRandom随机生成bytes.
	 */
	public static String randomBase62(int length) {
		byte[] randomBytes = new byte[length];
		random.nextBytes(randomBytes);
		return Encodes.encodeBase62(randomBytes);
	}

	public static void  setUidGenerator(UidGenerator generator) {
		uidGenerator = generator;
	}
}
