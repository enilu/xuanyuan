package cn.enilu.xuanyuan.solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * solr操作类
 *
 * @Author enilu
 * @Date 2021/4/26 14:34
 * @Version 1.0
 */
public class CommonSolrService {
    private Logger logger = LoggerFactory.getLogger(CommonSolrService.class);
    /**
     * 添加索引
     *
     * @param solrServer
     * @param doc
     * @return
     */
    protected boolean add(HttpSolrServer solrServer, SolrInputDocument doc) {
        if (doc != null) {
            try {
                solrServer.add(doc);
                solrServer.commit();
            } catch (Exception e) {
                logger.error("操作solr异常", e);
                return false;
            }
        }
        return true;
    }

    protected boolean add(HttpSolrServer solrServer, List<SolrInputDocument> docList) {
        if (docList != null && docList.size() > 0) {
            try {
                solrServer.add(docList);
                solrServer.commit();
            } catch (Exception e) {
                logger.error("操作solr异常", e);
                return false;
            }
        }
        return true;
    }

    /**
     * 删除索引
     *
     * @param solrServer
     * @param id
     * @throws IOException
     * @throws SolrServerException
     */
    protected void delById(HttpSolrServer solrServer, Long id)  {
        try {
            solrServer.deleteById(id.toString());
            solrServer.commit();
        } catch (Exception e) {
            logger.error("删除索引失败:{}",id);
        }

    }

    /**
     * 删除索引
     *
     * @param solrServer
     * @param ids
     * @throws IOException
     * @throws SolrServerException
     */
    protected void delById(HttpSolrServer solrServer, List<String> ids) throws IOException, SolrServerException {
        solrServer.deleteById(ids);
        solrServer.commit();
    }

    /**
     * 查询索引记录
     *
     * @param solrServer
     * @param id
     * @return
     */
    protected SolrDocument getById(HttpSolrServer solrServer, String id) {
        SolrQuery query = new SolrQuery();
        query.setStart(0);
        query.setRows(1);
        query.setQuery(id);
        query.addField("id");
        try {
            QueryResponse rsp = solrServer.query(query);
            SolrDocumentList solrDocumentList = rsp.getResults();
            if (solrDocumentList != null && solrDocumentList.size() > 0) {
                return solrDocumentList.get(0);
            }

        } catch (Exception e) {
            logger.error("查询solr异常:{}", id, e);
        }
        return null;
    }
}
