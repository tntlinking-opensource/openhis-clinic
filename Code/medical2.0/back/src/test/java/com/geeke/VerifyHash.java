import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import java.security.MessageDigest;

public class VerifyHash {
    public static void main(String[] args) throws Exception {
        String password = "123456";
        String userId = "1000";
        int iterations = 10000;

        // Shiro result
        Md5Hash md5 = new Md5Hash(password, userId, iterations);
        System.out.println("Shiro Md5Hash result:  " + md5.toHex());

        // Manual implementation - try different salt handling approaches

        // Approach A: hash(password + salt) as first iteration
        String hashA = manualHash(password + userId, iterations);
        System.out.println("Approach A (pwd+salt): " + hashA);

        // Approach B: hash(password) first, then hash with salt each iteration
        String hashB = manualHashWithSalt(password, userId, iterations);
        System.out.println("Approach B (salt-each): " + hashB);

        // Approach C: Shiro might hash source first, then mix salt
        // Let's try 1 iteration to see what Shiro produces
        Md5Hash md5_1 = new Md5Hash(password, userId, 1);
        System.out.println("\n--- 1 iteration debug ---");
        System.out.println("Shiro 1-iter: " + md5_1.toHex());
        System.out.println("Manual A 1-iter: " + md5Hex(password + userId));
        System.out.println("Manual pwd-only 1-iter: " + md5Hex(password));
        System.out.println("Manual salt-only 1-iter: " + md5Hex(userId));

        // Approach D: maybe salt is applied as bytes differently
        byte[] pwdBytes = password.getBytes("UTF-8");
        byte[] saltBytes = userId.getBytes("UTF-8");
        byte[] combined = new byte[pwdBytes.length + saltBytes.length];
        System.arraycopy(pwdBytes, 0, combined, 0, pwdBytes.length);
        System.arraycopy(saltBytes, 0, combined, pwdBytes.length, saltBytes.length);
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(combined);
        System.out.println("Manual bytes concat: " + bytesToHex(digest));
    }

    static String manualHash(String firstInput, int iterations) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(firstInput.getBytes("UTF-8"));
        for (int i = 1; i < iterations; i++) {
            digest = md.digest(bytesToHex(digest).getBytes("UTF-8"));
        }
        return bytesToHex(digest);
    }

    static String manualHashWithSalt(String password, String salt, int iterations) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest((password + salt).getBytes("UTF-8"));
        for (int i = 1; i < iterations; i++) {
            digest = md.digest((bytesToHex(digest) + salt).getBytes("UTF-8"));
        }
        return bytesToHex(digest);
    }

    static String md5Hex(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        return bytesToHex(md.digest(input.getBytes("UTF-8")));
    }

    static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}
