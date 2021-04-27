package cn.enilu.xuanyuan.net.sftp;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Objects;
import java.util.Properties;
import java.util.Vector;

public class SFTPUtil {
	
	private     Logger logger = LoggerFactory.getLogger(SFTPUtil.class);
	
    private String host;
    private String username;
    private String password;
    private int port;
    private ChannelSftp sftp = null;  
    private String remotePath = "";
    private Channel channel = null;  
    private Session sshSession = null;  
    
    public SFTPUtil(String host, String username, String password, int port){
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = port;
    }
    /**
     * 连接服务器 
     * @Title: connect 
     * @author 刘剑飞
     * @date 2016年8月26日下午3:43:48
     * @return void
     * @throws
     */
    public boolean connect() {
        logger.info("start connect...");
    	boolean result = true;
        try {  
            if(sftp != null){  
                logger.info("sftp is not null");
            }
            logger.info("sftp is null");
            JSch jsch = new JSch();
            logger.info("get session");
            jsch.getSession(username, host, port);  
            sshSession = jsch.getSession(username, host, port);  
            sshSession.setPassword(password);  
            Properties sshConfig = new Properties();  
            sshConfig.put("StrictHostKeyChecking", "no");
            sshConfig.put("PreferredAuthentications", "password,gssapi-with-mic,publickey,keyboard-interactive");

            sshSession.setConfig(sshConfig);
            logger.info(" session connect");
            sshSession.connect();
            logger.info("session connect success");
            channel = sshSession.openChannel("sftp");  
            channel.connect();
            logger.info("channel connect success");
            sftp = (ChannelSftp) channel;  
            logger.info("SFTP连接服务器成功！");
        } catch (Exception e) {
            logger.error("SFTP连接服务器失败！",e);
            result = false;

        } 
        return result;
    }  
    
    /**
     * 断开
     * @Title: disconnect 
     * @author 刘剑飞
     * @date 2016年8月26日下午3:44:15
     * @return void
     * @throws
     */
    public void disconnect() {  
        if(this.sftp != null){  
            if(this.sftp.isConnected()){  
                this.sftp.disconnect();  
            }else if(this.sftp.isClosed()){  
                logger.info("sftp is closed already");
            }  
        }
        if(this.channel!=null){
            if (channel.isConnected()) {  
                channel.disconnect();  
            }  
        }
        if(this.sshSession!=null){
            if (sshSession.isConnected()) {  
                sshSession.disconnect();  
            } 
        }
  
    }  
    
    /**
     * 下载文件
     * @Title: download 
     * @author 刘剑飞
     * @date 2016年8月26日下午3:45:37
     * @param remotePath
     * @param remoteFilename
     * @param localFile
     * @return void
     * @throws
     */
    public void download(String remotePath , String remoteFilename,String localFile) {  
        try {  
            sftp.cd(remotePath);  
            File file = new File(localFile);  
            sftp.get(remoteFilename, new FileOutputStream(file));  
        } catch (Exception e) {  
            logger.info("SFTPUtil.download异常",e);
        }  
    }  
    
    /**
     * 上传文件
     * @Title: upload 
     * @author 刘剑飞
     * @date 2016年8月26日下午4:19:24
     * @param localFile
     * @return
     * @return boolean
     * @throws
     */
    public boolean upload(String localFile) {  
        File file = new File(localFile);  
        boolean flag = false;
        if(file.isFile()){  
            String remotePath = this.remotePath;  
            try {  
                createDir(remotePath, sftp);  
            } catch (Exception e) {  
            	logger.info("在SFTP服务器创建文件路径失败" + remotePath);
                return flag;
            }  
  
            try {
				this.sftp.put(new FileInputStream(file), file.getName());
				flag = true;
			} catch (FileNotFoundException e) {
				logger.info("上传文件到SFTP服务器失败" );
			} catch (SftpException e) {
				logger.info("上传文件到SFTP服务器失败" );
			}  
        }  
        return flag;
    }  
    
