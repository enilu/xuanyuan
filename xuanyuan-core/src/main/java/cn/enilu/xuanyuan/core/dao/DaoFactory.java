package cn.enilu.xuanyuan.core.dao;

import com.alibaba.druid.pool.DruidDataSource;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;

public class DaoFactory
{

    public static Dao dao(String jdbcurl, String username, String password) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(jdbcurl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        Dao dao = new NutDao(dataSource);
        return dao;
    }
}
