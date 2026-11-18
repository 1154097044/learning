package com.xy.learning.week3mybatis.dto;

/** 事务退出后重新查询数据库的快照；不是用内存对象猜测提交/回滚结果。 */
public record TxResult(String scenario,boolean userExists,long todoCount) {
}
