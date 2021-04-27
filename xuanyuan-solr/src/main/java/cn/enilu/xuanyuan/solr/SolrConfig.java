package cn.enilu.xuanyuan.solr;

import cn.enilu.xuanyuan.core.util.ConfigUtil;
import lombok.Data;

@Data
public class SolrConfig {
    private String url;
    private Integer maxRetries;
    private Integer connectionTimeout;
    private Integer soTimeout;
    private Integer defaultMaxConnectionsPerHost;
    private Integer maxTotalConnections;
    private Boolean followRedirects;
    private Boolean allowCompression;

    /**
     * 构建solorConfig
     * @return
     */
    public static  SolrConfig getConfig(){
        SolrConfig config = new SolrConfig();
        config.setUrl(String.valueOf(ConfigUtil.get("solr.url")));
        config.setMaxRetries(Integer.valueOf(ConfigUtil.get("solr.maxRetries")));
        config.setConnectionTimeout(Integer.valueOf(ConfigUtil.get("solr.connectionTimeout")));
        config.setSoTimeout(Integer.valueOf(ConfigUtil.get("solr.soTimeout")));
        config.setDefaultMaxConnectionsPerHost(Integer.valueOf(ConfigUtil.get("solr.defaultMaxConnectionsPerHost")));
        config.setMaxTotalConnections(Integer.valueOf(ConfigUtil.get("solr.maxTotalConnections")));
        config.setFollowRedirects(Boolean.valueOf(ConfigUtil.get("solr.followRedirects")));
        config.setAllowCompression(Boolean.valueOf(ConfigUtil.get("solr.allowCompression")));
        return config;

    }

}
