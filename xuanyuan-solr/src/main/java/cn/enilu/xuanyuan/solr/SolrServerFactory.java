package cn.enilu.xuanyuan.solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * solor服务工厂类
 */
public class SolrServerFactory {
    protected static Logger logger = LoggerFactory.getLogger(SolrServerFactory.class);

    static ConcurrentHashMap<String, HttpSolrServer> serverPool = new ConcurrentHashMap<String, HttpSolrServer>();
    static ConcurrentHashMap<String, Long> sorIdMap = new ConcurrentHashMap<String, Long>();

    public static Long getSolrId(String url,String name) {
        if(sorIdMap.get(name)==null){
            getSolrServer(url,name);
        }
        Long id = sorIdMap.get(name)+1;
        sorIdMap.put(name,id);
        return id;
    };
    public static HttpSolrServer getSolrServer(String url,String name) {
        if (serverPool.containsKey(name)) {
            return serverPool.get(name);
        }

        synchronized (serverPool) {
            if (serverPool.containsKey(name)) {
                return serverPool.get(name);
            }

            HttpSolrServer solrServer = null;

            try {
                solrServer = initSolrServer(url,name);
                serverPool.put(name, solrServer);
                sorIdMap.put(name,maxSolrIndex(solrServer));
            } catch (Exception e) {
                logger.error("Error occurs while connect solr server {}: {}", name, e.getMessage(),e);

            }
            return solrServer;
        }
    }


    public static long maxSolrIndex(HttpSolrServer solrServer) {
        SolrQuery query = new SolrQuery();
        query.setStart(0);
        query.setRows(1);
        query.setQuery("*:*");
        query.setSort("id", SolrQuery.ORDER.desc);
        query.addField("id");
        QueryResponse rsp = null;
        try {
            rsp = solrServer.query(query);
            if (null != rsp) {
                SolrDocumentList solrList = rsp.getResults();
                if (!solrList.isEmpty()) {
                    SolrDocument sd = solrList.get(0);
                    String idStr = sd.getFieldValue("id").toString();
                    Long id = Long.valueOf(idStr);
                    return id;
                } else {
                    return 0;
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
        }
        return -1;
    }


    /**
     * 初始化SolrServer
     *
     * @return
     */
    private static HttpSolrServer initSolrServer(String url,String name) {
        SolrConfig config = SolrConfig.getConfig();


         url = url+name;
        int maxRetries = config.getMaxRetries();
        int connectionTimeout = config.getConnectionTimeout();
        int soTimeout = config.getSoTimeout();
        int defaultMaxConnectionsPerHost = config.getDefaultMaxConnectionsPerHost();
        int maxTotalConnections = config.getMaxTotalConnections();
        boolean followRedirects = config.getFollowRedirects();
        boolean allowCompression =config.getAllowCompression();


        HttpSolrServer server = new HttpSolrServer(url);
        server.setMaxRetries(maxRetries);
        server.setConnectionTimeout(Integer.valueOf(connectionTimeout));
        server.setSoTimeout(soTimeout);
        server.setDefaultMaxConnectionsPerHost(defaultMaxConnectionsPerHost);
        server.setMaxTotalConnections(maxTotalConnections);
        server.setFollowRedirects(followRedirects);
        server.setAllowCompression(allowCompression);

        return server;
    }


}