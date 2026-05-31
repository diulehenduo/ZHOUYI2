package org.diulehenduo.zhouyi2.entity;

import org.diulehenduo.zhouyi2.enums.YaoType;

/**
 * 爻（单个卦中的一条线）
 * <p>
 * 每卦由 6 条爻组成，从下往上（初爻→上爻）。
 * </p>
 */
public class Yao {

    /** 爻的位置（0=初爻，5=上爻） */
    private final int position;

    /** 爻的类型（老阴/少阳/少阴/老阳） */
    private final YaoType type;

    /** 是否变爻 */
    private final boolean changing;

    public Yao(int position, YaoType type) {
        if (position < 0 || position > 5) {
            throw new IllegalArgumentException("爻的位置必须在 0~5 之间: " + position);
        }
        this.position = position;
        this.type = type;
        this.changing = type.isChanging();
    }

    /**
     * 获取爻的二进制值（阳=1，阴=0）
     */
    public int getBinaryValue() {
        return type.isYang() ? 1 : 0;
    }

    /**
     * 获取爻的位置名称（初爻、二爻、……、上爻）
     */
    public String getPositionName() {
        return switch (position) {
            case 0 -> "初爻";
            case 1 -> "二爻";
            case 2 -> "三爻";
            case 3 -> "四爻";
            case 4 -> "五爻";
            case 5 -> "上爻";
            default -> "未知";
        };
    }

    public int getPosition() { return position; }

    public YaoType getType() { return type; }

    public boolean isChanging() { return changing; }

    @Override
    public String toString() {
        return getPositionName() + ":" + type.getSymbol() + (changing ? "✧" : "");
    }
}
