package com.xy.learning.week2.common;

import java.util.List;


/**
 * 通用分页结果。对照 Android Paging 里 LoadResult.Page 的 data + 总数信息。
 * 服务端分页要同时给前端 content（当页数据）和 total（总条数）才能算总页数。
 */
public record PageResult<T>(List<T> content, long total, long page, long size) {

    public int getTotalPage() {
        return size == 0 ? 0 : (int) Math.ceil((double) total / size);
    }
}
