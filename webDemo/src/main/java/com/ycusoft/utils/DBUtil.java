package com.ycusoft.utils;

import org.apache.commons.dbutils.QueryRunner;

public class DBUtil {
    private static final QueryRunner QUERY_RUNNER;

    static {
        QUERY_RUNNER = new QueryRunner(DruidUtil.getDataSource());
    }

    public static QueryRunner getQueryRunner() {
        return QUERY_RUNNER;
    }
}