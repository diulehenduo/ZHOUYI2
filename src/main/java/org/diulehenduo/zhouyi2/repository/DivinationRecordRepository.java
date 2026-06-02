package org.diulehenduo.zhouyi2.repository;

import org.diulehenduo.zhouyi2.entity.DivinationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 占卜记录数据仓库
 */
@Repository
public interface DivinationRecordRepository extends JpaRepository<DivinationRecord, Long> {

    /** 按客户姓名查询 */
    List<DivinationRecord> findByNameOrderByCreatedAtDesc(String name);

    /** 查询最近 N 条记录 */
    List<DivinationRecord> findTop10ByOrderByCreatedAtDesc();
}
