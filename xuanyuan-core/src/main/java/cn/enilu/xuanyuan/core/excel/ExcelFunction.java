package cn.enilu.xuanyuan.core.excel;


import cn.enilu.xuanyuan.core.util.DateUtil;

import java.util.Date;

/**
 * descript
 *
 * @Author enilu
 * @Date 2021/5/18 14:35
 * @Version 1.0
 */
public class ExcelFunction {
    public String dateFmt(Date date, String fmt) {
        if (date == null) {
            return "";
        }
        return DateUtil.formatDate(date, fmt);
    }
}
