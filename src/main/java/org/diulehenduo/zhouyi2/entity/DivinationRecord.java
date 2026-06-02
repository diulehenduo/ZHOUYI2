package org.diulehenduo.zhouyi2.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 占卜记录 - 存储每次占卜的客户数据和卦象结果
 */
@Entity
@Table(name = "divination_record")
public class DivinationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 客户姓名 */
    @Column(nullable = false, length = 50)
    private String name;

    /** 测算事由 */
    @Column(nullable = false, length = 500)
    private String matter;

    /** 本卦编号 */
    @Column(name = "original_number", nullable = false)
    private Integer originalNumber;

    /** 本卦卦名 */
    @Column(name = "original_name", nullable = false, length = 50)
    private String originalName;

    /** 本卦卦象符号 */
    @Column(name = "original_symbol", length = 10)
    private String originalSymbol;

    /** 本卦卦辞 */
    @Column(name = "original_judgment", columnDefinition = "TEXT")
    private String originalJudgment;

    /** 变卦编号 */
    @Column(name = "changed_number")
    private Integer changedNumber;

    /** 变卦卦名 */
    @Column(name = "changed_name", length = 50)
    private String changedName;

    /** 变卦卦象符号 */
    @Column(name = "changed_symbol", length = 10)
    private String changedSymbol;

    /** 动爻描述 */
    @Column(name = "moving_yaos", length = 100)
    private String movingYaos;

    /** 大模型/系统解读 */
    @Column(columnDefinition = "TEXT")
    private String analysis;

    /** 是否使用大模型解读 */
    @Column(name = "llm_used", nullable = false)
    private Boolean llmUsed;

    /** 创建时间 */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ===== getter / setter =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMatter() { return matter; }
    public void setMatter(String matter) { this.matter = matter; }

    public Integer getOriginalNumber() { return originalNumber; }
    public void setOriginalNumber(Integer originalNumber) { this.originalNumber = originalNumber; }

    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }

    public String getOriginalSymbol() { return originalSymbol; }
    public void setOriginalSymbol(String originalSymbol) { this.originalSymbol = originalSymbol; }

    public String getOriginalJudgment() { return originalJudgment; }
    public void setOriginalJudgment(String originalJudgment) { this.originalJudgment = originalJudgment; }

    public Integer getChangedNumber() { return changedNumber; }
    public void setChangedNumber(Integer changedNumber) { this.changedNumber = changedNumber; }

    public String getChangedName() { return changedName; }
    public void setChangedName(String changedName) { this.changedName = changedName; }

    public String getChangedSymbol() { return changedSymbol; }
    public void setChangedSymbol(String changedSymbol) { this.changedSymbol = changedSymbol; }

    public String getMovingYaos() { return movingYaos; }
    public void setMovingYaos(String movingYaos) { this.movingYaos = movingYaos; }

    public String getAnalysis() { return analysis; }
    public void setAnalysis(String analysis) { this.analysis = analysis; }

    public Boolean getLlmUsed() { return llmUsed; }
    public void setLlmUsed(Boolean llmUsed) { this.llmUsed = llmUsed; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
