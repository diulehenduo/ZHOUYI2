package org.diulehenduo.zhouyi2.service;

import org.diulehenduo.zhouyi2.entity.DivinationRecord;
import org.diulehenduo.zhouyi2.entity.HexagramResult;
import org.diulehenduo.zhouyi2.entity.Yao;
import org.diulehenduo.zhouyi2.model.response.DivinationResponse;
import org.diulehenduo.zhouyi2.repository.DivinationRecordRepository;
import org.diulehenduo.zhouyi2.util.HexagramDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * 核心占卜服务
 * <p>
 * 编排完整的占卜流程：
 * <ol>
 *   <li>摇卦生成六爻</li>
 *   <li>匹配本卦</li>
 *   <li>计算变卦</li>
 *   <li>构建 LLM prompt</li>
 *   <li>调用 LLM 分析</li>
 *   <li>生成响应（LLM 失败时使用预设卦辞降级）</li>
 * </ol>
 * </p>
 */
@Service
public class DivinationService {

    private static final Logger log = LoggerFactory.getLogger(DivinationService.class);

    private final YaoGenerator yaoGenerator;
    private final LlmService llmService;
    private final DivinationRecordRepository recordRepository;

    public DivinationService(YaoGenerator yaoGenerator, LlmService llmService,
                             DivinationRecordRepository recordRepository) {
        this.yaoGenerator = yaoGenerator;
        this.llmService = llmService;
        this.recordRepository = recordRepository;
    }

    /**
     * 执行占卜
     *
     * @param name  用户姓名
     * @param matter 测算事由
     * @return 占卜结果
     */
    public DivinationResponse performDivination(String name, String matter) {
        // 1. 摇卦生成六爻
        List<Yao> originalYaos = yaoGenerator.generate();
        log.info("摇卦结果: {}", originalYaos.stream().map(Yao::toString).collect(Collectors.joining(", ")));

        // 2. 匹配本卦
        String[] originalHexagram = matchHexagram(originalYaos);

        // 3. 计算变卦（找出动爻）
        List<Yao> movingYaos = originalYaos.stream()
                .filter(Yao::isChanging)
                .collect(Collectors.toList());

        List<Yao> changedYaos;
        String[] changedHexagram;
        if (!movingYaos.isEmpty()) {
            changedYaos = yaoGenerator.generateChangedYaos(originalYaos);
            changedHexagram = matchHexagram(changedYaos);
            log.info("动爻: {}, 变卦: {}", movingYaos.size(), changedHexagram[1]);
        } else {
            changedYaos = originalYaos;
            changedHexagram = originalHexagram;
            log.info("无动爻（静卦）");
        }

        // 4. 构建 HexagramResult（内部对象）
        HexagramResult result = new HexagramResult(
                Integer.parseInt(originalHexagram[0]), originalHexagram[1], originalHexagram[2],
                originalHexagram[3], originalYaos,
                Integer.parseInt(changedHexagram[0]), changedHexagram[1], changedHexagram[2],
                changedHexagram[3], changedYaos,
                movingYaos
        );

        // 5. 构建 LLM prompt 并调用
        String prompt = buildPrompt(name, matter, result);
        String analysis = llmService.chat(prompt);

        // 6. 生成响应 DTO
        DivinationResponse response = buildResponse(name, matter, result, analysis);

        // 7. 保存占卜记录到数据库
        try {
            saveRecord(name, matter, response);
        } catch (Exception e) {
            log.warn("保存占卜记录失败: {}", e.getMessage());
        }

        return response;
    }

    /**
     * 匹配六爻对应的卦
     */
    private String[] matchHexagram(List<Yao> yaos) {
        int[] binaryYaos = yaos.stream()
                .mapToInt(Yao::getBinaryValue)
                .toArray();
        String[] hexagram = HexagramDictionary.findByYaos(binaryYaos);
        if (hexagram == null) {
            log.warn("未找到匹配的卦，二进制: {}", binaryYaos);
            return new String[]{"0", "未知", "？", "未知", ""};
        }
        return hexagram;
    }

    /**
     * 构建 LLM 使用的 Prompt
     */
    private String buildPrompt(String name, String matter, HexagramResult result) {
        StringBuilder sb = new StringBuilder();

        sb.append("【周易占卜请求】\n");
        sb.append("用户：").append(name).append("\n");
        sb.append("所问之事：").append(matter).append("\n\n");

        sb.append("【起卦结果】\n");

        // 本卦信息
        sb.append("◆ 本卦：").append(result.getOriginalName())
                .append("（第").append(result.getOriginalNumber()).append("卦 ")
                .append(result.getOriginalSymbol()).append("）\n");
        sb.append("  卦辞：").append(result.getOriginalJudgment()).append("\n");

        // 爻的详情
        sb.append("  六爻（从下往上）：\n");
        for (Yao yao : result.getOriginalYaos()) {
            sb.append("  ").append(yao.getPositionName()).append("：")
                    .append(yao.getType().getSymbol())
                    .append("（").append(yao.getType().getLabel()).append("）");
            if (yao.isChanging()) {
                sb.append(" ⚡动爻");
            }
            sb.append("\n");
        }

        // 变卦信息
        if (result.hasMovingYao()) {
            sb.append("\n◆ 变爻：");
            for (Yao yao : result.getMovingYaos()) {
                sb.append(yao.getPositionName()).append("、");
            }
            sb.deleteCharAt(sb.length() - 1);

            sb.append("\n◆ 变卦：").append(result.getChangedName())
                    .append("（第").append(result.getChangedNumber()).append("卦 ")
                    .append(result.getChangedSymbol()).append("）\n");
            sb.append("  卦辞：").append(result.getChangedJudgment()).append("\n");
        } else {
            sb.append("\n◆ 静卦：无动爻，以本卦卦辞为主\n");
        }

        sb.append("\n【解读要求】\n");
        sb.append("请结合用户【").append(name).append("】所问之事【").append(matter).append("】进行深入解读：\n");
        sb.append("1. 卦象总论：解释本卦的核心含义和象征意义\n");
        if (result.hasMovingYao()) {
            sb.append("2. 动爻分析：分析动爻的爻辞和对事情的启示\n");
            sb.append("3. 变卦趋势：从本卦到变卦的变化预示什么趋势\n");
            sb.append("4. 事理结合：针对\"").append(matter).append("\"给出具体分析\n");
            sb.append("5. 建议指引：给出行为建议和注意事项\n");
        } else {
            sb.append("2. 事理结合：针对\"").append(matter).append("\"分析卦象的含义\n");
            sb.append("3. 建议指引：给出行为建议和注意事项\n");
        }

        return sb.toString();
    }

