package service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码加密校验工具类
 * 采用 PBKDF2WithHmacSHA256 算法加盐哈希存储密码，防止彩虹表、暴力破解
 * 存储格式：pbkdf2$迭代次数$Base64盐值$Base64哈希结果
 */
public final class PasswordService {
    // 安全随机数实例，用于生成随机盐
    private static final SecureRandom RANDOM = new SecureRandom();
    // PBKDF2 迭代运算次数，次数越高破解成本越大
    private static final int ITERATIONS = 120_000;
    // 生成密钥长度 256bit
    private static final int KEY_LENGTH = 256;

    /**
     * 私有构造，禁止外部实例化工具类
     */
    private PasswordService() {
    }

    /**
     * 对明文密码进行 PBKDF2 加盐哈希加密
     * @param password 原始明文密码
     * @return 加密字符串，格式：pbkdf2$迭代次数$Base64盐$Base64哈希值
     */
    public static String hash(String password) {
        // 生成16字节随机盐
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        // 拼接算法标识、迭代次数、盐、哈希结果，全部Base64编码方便字符串存储
        return "pbkdf2$" + ITERATIONS + "$" + Base64.getEncoder().encodeToString(salt)
                + "$" + Base64.getEncoder().encodeToString(derive(password, salt, ITERATIONS));
    }

    /**
     * 校验输入明文密码是否和数据库存储的加密密码匹配
     * 兼容旧明文密码兜底逻辑：非pbkdf2格式则直接字节比对
     * @param password 用户输入的明文密码
     * @param stored 数据库存储的加密密码串
     * @return 匹配返回true，不匹配/格式异常返回false
     */
    public static boolean matches(String password, String stored) {
        // 存储密码为空直接校验失败
        if (stored == null) {
            return false;
        }
        // 如果不是PBKDF2加密格式，降级原始字节比对（兼容历史明文数据）
        if (!stored.startsWith("pbkdf2$")) {
            return MessageDigest.isEqual(password.getBytes(), stored.getBytes());
        }
        // 按分隔符$拆分四段：[标识,迭代次数,盐,哈希值]
        String[] parts = stored.split("\\$");
        if (parts.length != 4) {
            return false;
        }
        try {
            // 解析迭代次数
            int iterations = Integer.parseInt(parts[1]);
            // Base64解码获取原始盐字节数组
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            // Base64解码获取数据库中正确哈希值
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            // 使用相同盐、迭代次数重新计算密码哈希，使用常量时间比对防止时序攻击
            return MessageDigest.isEqual(expected, derive(password, salt, iterations));
        } catch (RuntimeException e) {
            // 解析异常（数字格式错误、Base64非法等）直接返回校验失败
            return false;
        }
    }

    /**
     * 内部核心方法：使用PBKDF2WithHmacSHA256计算密码哈希
     * @param password 明文密码
     * @param salt 随机盐字节数组
     * @param iterations 迭代次数
     * @return 哈希摘要字节数组
     */
    private static byte[] derive(String password, byte[] salt, int iterations) {
        try {
            // 构造PBKDF2参数：密码字符数组、盐、迭代次数、输出密钥长度
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, KEY_LENGTH);
            // 创建PBKDF2工厂生成密钥，并返回原始字节
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            // 算法不存在、参数非法等抛出运行时异常
            throw new IllegalStateException("无法生成密码摘要", e);
        }
    }
}