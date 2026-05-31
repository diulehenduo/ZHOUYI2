package org.diulehenduo.zhouyi2.enums;

/**
 * 爻的类型枚举
 * <p>
 * 对应三枚铜钱抛掷结果：
 * <ul>
 *   <li>LAO_YANG (9) — 三正（老阳）⚊，变爻</li>
 *   <li>SHAO_YIN  (8) — 二正一反（少阴）⚋，不变</li>
 *   <li>SHAO_YANG (7) — 一正二反（少阳）⚊，不变</li>
 *   <li>LAO_YIN  (6) — 三反（老阴）⚋，变爻</li>
 * </ul>
 * </p>
 */
public enum YaoType {

    LAO_YIN(6, "老阴", "⚋", true),
    SHAO_YANG(7, "少阳", "⚊", false),
    SHAO_YIN(8, "少阴", "⚋", false),
    LAO_YANG(9, "老阳", "⚊", true);

    /** 三枚铜钱总点数（正面=3，反面=2） */
    private final int value;

    /** 中文名 */
    private final String label;

    /** 符号 */
    private final String symbol;

    /** 是否为变爻（老阴/老阳为变爻） */
    private final boolean changing;

    YaoType(int value, String label, String symbol, boolean changing) {
        this.value = value;
        this.label = label;
        this.symbol = symbol;
        this.changing = changing;
    }

    /**
     * 根据三枚铜钱总点数获取对应的爻类型
     */
    public static YaoType fromValue(int value) {
        for (YaoType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("无效的爻值: " + value + "（有效值: 6,7,8,9）");
    }

    /**
     * 获取变爻后的类型（老阴→少阳，老阳→少阴，少阴→少阳，少阳→少阴）
     */
    public YaoType changed() {
        return switch (this) {
            case LAO_YIN -> SHAO_YANG;
            case LAO_YANG -> SHAO_YIN;
            case SHAO_YIN -> SHAO_YANG;
            case SHAO_YANG -> SHAO_YIN;
        };
    }

    /**
     * 是否为阳爻（少阳/老阳）
     */
    public boolean isYang() {
        return this == SHAO_YANG || this == LAO_YANG;
    }

    public int getValue() { return value; }

    public String getLabel() { return label; }

    public String getSymbol() { return symbol; }

    public boolean isChanging() { return changing; }
}
