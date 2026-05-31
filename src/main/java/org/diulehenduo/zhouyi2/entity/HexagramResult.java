package org.diulehenduo.zhouyi2.entity;

import java.util.List;

/**
 * 完整卦象结果
 * <p>
 * 包含本卦（主卦）、变卦（变卦）、动爻列表等信息。
 * </p>
 */
public class HexagramResult {

    /** 本卦编号（1~64） */
    private final int originalNumber;

    /** 本卦卦名 */
    private final String originalName;

    /** 本卦卦象符号（如 ䷀） */
    private final String originalSymbol;

    /** 本卦卦辞 */
    private final String originalJudgment;

    /** 本卦六爻 */
    private final List<Yao> originalYaos;

    /** 变卦编号（无可动爻时与主卦相同） */
    private final int changedNumber;

    /** 变卦卦名 */
    private final String changedName;

    /** 变卦卦象符号 */
    private final String changedSymbol;

    /** 变卦卦辞 */
    private final String changedJudgment;

    /** 变卦六爻 */
    private final List<Yao> changedYaos;

    /** 动爻列表（变爻） */
    private final List<Yao> movingYaos;

    public HexagramResult(int originalNumber, String originalName, String originalSymbol,
                          String originalJudgment, List<Yao> originalYaos,
                          int changedNumber, String changedName, String changedSymbol,
                          String changedJudgment, List<Yao> changedYaos,
                          List<Yao> movingYaos) {
        this.originalNumber = originalNumber;
        this.originalName = originalName;
        this.originalSymbol = originalSymbol;
        this.originalJudgment = originalJudgment;
        this.originalYaos = originalYaos;
        this.changedNumber = changedNumber;
        this.changedName = changedName;
        this.changedSymbol = changedSymbol;
        this.changedJudgment = changedJudgment;
        this.changedYaos = changedYaos;
        this.movingYaos = movingYaos;
    }

    /**
     * 是否有动爻（本卦与变卦不同）
     */
    public boolean hasMovingYao() {
        return originalNumber != changedNumber;
    }

    // --- Getter 方法用于 JSON 序列化 ---

    public int getOriginalNumber() { return originalNumber; }

    public String getOriginalName() { return originalName; }

    public String getOriginalSymbol() { return originalSymbol; }

    public String getOriginalJudgment() { return originalJudgment; }

    public List<Yao> getOriginalYaos() { return originalYaos; }

    public int getChangedNumber() { return changedNumber; }

    public String getChangedName() { return changedName; }

    public String getChangedSymbol() { return changedSymbol; }

    public String getChangedJudgment() { return changedJudgment; }

    public List<Yao> getChangedYaos() { return changedYaos; }

    public List<Yao> getMovingYaos() { return movingYaos; }
}
