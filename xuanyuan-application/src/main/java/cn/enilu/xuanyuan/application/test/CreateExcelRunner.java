package cn.enilu.xuanyuan.application.test;

import cn.enilu.xuanyuan.core.bean.Ret;
import cn.enilu.xuanyuan.core.excel.ExcelUtil;
import cn.enilu.xuanyuan.core.runner.AbstractRunner;
import cn.enilu.xuanyuan.core.util.Lists;
import cn.enilu.xuanyuan.core.util.Maps;
import org.nutz.dao.Dao;
import org.nutz.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 演示根据模板生成excel文件
 *
 * @Author enilu
 * @Date 2021/5/18 14:41
 * @Version 1.0
 */
public class CreateExcelRunner extends AbstractRunner {
    private Logger logger = LoggerFactory.getLogger(CreateExcelRunner.class);
    @Override
    protected Dao initDao() {
        return null;
    }

    @Override
    protected String getDescript() {
        return "演示根据模板生成excel文件";
    }

    @Override
    public void process() {
        String template = "templates/test.xlsx";
        String filePath = "d:\\test.xlsx";
        List<Map> list = Lists.newArrayList();
        for(int i=0;i<100;i++){
            Map map  =Maps.newHashMap();
            map.put("cfgName","参数名"+i);
            map.put("cfgValue","参数值"+i);
            map.put("cfgDesc","参数描述"+i);
            map.put("createTime",new Date());
            list.add(map);
        }
        Map<String,Object> data = Maps.newHashMap("list",list);
        Ret ret = ExcelUtil.create(template,filePath,data);
        logger.info(Json.toJson(ret));
    }
}
