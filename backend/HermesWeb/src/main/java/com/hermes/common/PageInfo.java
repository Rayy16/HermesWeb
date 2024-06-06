package com.hermes.common;

import lombok.Getter;

@Getter
public class PageInfo {
    private PageInfo(Integer page, Integer pageSize) {
        this.limit = pageSize;
        this.offset = (page-1) * pageSize;
        this.page = page;
        this.pageSize = pageSize;
    }
    public static PageInfo build(Integer page, Integer pageSize) {
        return new PageInfo(page, pageSize);
    }
    final private Integer limit;
    final private Integer offset;
    final private Integer page;
    final private Integer pageSize;
}
