package com.magus.opio;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Arrays;

public class Token {
    @JSONField(name = "cluster_name", ordinal = 1)
    private String cluster_name;
    @JSONField(name = "db_listen_addr_list", ordinal = 2)
    private String[] db_listen_addr_list;
    @JSONField(name = "expire", ordinal = 3)
    private long expire;
    @JSONField(name = "rand_number", ordinal = 4)
    private long rand_number;

    private String tokenCache;

    public Token(String cluster_name, String[] db_listen_addr_list, long expire, long rand_number) {
        this.cluster_name = cluster_name;
        this.db_listen_addr_list = db_listen_addr_list;
        this.expire = expire;
        this.rand_number = rand_number;
    }

    public String getCluster_name() {
        return cluster_name;
    }

    public String[] getDb_listen_addr_list() {
        return db_listen_addr_list;
    }

    public long getExpire() {
        return expire;
    }

    public long getRand_number() {
        return rand_number;
    }

    public String getTokenCache() {
        return tokenCache;
    }

    public void setCluster_name(String cluster_name) {
        this.cluster_name = cluster_name;
    }

    public void setDb_listen_addr_list(String[] db_listen_addr_list) {
        this.db_listen_addr_list = db_listen_addr_list;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public void setRand_number(long rand_number) {
        this.rand_number = rand_number;
    }

    public void setTokenCache(String tokenCache) {
        this.tokenCache = tokenCache;
    }

    @Override
    public String toString() {
        return "Token{" +
                "cluster_name='" + cluster_name + '\'' +
                ", db_listen_addr_list=" + Arrays.toString(db_listen_addr_list) +
                ", expire=" + expire +
                ", rand_number=" + rand_number +
                '}';
    }
}
