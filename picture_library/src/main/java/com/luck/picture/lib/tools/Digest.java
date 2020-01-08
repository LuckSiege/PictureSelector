package com.luck.picture.lib.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * @author：luck
 * @date：2019-12-30 15:08
 * @describe：Digest
 */
public class Digest {
    /**
     * 获取文件MD5值
     *
     * @param file
     * @return
     */
    public static String computeMD5(File file) {
        return compute(file, "MD5");
    }

    /**
     * 获取文件MD5值
     *
     * @param inputStream
     * @return
     */
    public static String computeToQMD5(FileInputStream inputStream) {
        return computeToQ(inputStream, "MD5");
    }

    private static String computeToQ(FileInputStream fis, String type) {

        try {
            MessageDigest messageDigest = MessageDigest.getInstance(type);
            byte[] buffer = new byte[1024 * 1024];
            for (int bytesRead; (bytesRead = fis.read(buffer)) != -1; ) {
                messageDigest.update(buffer, 0, bytesRead);
            }

            return bytesToHex(messageDigest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取文件MD5值
     *
     * @param inputStream
     * @return
     */
    public static String computeToQMD5(InputStream inputStream) {
        return computeToQ(inputStream, "MD5");
    }

    private static String computeToQ(InputStream fis, String type) {

        try {
            MessageDigest messageDigest = MessageDigest.getInstance(type);
            byte[] buffer = new byte[1024 * 1024];
            for (int bytesRead; (bytesRead = fis.read(buffer)) != -1; ) {
                messageDigest.update(buffer, 0, bytesRead);
            }

            return bytesToHex(messageDigest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static String compute(File file, String type) {
        FileInputStream fis = null;

        try {
            MessageDigest messageDigest = MessageDigest.getInstance(type);
            fis = new FileInputStream(file);
            byte[] buffer = new byte[1024 * 1024];

            for (int bytesRead = 0; (bytesRead = fis.read(buffer)) != -1; ) {
                messageDigest.update(buffer, 0, bytesRead);
            }

            return bytesToHex(messageDigest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        for (byte b : bytes) {
            sb.append(byteToHex(b));
        }

        return sb.toString();
    }

    private static String byteToHex(byte b) {
        String hex = Integer.toHexString(0xFF & b | 0x00);
        return b >= 0 && b <= 15 ? '0' + hex : hex;
    }
}
