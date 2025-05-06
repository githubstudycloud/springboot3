package com.platform.visualization.domain.model.dataset;

/**
 * 查询定义
 */
public class Query {
    private final String queryText;
    private final QueryType queryType;
    private final Integer timeout;

    public Query(String queryText, QueryType queryType, Integer timeout) {
        this.queryText = queryText;
        this.queryType = queryType;
        this.timeout = timeout;
    }

    public String getQueryText() {
        return queryText;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public Integer getTimeout() {
        return timeout;
    }

    /**
     * 查询类型
     */
    public enum QueryType {
        SQL, NOSQL, API, CUSTOM
    }
}