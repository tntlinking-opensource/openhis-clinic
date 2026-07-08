package com.geeke;

import java.security.MessageDigest;

/**
 * Password reset utility - run directly in IDE when needed.
 *
 * Usage:
 *   1. Uncomment the desired method in main()
 *   2. Run in IDE
 *   3. Copy the generated SQL and execute in database
 *
 * Shiro SimpleHash algorithm (verified against Shiro 1.10.1):
 *   iteration 1: md.update(salt); md.digest(source)
 *   iteration N: md.digest(raw_bytes_of_previous)
 */
public class BatchPasswordReset {

    private static final int ITERATIONS = 10000;

    public static void main(String[] args) throws Exception {
        // Default: reset all accounts to 123456
        resetAllTo123456();

        // Demo environment passwords (uncomment when deploying to demo)
        // resetDemoPasswords();
    }

    /**
     * Reset all user passwords to "123456"
     */
    private static void resetAllTo123456() throws Exception {
        String[] userIds = {
            "1000", "1001",
            "2077468568630583305", "2077468568630583314", "2077468568630584865",
            "2084869364217815460", "2097952229740306855", "2099436828135891046",
            "2304283214555807744", "2521116562640420866", "2521318838420217857",
            "2521318838420217867", "2688809008048635909", "2920702143560057180",
            "2920702143560057449", "2981473611516363384"
        };
        printSql(userIds, "123456");
    }

    /**
     * Reset demo environment accounts to specific passwords:
     *   super   / xzsuper123
     *   system  / xzsystem123
     *   xzadmin / 2WSXcde!@
     */
    private static void resetDemoPasswords() throws Exception {
        System.out.println("BEGIN;");
        printOne("1000", "super", "xzsuper123");
        printOne("1001", "system", "xzsystem123");
        printOne("2077468568630583314", "xzadmin", "2WSXcde!@");
        System.out.println("COMMIT;");
    }

    private static void printSql(String[] userIds, String password) throws Exception {
        System.out.println("BEGIN;");
        for (String userId : userIds) {
            String hash = shiroMd5Hash(password, userId, ITERATIONS);
            System.out.println("UPDATE sys_user SET login_password = '" + hash + "' WHERE id = '" + userId + "';");
        }
        System.out.println("COMMIT;");
    }

    private static void printOne(String userId, String loginName, String password) throws Exception {
        String hash = shiroMd5Hash(password, userId, ITERATIONS);
        System.out.println("UPDATE sys_user SET login_password = '" + hash + "' WHERE id = '" + userId + "';  -- " + loginName + " / " + password);
    }

    private static String shiroMd5Hash(String password, String salt, int iterations) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(salt.getBytes("UTF-8"));
        byte[] digest = md.digest(password.getBytes("UTF-8"));
        for (int i = 1; i < iterations; i++) {
            digest = md.digest(digest);
        }
        return bytesToHex(digest);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}
