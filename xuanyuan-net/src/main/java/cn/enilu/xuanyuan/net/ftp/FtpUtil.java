package cn.enilu.xuanyuan.net.ftp;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;

import java.io.*;

public class FtpUtil {
    // 日志对象
    static final Logger FTP_LOG = Logger.getLogger(FtpUtil.class);


    /**
     * 连接到FTP服务器
     *
     * @return
     * @throws Exception
     */
    public static FTPClient connectToFtp(String ip, int port, String user, String password, String filepath)
            throws Exception {
        FTPClient ftpClient = null;
        try {
            ftpClient = new FTPClient();
            ftpClient.connect(ip, port);
            if (!ftpClient.login(user, password)) {
                FTP_LOG.error("用户名或密码错误！");
                ftpClient.logout();
                ftpClient.disconnect();
            }
            ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
            ftpClient.changeWorkingDirectory(filepath);
        } catch (IOException e) {
            FTP_LOG.error("连接FTP服务器时发生IO异常！", e);
            throw new Exception("连接FTP服务器时发生IO异常！");
        }
        return ftpClient;
    }

    /**
     * 将文件上传至FTP服务器。
     *
     * @param fileToUp
     * @param fileName
     * @throws Exception
     */
    public static void upLoadFileFtp(InputStream fileToUp, String fileName, FTPClient ftpClient) throws Exception {
        if (null == ftpClient || !ftpClient.isAvailable()) {
            FTP_LOG.error("上传文件失败，未能正确连接到FTP服务器！");
            throw new Exception("上传文件失败，未能正确连接到FTP服务器！");
        }
        // 正确连接到FTP服务器
        else {
            try {
                if (ftpClient.storeFile(fileName, fileToUp)) {
                    FTP_LOG.info(fileName + "上传文件成功！");
                }
            } catch (IOException e) {
                FTP_LOG.error("上传文件至FTP服务器时发生IO异常！", e);
                throw new Exception("上传文件至FTP服务器时发生IO异常！,文件名称:" + fileName);
            } catch (Exception e) {
                FTP_LOG.error("上传文件至FTP服务器时发生异常！", e);
                throw new Exception("上传文件至FTP服务器时发生异常！,文件名称:" + fileName);
            } finally {
                try {
                    fileToUp.close();
                } catch (IOException e) {
                    FTP_LOG.error("上传文件时关闭输入流时发生异常,文件名称:" + fileName, e);
                    throw new Exception("连接FTP服务器时发生IO异常！文件名称:" + fileName);
                }

            }
        }
    }

    /**
     * 将文件上传至FTP服务器
     *
     * @param filePath
     * @param fileName
     * @throws Exception
     */
    public static void upLoadFileNameFtp(String filePath, String fileName, FTPClient ftpClient) throws Exception {
        if (null == ftpClient || !ftpClient.isAvailable()) {
            FTP_LOG.error("上传文件失败，未能正确连接到FTP服务器！");
            throw new Exception("上传文件失败，未能正确连接到FTP服务器！");
        }
        File f = new File(filePath + File.separator + fileName);
        if (!f.exists()) {
            throw new Exception("上传文件时,文件不存在,filePath:" + filePath + ",fileName:" + fileName);
        }
        InputStream in = null;
        try {
            in = new FileInputStream(f);
            if (ftpClient.storeFile(fileName, in)) {
                FTP_LOG.info(fileName + "上传文件成功！");
            }
        } catch (IOException e) {
            FTP_LOG.error("上传文件至FTP服务器时发生IO异常！", e);
            throw new Exception("上传文件至FTP服务器时发生IO异常！,filePath:" + filePath + ",fileName:" + fileName);
        } catch (Exception e) {
            FTP_LOG.error("上传文件至FTP服务器时发生异常！", e);
            throw new Exception("上传文件至FTP服务器时发生异常！,filePath:" + filePath + ",fileName:" + fileName);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                FTP_LOG.error("上传文件关闭输入流时发生异常,filePath:" + filePath + ",fileName:" + fileName, e);
                throw new Exception("上传文件关闭输入流时发生异常,filePath:" + filePath + ",fileName:" + fileName);
            }

        }

    }

    /**
     * 从FTP服务器下载文件
     *
     * @param fileName
     * @param fileToDown
     * @throws Exception
     */
    public static void downloadFileFtp(String fileName, OutputStream fileToDown, FTPClient ftpClient) throws Exception {
        if (null == ftpClient || !ftpClient.isAvailable()) {
            FTP_LOG.error("从FTP服务器下载文件，未能正确连接到FTP服务器！");
            throw new Exception("从FTP服务器下载文件，未能正确连接到FTP服务器,fileName:" + fileName);
        }

        // 正确连接到FTP服务器
        else {
            InputStream in = null;
            try {
                in = ftpClient.retrieveFileStream(fileName);
                byte[] bytes = new byte[1024];
                int cnt = 0;
                while ((cnt = in.read(bytes, 0, bytes.length)) != -1) {
                    fileToDown.write(bytes, 0, cnt);
                }
                fileToDown.flush();
                FTP_LOG.info("文件下载成功！");
            } catch (IOException e) {
                FTP_LOG.error("从FTP服务器下载文件时发生IO异常！", e);
                throw new Exception("上传文件至FTP服务器时发生IO异常！,fileName:" + fileName);
            } catch (Exception e) {
                FTP_LOG.error("从FTP服务器下载文件时发生异常！", e);
                throw new Exception("上传文件至FTP服务器时发生异常！,fileName:" + fileName);
            } finally {
                if (null != in) {
                    try {
                        in.close();
                        ftpClient.completePendingCommand();
                    } catch (IOException e) {
                        FTP_LOG.error("从FTP服务器下载文件，关闭数据输出流时发生IO异常！", e);
                        throw new Exception("从FTP服务器下载文件，关闭数据输出流时发生IO异常！,fileName:" + fileName);
                    }
                }

            }
        }
    }

    /**
     * 检查文件是否存在
     *
     * @param fileName
     * @param ftpClient
     * @return
     * @throws Exception
     */
    public static boolean isFileExist(String fileName, FTPClient ftpClient) throws Exception {
        if (null == ftpClient || !ftpClient.isAvailable()) {
            FTP_LOG.error("判断文件是否存在，未能正确连接到FTP服务器！");
            throw new Exception("从FTP服务器下载文件，未能正确连接到FTP服务器,fileName:" + fileName);
        }
        InputStream in = null;
        try {
            in = ftpClient.retrieveFileStream(fileName);
            if (in != null) {
                return true;
            }

        } catch (IOException e) {
            FTP_LOG.error("判断文件是否存在时发生IO异常,fileName:" + fileName, e);
            throw new Exception("判断文件是否存在时发生IO异常,fileName:" + fileName);
        } finally {
            if (null != in) {
                try {
                    in.close();
                    ftpClient.completePendingCommand();
                } catch (IOException e) {
                    FTP_LOG.error("从FTP服务器下载文件，关闭数据输出流时发生IO异常！", e);
                    throw new Exception("从FTP服务器下载文件，关闭数据输入流时发生IO异常！,fileName:" + fileName);
                }
            }

        }
        return false;
    }

    /**
     * 测试时，注释掉FTP_LOG的声明，把下面的内部类打开
     */
    public static class FTP_LOG {
        public static void error(String msg, Exception e) {
            FTP_LOG.info(msg);
            if (null != e) {
                FTP_LOG.error("FtpUtil.error异常", e);
            }
        }

        public static void error(String msg) {
            error(msg, null);
        }
    }

}
