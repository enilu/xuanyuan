package cn.enilu.xuanyuan.application.test;

import cn.enilu.xuanyuan.core.runner.AbstractRunner;
import org.nutz.dao.Dao;

/**
 * 测试所有用例
 *
 * @Author enilu
 * @Date 2021/5/18 14:47
 * @Version 1.0
 */
public class AllTestRunner extends AbstractRunner {
    @Override
    protected Dao initDao() {
        return null;
    }

    @Override
    public void process() {
        AsyncRunner asyncRunner = new AsyncRunner();
        asyncRunner.process();
        CreateExcelRunner createExcelRunner = new CreateExcelRunner();
        createExcelRunner.process();

    }
}
