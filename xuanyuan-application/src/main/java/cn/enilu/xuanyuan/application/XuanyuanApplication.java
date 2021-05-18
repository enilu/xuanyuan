package cn.enilu.xuanyuan.application;

import cn.enilu.xuanyuan.core.runner.AbstractRunner;
import cn.enilu.xuanyuan.core.util.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 * descript
 *
 * @Author enilu
 * @Date 2021/4/27 11:01
 * @Version 1.0
 */
public class XuanyuanApplication {
    private static Logger logger = LoggerFactory.getLogger(XuanyuanApplication.class);

    public static void main(String[] args) {
        String env = "dev";
        if(args!=null &&args.length>0){
            env = args[0];
        }

        logger.info("run env:{}",env);
        init(env);

    }

    public static void init(String env) {
        try {
            InputStream input = Class.forName(XuanyuanApplication.class.getName())
                    .getResourceAsStream("/conf-"+env+".properties");
            Properties properties = new Properties();
            properties.load(input);
            Set keySet = properties.keySet();
            Iterator iter = keySet.iterator();
            logger.info("configuration:  -------------");
            while (iter.hasNext()) {
                String key = (String) iter.next();
                String val = (String) properties.get(key);
                logger.info("configuration:  {}={}", key, val);
            }
            logger.info("configuration:  -------------");
            ConfigUtil.init(properties);
            String runClassName = ConfigUtil.get("system.run.class-name");
            logger.info("[ {} ] start run",runClassName);
            AbstractRunner runner = (AbstractRunner) Class.forName(runClassName).getDeclaredConstructor().newInstance();
            runner.run();
        } catch (Exception e) {
            logger.error("application failed to start ",e);
        }

    }
}
