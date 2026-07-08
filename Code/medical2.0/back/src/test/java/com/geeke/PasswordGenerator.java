package com.geeke;

import org.apache.shiro.crypto.hash.Md5Hash;

/**
 * 密码哈希生成工具 — 在 IDE 中直接运行此 main 方法
 *
 * 用法：
 *   1. 修改下方 PASSWORD 和 USER_ID 两个常量
 *   2. 右键 Run PasswordGenerator.main()
 *   3. 复制输出的哈希值，执行 SQL: UPDATE sys_user SET login_password = '哈希值' WHERE id = '用户ID'
 */
public class PasswordGenerator {

    // ========== 在这里修改 ==========
    private static final String PASSWORD = "123456";   // 要设置的明文密码
    private static final String USER_ID  = "1000";     // 用户ID
    // =================================

    private static final int HASH_ITERATIONS = 10000;  // 与 UserRealm 保持一致

    public static void main(String[] args) {
        Md5Hash md5 = new Md5Hash(PASSWORD, USER_ID, HASH_ITERATIONS);
        String hashedPassword = md5.toHex();

        System.out.println("========================================");
        System.out.println("  密码哈希生成结果");
        System.out.println("========================================");
        System.out.println("  明文密码:   " + PASSWORD);
        System.out.println("  用户ID:     " + USER_ID);
        System.out.println("  迭代次数:   " + HASH_ITERATIONS);
        System.out.println("  哈希值:     " + hashedPassword);
        System.out.println("========================================");
        System.out.println();
        System.out.println("执行以下 SQL 重置密码:");
        System.out.println("  UPDATE sys_user SET login_password = '" + hashedPassword + "' WHERE id = '" + USER_ID + "';");
    }
}
