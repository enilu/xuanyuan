package cn.enilu.xuanyuan.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 执行脚本
 */
public class ShellExecutor {
    private static  Logger logger = LoggerFactory.getLogger(ShellExecutor.class);

    public static boolean callCmd(String cmd, boolean wait) {
        try {

            Process process = Runtime.getRuntime().exec(cmd);
            if (wait) {
                int status = process.waitFor();
                if (status != 0) {
                    logger.error("{}执行失败,执行结果status:{}", cmd, status);
                    return false;
                } else {
                    logger.debug("{}执行成功", cmd);
                    return true;
                }
            } else {
                logger.debug("{}执行完毕", cmd);
                return true;

            }

        } catch (Exception e) {
            logger.error("{}执行脚失败", cmd, e);
            return false;
        }
    }
}
