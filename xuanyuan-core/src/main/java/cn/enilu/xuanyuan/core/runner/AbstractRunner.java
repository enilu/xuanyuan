package cn.enilu.xuanyuan.core.runner;

import cn.enilu.xuanyuan.core.dao.BaseDao;
import org.nutz.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抽象的任务运行类
 *
 * @Author enilu
 * @Date 2021/4/27 11:00
 * @Version 1.0
 */
public abstract class AbstractRunner {
    protected BaseDao baseDao;
    private Logger logger = LoggerFactory.getLogger(getClass());
    protected abstract Dao initDao();

    /**
     * 模块逻辑入口
     */
    public void run() {
        Dao dao = this.initDao();
        if(dao!=null) {
            baseDao = new BaseDao(dao);
        }
        logger.info("start process");
        this.process();
    }

    public abstract void process();
}