    /**
     * 构建响应 DTO
     */
    private DivinationResponse buildResponse(String name, String matter,
                                              HexagramResult result, String llmAnalysis) {
        DivinationResponse resp = new DivinationResponse();

        // 本卦信息
        resp.setOriginalNumber(result.getOriginalNumber());
        resp.setOriginalName(result.getOriginalName());
        resp.setOriginalSymbol(result.getOriginalSymbol());
        resp.setOriginalJudgment(result.getOriginalJudgment());

        // 爻详情（前端展示用）
        List<DivinationResponse.YaoInfo> yaoInfos = new ArrayList<>(6);
        for (Yao yao : result.getOriginalYaos()) {
            yaoInfos.add(new DivinationResponse.YaoInfo(
                    yao.getPosition(),
                    yao.getPositionName(),
                    yao.getType().getLabel(),
                    yao.getType().getSymbol(),
                    yao.getType().isYang(),
                    yao.isChanging()
            ));
        }
        resp.setOriginalYaos(yaoInfos);

        // 变卦信息
        if (result.hasMovingYao()) {
            resp.setChangedNumber(result.getChangedNumber());
            resp.setChangedName(result.getChangedName());
            resp.setChangedSymbol(result.getChangedSymbol());
            resp.setChangedJudgment(result.getChangedJudgment());

            List<String> movingDesc = result.getMovingYaos().stream()
                    .map(Yao::getPositionName)
                    .collect(Collectors.toList());
            resp.setMovingYaoDescriptions(movingDesc);
        }

        // 大模型解读
        if (llmAnalysis != null && !llmAnalysis.isBlank()) {
            resp.setAnalysis(llmAnalysis);
            resp.setLlmUsed(true);
        } else {
            // LLM 降级：使用卦辞作为解读
            resp.setAnalysis(buildFallbackAnalysis(result));
            resp.setLlmUsed(false);
        }

        return resp;
    }

    /**
     * LLM 调用失败时的降级方案——使用预设卦辞
     */
    private String buildFallbackAnalysis(HexagramResult result) {
        StringBuilder sb = new StringBuilder();

        sb.append("【卦象解读】\n\n");
        sb.append("您所得之卦为「").append(result.getOriginalName()).append("」\n");
        sb.append("卦辞曰：").append(result.getOriginalJudgment()).append("\n\n");

        if (result.hasMovingYao()) {
            String movingPositions = result.getMovingYaos().stream()
                    .map(Yao::getPositionName)
                    .collect(Collectors.joining("、"));
            sb.append("其中有动爻：").append(movingPositions);
            sb.append("，变卦为「").append(result.getChangedName()).append("」\n");
            sb.append("变卦卦辞：").append(result.getChangedJudgment()).append("\n\n");
        }

        sb.append("（注：此为系统自动解读，如需更深入的分析，请配置大模型 API Key）");

        return sb.toString();
    }

    /**
     * 保存占卜记录到数据库
     */
    private void saveRecord(String name, String matter, DivinationResponse resp) {
        DivinationRecord record = new DivinationRecord();
        record.setName(name);
        record.setMatter(matter);

        record.setOriginalNumber(resp.getOriginalNumber());
        record.setOriginalName(resp.getOriginalName());
        record.setOriginalSymbol(resp.getOriginalSymbol());
        record.setOriginalJudgment(resp.getOriginalJudgment());

        if (resp.getChangedNumber() != null) {
            record.setChangedNumber(resp.getChangedNumber());
            record.setChangedName(resp.getChangedName());
            record.setChangedSymbol(resp.getChangedSymbol());
        }

        if (resp.getMovingYaoDescriptions() != null && !resp.getMovingYaoDescriptions().isEmpty()) {
            record.setMovingYaos(String.join("、", resp.getMovingYaoDescriptions()));
        }

        record.setAnalysis(resp.getAnalysis());
        record.setLlmUsed(resp.isLlmUsed());

        recordRepository.save(record);
        log.info("占卜记录已保存, id={}, name={}, 本卦={}",
                record.getId(), name, resp.getOriginalName());
    }
}
