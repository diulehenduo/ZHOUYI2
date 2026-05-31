package org.diulehenduo.zhouyi2.model.response;

import java.util.List;

/**
 * 占卜结果响应体
 */
public class DivinationResponse {

    /** 本卦编号 */
    private int originalNumber;

    /** 本卦卦名 */
    private String originalName;

    /** 本卦卦象符号 */
    private String originalSymbol;

    /** 本卦卦辞 */
    private String originalJudgment;

    /** 原始六爻详情 */
    private List<YaoInfo> originalYaos;

    /** 变卦编号（无可动爻则与本卦相同） */
    private Integer changedNumber;

    /** 变卦卦名 */
    private String changedName;

    /** 变卦卦象符号 */
    private String changedSymbol;

    /** 变卦卦辞 */
    private String changedJudgment;

    /** 动爻描述（如"九三：君子终日乾乾"） */
    private List<String> movingYaoDescriptions;

    /** 大模型解读 */
    private String analysis;

    /** 是否使用了 LLM（false 表示使用预设卦辞降级） */
    private boolean llmUsed;

    public DivinationResponse() {}

    // --- getter / setter ---

    public int getOriginalNumber() { return originalNumber; }

    public void setOriginalNumber(int originalNumber) { this.originalNumber = originalNumber; }

    public String getOriginalName() { return originalName; }

    public void setOriginalName(String originalName) { this.originalName = originalName; }

    public String getOriginalSymbol() { return originalSymbol; }

    public void setOriginalSymbol(String originalSymbol) { this.originalSymbol = originalSymbol; }

    public String getOriginalJudgment() { return originalJudgment; }

    public void setOriginalJudgment(String originalJudgment) { this.originalJudgment = originalJudgment; }

    public List<YaoInfo> getOriginalYaos() { return originalYaos; }

    public void setOriginalYaos(List<YaoInfo> originalYaos) { this.originalYaos = originalYaos; }

    public Integer getChangedNumber() { return changedNumber; }

    public void setChangedNumber(Integer changedNumber) { this.changedNumber = changedNumber; }

    public String getChangedName() { return changedName; }

    public void setChangedName(String changedName) { this.changedName = changedName; }

    public String getChangedSymbol() { return changedSymbol; }

    public void setChangedSymbol(String changedSymbol) { this.changedSymbol = changedSymbol; }

    public String getChangedJudgment() { return changedJudgment; }

    public void setChangedJudgment(String changedJudgment) { this.changedJudgment = changedJudgment; }

    public List<String> getMovingYaoDescriptions() { return movingYaoDescriptions; }

    public void setMovingYaoDescriptions(List<String> movingYaoDescriptions) { this.movingYaoDescriptions = movingYaoDescriptions; }

    public String getAnalysis() { return analysis; }

    public void setAnalysis(String analysis) { this.analysis = analysis; }

    public boolean isLlmUsed() { return llmUsed; }

    public void setLlmUsed(boolean llmUsed) { this.llmUsed = llmUsed; }

    /**
     * 爻的简单信息（前端展示用）
     */
    public static class YaoInfo {
        private int position;
        private String positionName;
        private String type;
        private String symbol;
        private boolean yang;
        private boolean changing;

        public YaoInfo() {}

        public YaoInfo(int position, String positionName, String type, String symbol,
                       boolean yang, boolean changing) {
            this.position = position;
            this.positionName = positionName;
            this.type = type;
            this.symbol = symbol;
            this.yang = yang;
            this.changing = changing;
        }

        public int getPosition() { return position; }
        public void setPosition(int position) { this.position = position; }
        public String getPositionName() { return positionName; }
        public void setPositionName(String positionName) { this.positionName = positionName; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getSymbol() { return symbol; }
        public void setSymbol(String symbol) { this.symbol = symbol; }
        public boolean isYang() { return yang; }
        public void setYang(boolean yang) { this.yang = yang; }
        public boolean isChanging() { return changing; }
        public void setChanging(boolean changing) { this.changing = changing; }
    }
}
