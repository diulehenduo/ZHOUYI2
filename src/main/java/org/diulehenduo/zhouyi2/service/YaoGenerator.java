package org.diulehenduo.zhouyi2.service;

import org.diulehenduo.zhouyi2.entity.Yao;
import org.diulehenduo.zhouyi2.enums.YaoType;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 爻生成器 - 模拟三枚铜钱抛掷生成六爻
 * <p>
 * 传统摇卦法：用三枚铜钱抛掷六次，每次得出一爻。
 * 铜钱正反规则：
 * <ul>
 *   <li>正面（阳）记 3 分</li>
 *   <li>反面（阴）记 2 分</li>
 *   <li>三枚总分为 6/7/8/9</li>
 *   <li>6=老阴（变爻），7=少阳，8=少阴，9=老阳（变爻）</li>
 * </ul>
 * </p>
 */
@Component
public class YaoGenerator {

    private final Random random = new SecureRandom();

    /**
     * 生成六爻（从初爻到上爻）
     *
     * @return 包含 6 个爻的列表，索引 0=初爻（最下），5=上爻（最上）
     */
    public List<Yao> generate() {
        List<Yao> yaos = new ArrayList<>(6);
        for (int i = 0; i < 6; i++) {
            yaos.add(generateYao(i));
        }
        return yaos;
    }

    /**
     * 生成单爻
     *
     * @param position 爻的位置（0=初爻，5=上爻）
     * @return 生成的爻
     */
    private Yao generateYao(int position) {
        int total = tossThreeCoins();
        YaoType type = YaoType.fromValue(total);
        return new Yao(position, type);
    }

    /**
     * 模拟三枚铜钱抛掷
     * <p>
     * 每枚铜钱：正面（阳）概率 1/2，反面（阴）概率 1/2
     * </p>
     *
     * @return 三枚铜钱总分（6、7、8、9 之一）
     */
    private int tossThreeCoins() {
        int total = 0;
        for (int i = 0; i < 3; i++) {
            // 0=反面(2分), 1=正面(3分)
            total += random.nextBoolean() ? 3 : 2;
        }
        return total;
    }

    /**
     * 根据已有的爻生成变卦的爻
     *
     * @param originalYaos 本卦六爻
     * @return 变卦六爻（变爻取反，不变爻保持）
     */
    public List<Yao> generateChangedYaos(List<Yao> originalYaos) {
        List<Yao> changed = new ArrayList<>(6);
        for (int i = 0; i < 6; i++) {
            Yao original = originalYaos.get(i);
            if (original.isChanging()) {
                // 变爻取反
                YaoType changedType = original.getType().changed();
                changed.add(new Yao(i, changedType));
            } else {
                // 不变爻保持
                changed.add(new Yao(i, original.getType()));
            }
        }
        return changed;
    }
}