    /**
     * 创建远程路径
     * @Title: createDir 
     * @author 刘剑飞
     * @date 2016年8月26日下午3:52:10
     * @param filepath
     * @param sftp
     * @return void
     * @throws
     */
    private void createDir(String filepath, ChannelSftp sftp){  
        boolean bcreated = false;  
        boolean bparent = false;  
        File file = new File(filepath);  
        String ppath = file.getParent().replace("\\", "/");  
        try {  
            this.sftp.cd(ppath);  
            bparent = true;  
        } catch (SftpException e1) {  
            bparent = false;  
        }  
        try {  
            if(bparent){  
                try {  
                    this.sftp.cd(filepath);  
                    bcreated = true;  
                } catch (Exception e) {  
                    bcreated = false;  
                }  
                if(!bcreated){  
                    this.sftp.mkdir(filepath);  
                    bcreated = true;  
                }  
            }else{  
                createDir(ppath,sftp);  
                this.sftp.cd(ppath);  
                this.sftp.mkdir(filepath);  
            }  
        } catch (SftpException e) {  
            logger.info("mkdir failed :" + filepath);
            logger.info("SFTPUtil.createDir异常",e);
        }  
          
        try {  
            this.sftp.cd(filepath);  
        } catch (SftpException e) {  
            logger.info("SFTPUtil.createDir异常",e);
            logger.info("can not cd into :" + filepath);
        }  
          
    }  
    
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public ChannelSftp getSftp() {
		return sftp;
	}
	public void setSftp(ChannelSftp sftp) {
		this.sftp = sftp;
	}
	public String getRemotePath() {
		return remotePath;
	}
	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}
	
	
    public void del(String filepath) throws IOException{  
       File f = new File(filepath);//定义文件路径         
       if(f.exists() && f.isDirectory()){//判断是文件还是目录  
          if(f.listFiles().length==0){//若目录下没有文件则直接删除  
              f.delete();  
           }else{//若有则把文件放进数组，并判断是否有下级目录  
               File delFile[]=f.listFiles();  
               int i =f.listFiles().length;  
               for(int j=0;j<i;j++){  
                  if(delFile[j].isDirectory()){  
                     del(delFile[j].getAbsolutePath());//递归调用del方法并取得子目录路径  
                  }          
                   delFile[j].delete();//删除文件  
             }
          } 
       }else if(f.exists()&&!f.isDirectory()){
           f.delete();
       }
    }
    
    /**
     * 遍历目录下所有文件
     * @param remotePath
     */
    public Vector listFile(String remotePath) {  
        try {  
        	return sftp.ls(remotePath);
        } catch (Exception e) {
        	try {
        		logger.info("轮询路径:"+remotePath+"异常");
				return sftp.ls(remotePath);
			} catch (SftpException e1) {
				logger.info("SFTPUtil.listFile异常",e);
			}
            logger.info("SFTPUtil.listFile异常",e);
        }
		return null;  
    }  
    
    
    /**
     * 上传文件不需要创建远程目录
     * @Title: uploadWithOutCreateRemotePath 
     * @author qyh
     * @date 2018年1月24日下午4:19:24
     * @param localFile
     * @return
     * @return boolean
     * @throws
     */
    public boolean uploadFile(String localFile) {  
        File file = new File(localFile);  
        boolean flag = false;
        if(file.isFile()){  
//            String remotePath = this.remotePath;  
//            try {  
//                //createDir(remotePath, sftp);  
//            } catch (Exception e) {  
//            	_LOG.info("在SFTP服务器创建文件路径失败" + remotePath);  
//                return flag;
//            }  
  
            try {
            	logger.info("本地文件地址"+file.getName());
				this.sftp.put(new FileInputStream(file), file.getName());
				flag = true;
			} catch (FileNotFoundException e) {
				logger.info("上传文件到SFTP服务器失败",e );
			} catch (SftpException e) {
				logger.info("上传文件到SFTP服务器失败",e );
			}  
        }  
        return flag;
    }  
    
    
    /**
     * @throws SftpException 
     * 上传文件不需要创建远程目录并以流的形式传输
     * @Title: uploadWithOutCreateRemotePath 
     * @author qyh
     * @date 2018年1月24日下午4:19:24
     * @param localFile
     * @return
     * @return boolean
     * @throws
     */
	public boolean uploadFileWithStream(InputStream localFile, String fileName) throws SftpException {
		
		boolean flag = false;
		try{
			logger.info("上传流到SFTP服务器开始");
			this.sftp.cd(remotePath); 
			this.sftp.put(localFile, fileName);
			logger.info("上传流到SFTP服务器结束");
			flag = true;
		} catch (Exception e){
			throw new RuntimeException(e);
		} finally {
			if(localFile!=null){
				try {
					localFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}
    
	/**
	 * 哈密推数删除指定文件
	 * @param fileName
	 * @throws IOException
	 */
	public void delFile(String fileName) throws IOException {

		try {
			this.sftp.cd(remotePath);
			this.sftp.rm(fileName);
		} catch (SftpException e) {
			e.printStackTrace();
		}
	}

    public boolean upload(InputStream inputStream,String realityName) {
        boolean flag = false;
        String remotePath = this.remotePath;
        try {
            createDir(remotePath, sftp);
        } catch (Exception e) {
            logger.info("在SFTP服务器创建文件路径失败" + remotePath);
            return flag;
        }

        try {
            this.sftp.put(inputStream,realityName);
            flag = true;
        } catch (SftpException e) {
            logger.info("上传文件到SFTP服务器失败" );
        }
        return flag; 
    }
    
    /**
     * *判断远程SFTP服务上是否在存在某个文件
     * @param fileName
     * @return
     */
    public boolean isDirExist(String fileName) {

    	 boolean isExist = false;
    	 String remotePath = this.remotePath;
    	 
    	 try {
    		this.sftp.cd(remotePath);
			SftpATTRS attrs = this.sftp.stat(fileName);
			if(attrs != null) {
				isExist = true;
			}
			
			logger.info("判断远程SFTP服务目录{}是否在存在文件：{} 结果 {}", remotePath, fileName, isExist);
			
		} catch (SftpException e) {
			if(e.getMessage().toLowerCase().equals("no such file")) {
				isExist = false;
			}
		}
 
    	return isExist;
    }

}
