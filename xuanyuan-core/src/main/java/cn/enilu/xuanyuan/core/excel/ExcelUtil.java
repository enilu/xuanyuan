package cn.enilu.xuanyuan.core.excel;

import cn.enilu.xuanyuan.core.bean.Ret;
import cn.enilu.xuanyuan.core.bean.Rets;
import org.jxls.common.Context;
import org.jxls.expression.JexlExpressionEvaluator;
import org.jxls.transform.Transformer;
import org.jxls.util.JxlsHelper;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 根据模板和数据生成excel文件
 *
 * @Author enilu
 * @Date 2021/5/18 14:34
 * @Version 1.0
 */
public class ExcelUtil {

    /**
     * 根据模板创建excel文件
     *
     * @param template excel模板
     * @param fileName 导出的文件完整路径
     * @param data     excel中填充的数据
     * @return
     */
    public static Ret create(String template, String fileName, Map<String, Object> data) {
        FileOutputStream outputStream = null;
        File file = new File(fileName);
        try {

            // 定义输出类型
            outputStream = new FileOutputStream(file);

            JxlsHelper jxlsHelper = JxlsHelper.getInstance();
            String templateFile = ExcelUtil.class.getClassLoader().getResource(template).getPath();
            InputStream is = new BufferedInputStream(new FileInputStream(templateFile));

            Transformer transformer = jxlsHelper.createTransformer(is, outputStream);
            Context context = new Context();
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                context.putVar(entry.getKey(), entry.getValue());
            }

            JexlExpressionEvaluator evaluator = (JexlExpressionEvaluator) transformer.getTransformationConfig().getExpressionEvaluator();
            Map<String, Object> funcs = new HashMap<String, Object>(4);
            funcs.put("utils", new ExcelFunction());
            evaluator.getJexlEngine().setFunctions(funcs);
            jxlsHelper.processTemplate(context, transformer);
        } catch (Exception e) {
            e.printStackTrace();
            return Rets.failure(e.getMessage());
        } finally {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return Rets.success(fileName);
    }

}
