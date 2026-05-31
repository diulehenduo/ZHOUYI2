package org.diulehenduo.zhouyi2.controller;

import jakarta.validation.Valid;
import org.diulehenduo.zhouyi2.model.request.DivinationRequest;
import org.diulehenduo.zhouyi2.model.response.ApiResponse;
import org.diulehenduo.zhouyi2.model.response.DivinationResponse;
import org.diulehenduo.zhouyi2.service.DivinationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 占卜 API 控制器
 */
@RestController
@RequestMapping("/api/v1")
public class DivinationController {

    private static final Logger log = LoggerFactory.getLogger(DivinationController.class);

    private final DivinationService divinationService;

    public DivinationController(DivinationService divinationService) {
        this.divinationService = divinationService;
    }

    /**
     * 执行占卜
     * <p>
     * 接收用户姓名和测算事由，返回卦象解读结果。
     * </p>
     *
     * @param request 请求体：{ "name": "张三", "matter": "求事业" }
     * @return 卦象结果与大模型解读
     */
    @PostMapping("/divination")
    public ResponseEntity<ApiResponse<DivinationResponse>> divination(
            @Valid @RequestBody DivinationRequest request) {

        log.info("占卜请求: name={}, matter={}", request.getName(), request.getMatter());

        try {
            DivinationResponse result = divinationService.performDivination(
                    request.getName(), request.getMatter());

            log.info("占卜完成: name={}, 本卦={}, llmUsed={}",
                    request.getName(), result.getOriginalName(), result.isLlmUsed());

            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            log.error("占卜处理异常: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error(500, "占卜处理失败: " + e.getMessage()));
        }
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("周易占卜服务运行正常"));
    }
}
