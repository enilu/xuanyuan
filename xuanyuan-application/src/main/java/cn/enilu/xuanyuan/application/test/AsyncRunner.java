package cn.enilu.xuanyuan.application.test;

import cn.enilu.xuanyuan.core.async.AbstractAsyncProcessor;
import cn.enilu.xuanyuan.core.bean.Ret;
import cn.enilu.xuanyuan.core.bean.Rets;
import cn.enilu.xuanyuan.core.runner.AbstractRunner;
import org.nutz.dao.Dao;
import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * 异步处理演示
 *
 * @Author enilu
 * @Date 2021/5/18 11:56
 * @Version 1.0
 */
public class AsyncRunner extends AbstractRunner {
    private Logger logger = LoggerFactory.getLogger(AsyncRunner.class);
    class MyAsyncProcessor extends AbstractAsyncProcessor{

        private String filePath = "d:\\async_test.txt";
        @Override
        public void process() {
            int i= 0 ;
            while(i<3){
                try {
                    logger.info("{}:processing...",i);
                    Thread.sleep(1000L);
                    i++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Files.write(filePath,"this a test file");
        }

        @Override
        public boolean isFinished() {
           return new File(filePath).exists();
        }

        @Override
        public Ret getResult() {
            String txt = Files.read(filePath);
            return Rets.success(txt);
        }
    }
    @Override
    protected Dao initDao() {
        return null;
    }

    @Override
    protected String getDescript() {
        return "异步处理演示";
    }

    @Override
    public void process() {
        Ret ret = new MyAsyncProcessor().execute();
        logger.info(Json.toJson(ret));
    }
}
