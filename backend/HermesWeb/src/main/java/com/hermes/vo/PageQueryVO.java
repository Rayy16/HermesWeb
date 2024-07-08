package com.hermes.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hermes.common.exception.ParamValidFailedException;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PageQueryVO {
    public static int DEFAULT_PAGE = 1;
    public static int DEFAULT_PAGESIZE = 10;
    Integer pageSize;
    Integer page;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public static void validatePageQueryVO(PageQueryVO request) {
        Integer pageSize = request.getPageSize();
        Integer page = request.getPage();
        if (page == null || pageSize == null) {
            request.setPage(PageQueryVO.DEFAULT_PAGE);
            request.setPageSize(PageQueryVO.DEFAULT_PAGESIZE);
            return;
        }
        if (page <= 0 || pageSize <= 0) {
            throw new ParamValidFailedException("invalid page or page_size number, page = " + page + "; page_size = " + pageSize);
        }
    }
}
