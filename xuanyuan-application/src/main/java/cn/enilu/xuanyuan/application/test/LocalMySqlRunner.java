package cn.enilu.xuanyuan.application.test;

import cn.enilu.xuanyuan.core.dao.DaoFactory;
import cn.enilu.xuanyuan.core.runner.AbstractRunner;
import cn.enilu.xuanyuan.core.util.ConfigUtil;
import org.nutz.dao.Dao;
import org.nutz.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * descript
 *
 * @Author enilu
 * @Date 2021/4/27 11:09
 * @Version 1.0
 */
public class LocalMySqlRunner extends AbstractRunner {
    private Logger logger = LoggerFactory.getLogger(LocalMySqlRunner.class);
    @Override
    protected Dao initDao() {
        String jdbcUrl = ConfigUtil.get("jdbc.url");
        String jdbcUser = ConfigUtil.get("jdbc.user");
        String jdbcPass = ConfigUtil.get("jdbc.pass");
        return DaoFactory.dao(jdbcUrl,jdbcUser,jdbcPass);
    }

    @Override
    public void process() {
        List<Map> list = baseDao.queryMapList("select 1 as number");
        logger.info("查询结果:{}",Json.toJson(list));
    }
}
