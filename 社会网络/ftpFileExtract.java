package spiderSystem;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.log4j.Logger;

/**
 * 简单操作FTP工具类 ,此工具类支持中文文件名，不支持中文目录
 * 如果需要支持中文目录，需要 new String(path.getBytes("UTF-8"),"ISO-8859-1") 对目录进行转码
 * @author WZH
 * 
 */
public class FTPUtil {

    private static Logger logger = Logger.getLogger(FTPUtil.class);

    /**
     * 获取FTPClient对象
     * @param ftpHost 服务器IP
     * @param ftpPort 服务器端口号
     * @param ftpUserName 用户名
     * @param ftpPassword 密码
     * @return FTPClient
     */
    public FTPSClient getFTPClient(String ftpHost, int ftpPort,
            String ftpUserName, String ftpPassword) {

        FTPSClient ftp = null;
        try {
            ftp = new FTPSClient();
            // 连接FPT服务器,设置IP及端口
            ftp.connect(ftpHost, ftpPort);
            // 设置用户名和密码
            ftp.login(ftpUserName, ftpPassword);
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            // 设置连接超时时间,5000毫秒
            ftp.setConnectTimeout(5000);
            // 设置中文编码集，防止中文乱码
            ftp.execPROT("P");
            ftp.enterLocalPassiveMode();
            ftp.setControlEncoding("UTF-8");
            if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                System.out.println("未连接到FTP，用户名或密码错误");
                ftp.disconnect();
            } else {
            	System.out.println("FTP连接成功");
            }

        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("FTP的IP地址可能错误，请正确配置");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("FTP的端口错误,请正确配置");
        }
        return ftp;
    }
    
    /**
     * 关闭FTP方法
     * @param ftp
     * @return
     */
    public boolean closeFTP(FTPSClient ftp){
        
        try {
            ftp.logout();
        } catch (Exception e) {
        	System.out.println("FTP关闭失败");
        }finally{
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                	System.out.println("FTP关闭失败");
                }
            }
        }
        
        return false;
        
    }
    
    
    /**
     * 遍历解析文件夹下所有文件
     * @param folderPath 需要解析的的文件夹
     * @param ftp FTPClient对象
     * @return
     */
    public boolean readFileByFolder(FTPClient ftp,String folderPath){
        boolean flage = false;
        try {
            ftp.changeWorkingDirectory(new String(folderPath.getBytes("UTF-8"),"ISO-8859-1"));
            //设置FTP连接模式
            ftp.enterLocalPassiveMode();
            //获取指定目录下文件文件对象集合
            FTPFile files[] = ftp.listFiles();
            InputStream in = null;
            BufferedReader reader = null;
            for (FTPFile file : files) {
                //判断为文件则解析
                if(file.isFile()){
                    String fileName = file.getName();
                    if(fileName.endsWith(".")){
                        in = ftp.retrieveFileStream(new String(file.getName().getBytes("UTF-8"),"ISO-8859-1"));
                        reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                        String temp;
                        StringBuffer buffer = new StringBuffer();
                        while((temp = reader.readLine())!=null){
                            buffer.append(temp);
                        }
                        if(reader!=null){
                            reader.close();
                        }
                        if(in!=null){
                            in.close();
                        }
                        //ftp.retrieveFileStream使用了流，需要释放一下，不然会返回空指针
                        ftp.completePendingCommand();
                        //这里就把一个txt文件完整解析成了个字符串，就可以调用实际需要操作的方法
                        System.out.println(buffer.toString());
                        //读取完毕删除文件。
//                        ftp.deleteFile(new String(file.getName().getBytes("UTF-8"),"ISO-8859-1"));
//                        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                    }
                }
                //判断为文件夹，递归
                if(file.isDirectory()){
                    String path = folderPath+File.separator+file.getName();
                    readFileByFolder(ftp, path);
                }
            } 
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("文件解析失败");
        }
        
        return flage;
        
    }
    
    public static void main(String[] args) {
        FTPUtil test = new FTPUtil();
        FTPSClient ftp = test.getFTPClient("47.104.188.189", 16001, "cancer_online","j8N%V1PLdU^22X@eg$Buhol&TNdE$AiX");
        test.readFileByFolder(ftp, "/");
        test.closeFTP(ftp);
        System.exit(0);
    }
}