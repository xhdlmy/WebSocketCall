package com.xhd.base.util;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;

/**
 * 1 File 信息获取
 * 2 File IO操作封装
 */
public class FileUtils {

    private FileUtils(){}

    /**
     * getBytes 获取文件的字节流
     * @return 如果 IOException，返回空字节数组
     */
    public static byte[] getBytes(File file){
        byte[] buffer = new byte[0];
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    public static byte[] getBytes(String absolutePath){
        return getBytes(new File(absolutePath));
    }

    /*===================================File String 文本 序列化/反序列化==================================*/

    // 读取文本文件
    public static String read(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        if(FileUtils.createFile(file)){
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s;
            while((s = br.readLine()) != null){
                sb.append(s).append("\n");
            }
            br.close();
        }
        return sb.toString();
    }

    // 写入文本文件：自动创建，是否追加
    public static void write(File file, String s, boolean isAppend) throws IOException {
        if(FileUtils.createFile(file)){
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, isAppend));
            bw.write(s);
            bw.newLine();
            bw.flush();
            bw.close();
        }
    }

    /*===================================File 信息获取===================================*/

    public static String getDirSize(String dirPath) {
        return getDirSize(new File(dirPath));
    }

    public static String getDirSize(File dir) {
        long len = getDirLength(dir);
        return len == -1 ? "" : byte2FitMemorySize(len);
    }

    public static String getFileSize(String absolutePath) {
        return getFileSize(new File(absolutePath));
    }

    public static String getFileSize(File file) {
        long len = getFileLength(file);
        return len == -1 ? "" : byte2FitMemorySize(len);
    }

    public static long getFileLength(String absolutePath) {
        return getFileLength(new File(absolutePath));
    }

    public static long getFileLength(File file) {
        if (!isFile(file)) return -1;
        return file.length();
    }

    public static long getDirLength(String dirPath) {
        return getDirLength(new File(dirPath));
    }

    public static long getDirLength(File dir) {
        if (!isDirectory(dir)) return -1;
        long len = 0;
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (file.isDirectory()) {
                    len += getDirLength(file);
                } else {
                    len += file.length();
                }
            }
        }
        return len;
    }

    /**
     * 获取文件的MD5校验码
     */
    public static byte[] getFileMD5(File file) throws Exception {
        if (file == null) return null;
        MessageDigest md = MessageDigest.getInstance("MD5");
        FileInputStream fis = new FileInputStream(file);
        DigestInputStream dis = new DigestInputStream(fis, md);
        byte[] buffer = new byte[1024 * 256];
        while (true) {
            if (!(dis.read(buffer) > 0)) break;
        }
        md = dis.getMessageDigest();
        dis.close();
        return md.digest();
    }

    public static byte[] getFileMD5(String absolutePath) throws Exception {
        return getFileMD5(new File(absolutePath));
    }

    public static String getFileMD5ToString(File file) throws Exception {
        return bytes2HexString(getFileMD5(file));
    }

    public static String getFileMD5ToString(String absolutePath) throws Exception {
        File file = TextUtils.isEmpty(absolutePath) ? null : new File(absolutePath);
        return getFileMD5ToString(file);
    }

    /**
     * 简单获取文件编码格式
     */
    public static String getFileCharsetSimple(String absolutePath) throws IOException {
        return getFileCharsetSimple(new File(absolutePath));
    }

    public static String getFileCharsetSimple(File file) throws IOException {
        int p = 0;
        InputStream is = null;
        is = new BufferedInputStream(new FileInputStream(file));
        p = (is.read() << 8) + is.read();
        is.close();
        switch (p) {
            case 0xefbb:
                return "UTF-8";
            case 0xfffe:
                return "Unicode";
            case 0xfeff:
                return "UTF-16BE";
            default:
                return "GBK";
        }
    }

    /**============================================File 操作============================================*/

    /**
     * 判断 File 是否存在
     */
    public static boolean isExists(String absolutePath) {
        return isExists(new File(absolutePath));
    }
    
    public static boolean isExists(File file) {
        return file != null && file.exists();
    }

    /**
     * 判断是否为文件
     */
    public static boolean isFile(String absolutePath) {
        return isFile(new File(absolutePath));
    }

    public static boolean isFile(File file) {
        return isExists(file) && file.isFile();
    }

    /**
     * 判断是否为目录
     */
    public static boolean isDirectory(String absolutePath) {
        return isDirectory(new File(absolutePath));
    }

    public static boolean isDirectory(File dir) {
        return isExists(dir) && dir.isDirectory();
    }

    /**
     * 创建目录
     * @return 是否创建成功
     */
    public static boolean createDirectory(String dirPath) {
        return createDirectory(new File(dirPath));
    }
    
    public static boolean createDirectory(File file) {
        return isDirectory(file) || file.mkdirs();// 存在，就不用创建（包括上级目录的创建）
    }

    /**
     * 创建文件
     * @return 是否创建成功
     */
    public static boolean createFile(String absolutePath) {
        return createFile(new File(absolutePath));
    }
    
    public static boolean createFile(File file) {
        if(isFile(file)){
            return true;
        }else{
            // 判断父目录是否存在
            File parentFile = file.getParentFile();
            if(isExists(parentFile)){
                return createNewFile(file);
            }else{
                // 不存在，先创建目录
                if(createDirectory(parentFile)){
                    return createNewFile(file);
                }else{
                    return false;// 创建目录失败
                }
            }
        }
    }

    public static boolean createNewFile(File file){
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除文件
     * @return 是否删除成功
     */
    public static boolean deleteFile(String absolutePath) throws FileNotFoundException {
        return deleteFile(new File(absolutePath));
    }

    public static boolean deleteFile(File file) throws FileNotFoundException {
        if(!isExists(file)){
            throw new FileNotFoundException();
        }
        return file.delete();
    }

    /**
     * 复制或移动目录
     * @param srcDir   源目录
     * @param destDir  目标目录
     * @param isDeleteSrc   是否删除源目录
     * @return 成功与否
     */
    private static boolean copyOrMoveDir(File srcDir, File destDir, boolean isDeleteSrc) throws IOException {
        if(srcDir == null || destDir == null){
            return false;
        }
        String srcPath = srcDir.getPath() + File.separator;
        String destPath = destDir.getPath() + File.separator;
        if (destPath.contains(srcPath)){
            throw new IOException("can not do this, move directory failed");
        }
        if(!isDirectory(srcDir)){
            throw new FileNotFoundException("have no src directory!");
        }
        // 创建目标目录
        if (createDirectory(destDir)) {
            // 开始移动 (递归)
            File[] files = srcDir.listFiles();
            for (File file : files) {
                File oneDestFile = new File(destPath + file.getName());
                if (file.isFile()) {
                    // 复制或移动文件 如果操作失败返回false
                    if (!copyOrMoveFile(file, oneDestFile, isDeleteSrc)) return false;
                }
                if (file.isDirectory()) {
                    // 递归 复制或移动目录 如果操作失败返回false
                    if (!copyOrMoveDir(file, oneDestFile, isDeleteSrc)) return false;
                }
            }
            // 是否删除
            if (isDeleteSrc) {
                if(deleteDirectory(srcDir)) return false;// 删除旧目录失败
            }
            return true;// 循环完成，则成功
        }else{
            return false;// 创建目标目录失败
        }
    }

    // 复制或移动文件
    private static boolean copyOrMoveFile(File srcFile, File destFile, boolean isDeleteSrcFile) throws IOException {
        if(srcFile == null || destFile == null){
            return false;
        }
        if(!isFile(srcFile)){
            throw new FileNotFoundException("have no src file!");
        }
        // 创建目标文件
        if (createFile(destFile)) {
            // 开始移动 抛出 IOException
            FileIOUtils.writeFileFromIS(destFile, new FileInputStream(srcFile), false);
            if (isDeleteSrcFile) {// 删除旧文件
                if(!deleteFile(srcFile)) return false;// 失败
            }
            return true;
        }else{
            return false;// 创建目标文件失败
        }
    }

    /**
     * 复制目录
     */
    public static boolean copyDir(String srcDirPath, String destDirPath) throws IOException {
        return copyDir(new File(srcDirPath), new File(destDirPath));
    }

    public static boolean copyDir(File srcDir, File destDir) throws IOException {
        return copyOrMoveDir(srcDir, destDir, false);
    }

    /**
     * 复制文件
     */
    public static boolean copyFile(String absolutePath, String destFilePath) throws IOException {
        return copyFile(new File(absolutePath), new File(destFilePath));
    }

    public static boolean copyFile(File srcFile, File destFile) throws IOException {
        return copyOrMoveFile(srcFile, destFile, false);
    }

    /**
     * 移动目录
     */
    public static boolean moveDir(String srcDirPath, String destDirPath) throws IOException {
        return moveDir(new File(srcDirPath), new File(destDirPath));
    }

    public static boolean moveDir(File srcDir, File destDir) throws IOException {
        return copyOrMoveDir(srcDir, destDir, true);
    }

    /**
     * 移动文件
     */
    public static boolean moveFile(String absolutePath, String destFilePath) throws IOException {
        return moveFile(new File(absolutePath), new File(destFilePath));
    }

    public static boolean moveFile(File srcFile, File destFile) throws IOException {
        return copyOrMoveFile(srcFile, destFile, true);
    }

    /**
     * 递归删除目录
     * @return 是否删除成功
     * @throws FileNotFoundException gai该目录路径 Directory 不存在
     */
    public static boolean deleteDirectory(String dirPath) throws FileNotFoundException {
        return deleteDirectory(new File(dirPath));
    }

    public static boolean deleteDirectory(String dirPath, FileFilter filter) throws FileNotFoundException {
        return deleteDirectory(new File(dirPath), filter);
    }

    public static boolean deleteDirectory(File dir) throws FileNotFoundException {
        return deleteDirectory(dir, new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return true;
            }
        });
    }

    public static boolean deleteDirectory(File dir, FileFilter filter) throws FileNotFoundException {
        if(!isDirectory(dir)){
            throw new FileNotFoundException("have no this directory");
        }
        // 存在，就递归删除
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (filter.accept(file)) {
                    if (file.isFile()) {
                        if (!file.delete()) return false;
                    }
                    // 递归删除
                    if (file.isDirectory()) {
                        if (!deleteDirectory(file, filter)) return false;
                    }
                }
            }
        }
        return dir.delete();// 最后删除该层的目录
    }

    /**
     * 获取目录下所有文件
     * @return 目录下所有文件 (默认不递归)
     */
    @NonNull
    public static ArrayList<File> listFilesInDir(String dirPath) {
        return listFilesInDir(dirPath, false);
    }
    
    @NonNull
    public static ArrayList<File> listFilesInDir(String dirPath, boolean isRecursive) {
        return listFilesInDir(new File(dirPath), isRecursive);
    }
    
    @NonNull
    public static ArrayList<File> listFilesInDir(File dir) {
        return listFilesInDir(dir, false);
    }
    
    @NonNull
    public static ArrayList<File> listFilesInDir(File dir, boolean isRecursive) { // isRecursive 是否递归
        return listFilesInDirWithFilter(dir, new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return true;// 默认不过滤
            }
        }, isRecursive);
    }

    /**
     * 获取目录下所有过滤的文件
     * @param filter 过滤条件
     * @return 目录下所有过滤后的文件 (默认不递归)
     */
    @NonNull
    public static ArrayList<File> listFilesInDirWithFilter(String dirPath, FileFilter filter) {
        return listFilesInDirWithFilter(new File(dirPath), filter, false);
    }

    @NonNull
    public static ArrayList<File> listFilesInDirWithFilter(String dirPath, FileFilter filter, boolean isRecursive) {
        return listFilesInDirWithFilter(new File(dirPath), filter, isRecursive);
    }

    @NonNull
    public static ArrayList<File> listFilesInDirWithFilter(File dir, FileFilter filter) {
        return listFilesInDirWithFilter(dir, filter, false);
    }

    @NonNull
    public static ArrayList<File> listFilesInDirWithFilter(File dir, FileFilter filter, boolean isRecursive) {
        ArrayList<File> list = new ArrayList<>();
        if (!isDirectory(dir)) return list;
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                // 根据创建的 FileFilter 重写 accept 接受的规则，来过滤
                if (filter.accept(file)) {
                    list.add(file);
                }
                // 如果 File 为 Directory，并且需要递归
                if (isRecursive && file.isDirectory()) {
                    list.addAll(listFilesInDirWithFilter(file, filter, true));
                }
            }
        }
        return list;
    }

    /**========================================private 辅助方法========================================*/

    @NonNull
    private static char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * byteArr转hexString
     * @param bytes 字节数组
     * @return 16进制大写字符串
     */
    private static String bytes2HexString(byte[] bytes) {
        if (bytes == null) return null;
        int len = bytes.length;
        if (len <= 0) return null;
        char[] ret = new char[len << 1];
        for (int i = 0, j = 0; i < len; i++) {
            ret[j++] = hexDigits[bytes[i] >>> 4 & 0x0f];
            ret[j++] = hexDigits[bytes[i] & 0x0f];
        }
        return new String(ret);
    }

    /**
     * 字节数组转合适内存大小
     * <p>保留2位小数</p>
     */
    @SuppressLint("DefaultLocale")
    private static String byte2FitMemorySize(long byteNum) {
        if (byteNum < 0) {
            return "shouldn't be less than zero!";
        } else if (byteNum < 1024) {
            return String.format("%.2fB", (double) byteNum);
        } else if (byteNum < (1024 * 1024)) {
            return String.format("%.2fKB", (double) byteNum / 1024);
        } else if (byteNum < (1024 * 1024 * 1024)) {
            return String.format("%.2fMB", (double) byteNum / (1024 * 1024));
        } else {
            return String.format("%.2fGB", (double) byteNum / (1024 * 1024 * 1024));
        }
    }

    /**
     * 获取不带拓展名的文件名
     */
    @Nullable
    public static String getFileNameNoExtension(File file) {
        if (file == null) return null;
        return getFileNameNoExtension(file.getPath());
    }

    public static String getFileNameNoExtension(String absolutePath) {
        if (TextUtils.isEmpty(absolutePath)) return absolutePath;
        int lastPoi = absolutePath.lastIndexOf('.');
        int lastSep = absolutePath.lastIndexOf(File.separator);
        if (lastSep == -1) {
            return (lastPoi == -1 ? absolutePath : absolutePath.substring(0, lastPoi));
        }
        if (lastPoi == -1 || lastSep > lastPoi) {
            return absolutePath.substring(lastSep + 1);
        }
        return absolutePath.substring(lastSep + 1, lastPoi);
    }

}
