package com.hermes.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageQueryDTO<E> {
    private List<E> pageResult;
    private Integer total;
}
