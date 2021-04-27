package cn.enilu.xuanyuan.pdf.spire;

import cn.enilu.xuanyuan.core.bean.Ret;
import cn.enilu.xuanyuan.core.bean.Rets;
import com.spire.doc.Document;
import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfPageBase;
import org.nutz.lang.Files;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author: enilu
 * <p>
 * Created on 2020/11/25
 **/
public class PdfConvertUtil {
    public Ret pdf2Doc(String pdfFile, String docFile){
        PdfDocument pdf = new PdfDocument();
        pdf.loadFromFile(pdfFile);
        //保存为Word格式
        pdf.saveToFile(docFile, FileFormat.DOCX);
        return Rets.success();
    }
    public Ret pdf2Html(String pdfFile,String htmlFile){
        PdfDocument pdf = new PdfDocument();
        pdf.loadFromFile(pdfFile);
        //保存为html
        pdf.saveToFile(htmlFile, FileFormat.HTML);
        return Rets.success();
    }
    public Ret pdf2Txt(String pdfFile,String txtFile){
        if(Files.isFile(new File(txtFile))){
            return Rets.success();
        }
        //创建PdfDocument实例
        PdfDocument doc = new PdfDocument();
        //加载PDF文件
        doc.loadFromFile(pdfFile);

        //创建StringBuilder实例
        StringBuffer sb = new StringBuffer();

        PdfPageBase page;
        //遍历PDF页面，获取每个页面的文本并添加到StringBuilder对象
        for(int i= 0;i<doc.getPages().getCount();i++){
            page = doc.getPages().get(i);
            sb.append(page.extractText(true));
        }
        FileWriter writer;
        try {
            //将StringBuilder对象中的文本写入到文本文件
            writer = new FileWriter(txtFile);
            writer.write(sb.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return Rets.failure(e.getMessage());
        }
        doc.close();
        return Rets.success();
    }
    public Ret doc2Pdf(String docFile,String pdfFile){
        Document document = new Document();
        document.loadFromFile(docFile);
        //保存结果文件
        document.saveToFile(pdfFile, com.spire.doc.FileFormat.PDF);
        return Rets.success();
    }
}
