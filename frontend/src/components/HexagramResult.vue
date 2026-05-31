<template>
  <div class="result-container" v-if="result">
    <!-- 本卦 -->
    <div class="result-card">
      <div class="hexagram-section">
        <div class="hexagram-label">本卦</div>
        <div class="hexagram-symbol">{{ result.originalSymbol }}</div>
        <div class="hexagram-name">{{ result.originalName }}</div>
        <div class="hexagram-number">第{{ result.originalNumber }}卦</div>
      </div>

      <!-- 爻展示 -->
      <div class="yao-display">
        <div
          v-for="yao in reversedYaos"
          :key="yao.position"
          class="yao-line"
          :class="{ changing: yao.changing, yang: yao.yang, yin: !yao.yang }"
        >
          <span class="yao-position">{{ yao.positionName }}</span>
          <span class="yao-symbol">{{ yao.symbol }}</span>
          <span class="yao-label">{{ yao.type }}</span>
          <span v-if="yao.changing" class="changing-badge">动</span>
        </div>
      </div>

      <!-- 卦辞 -->
      <div class="judgment-section">
        <div class="judgment-title">卦辞</div>
        <div class="judgment-text">{{ result.originalJudgment }}</div>
      </div>
    </div>

    <!-- 变卦 -->
    <div class="result-card" v-if="result.changedNumber">
      <div class="hexagram-section">
        <div class="hexagram-label" style="background: #8e44ad;">变卦</div>
        <div class="hexagram-symbol">{{ result.changedSymbol }}</div>
        <div class="hexagram-name">{{ result.changedName }}</div>
        <div class="hexagram-number">第{{ result.changedNumber }}卦</div>
      </div>

      <!-- 动爻信息 -->
      <div class="moving-yao" v-if="result.movingYaoDescriptions">
        <span class="moving-label">▸ 动爻：</span>
        <span v-for="(desc, i) in result.movingYaoDescriptions" :key="i">
          {{ desc }}<span v-if="i < result.movingYaoDescriptions.length - 1">、</span>
        </span>
      </div>

      <div class="judgment-section">
        <div class="judgment-title">卦辞</div>
        <div class="judgment-text">{{ result.changedJudgment }}</div>
      </div>
    </div>

    <!-- LLM 解读 -->
    <div class="result-card analysis-card">
      <div class="analysis-header">
        <span>📜 卦象解读</span>
        <span v-if="result.llmUsed" class="llm-badge">LLM 解读</span>
        <span v-else class="local-badge">系统解读</span>
      </div>
      <div class="analysis-content markdown-body">
        <p v-for="(para, i) in analysisParagraphs" :key="i">{{ para }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  result: {
    type: Object,
    default: null
  }
})

// 爻从下往上显示，所以需要反转
const reversedYaos = computed(() => {
  if (!props.result?.originalYaos) return []
  return [...props.result.originalYaos].reverse()
})

// 分析文本按段落分割
const analysisParagraphs = computed(() => {
  if (!props.result?.analysis) return []
  return props.result.analysis
    .split('\n')
    .filter(line => line.trim())
})
</script>

<style scoped>
.result-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-width: 500px;
  margin: 0 auto;
  padding: 20px;
}

.result-card {
  background: white;
  border-radius: 16px;
  padding: 28px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
}

.hexagram-section {
  text-align: center;
  margin-bottom: 20px;
}

.hexagram-label {
  display: inline-block;
  background: #c0392b;
  color: white;
  padding: 4px 16px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 12px;
}

.hexagram-symbol {
  font-size: 64px;
  line-height: 1;
  margin-bottom: 8px;
}

.hexagram-name {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 4px;
}

.hexagram-number {
  font-size: 13px;
  color: #888;
}

/* 爻展示 */
.yao-display {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  margin-bottom: 20px;
}

.yao-line {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  border-radius: 8px;
  background: #fafafa;
  min-width: 200px;
  justify-content: center;
}

.yao-line.changing {
  background: #fff3e0;
  border: 1px solid #ffe0b2;
}

.yao-position {
  font-size: 13px;
  color: #888;
  min-width: 40px;
}

.yao-symbol {
  font-size: 28px;
  line-height: 1;
}

.yao-label {
  font-size: 13px;
  color: #666;
  min-width: 32px;
}

.changing-badge {
  background: #e67e22;
  color: white;
  padding: 1px 8px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 600;
}

/* 卦辞 */
.judgment-section {
  border-top: 1px solid #eee;
  padding-top: 16px;
}

.judgment-title {
  font-size: 13px;
  color: #888;
  font-weight: 600;
  margin-bottom: 8px;
}

.judgment-text {
  font-size: 15px;
  color: #444;
  line-height: 1.8;
}

/* 动爻 */
.moving-yao {
  background: #fff8e1;
  border: 1px solid #ffe082;
  border-radius: 8px;
  padding: 10px 16px;
  font-size: 14px;
  color: #e67e22;
  margin-bottom: 16px;
}

.moving-label {
  font-weight: 600;
}

/* 分析 */
.analysis-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
}

.analysis-content {
  line-height: 1.8;
  color: #333;
  font-size: 15px;
}

.analysis-content p {
  margin: 0 0 12px 0;
}

.llm-badge {
  background: #27ae60;
  color: white;
  padding: 2px 10px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 600;
}

.local-badge {
  background: #95a5a6;
  color: white;
  padding: 2px 10px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 600;
}
</style>
