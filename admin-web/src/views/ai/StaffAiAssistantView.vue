<template>
  <section class="page-section ai-page">
    <PageHeader
      title="AI 助手"
      description="基于当前单位数据回答业务问题，先查结构化数据，再由大模型组织答案。"
    />

    <div class="ai-layout">
      <el-card shadow="never" class="ai-side-card">
        <div class="side-title">提问范围</div>
        <div class="side-desc">只回答当前单位内的数据，不会跨公司读取。</div>

        <div class="side-block">
          <div class="side-label">聚焦批次</div>
          <el-select
            v-model="batchId"
            clearable
            filterable
            placeholder="可选，指定某个批次"
            style="width: 100%"
          >
            <el-option
              v-for="item in batches"
              :key="item.id"
              :label="`${item.batchCode} / ${item.productName}`"
              :value="item.id"
            />
          </el-select>
          <div class="focus-tip">
            {{ batchId ? `当前将优先围绕批次 ${focusedBatchLabel} 回答。` : '未选择时，默认回答整个单位的数据问题。' }}
          </div>
        </div>

        <div class="side-block">
          <div class="side-label">推荐问题</div>
          <el-button
            v-for="item in suggestions"
            :key="item"
            text
            class="suggest-btn"
            @click="applySuggestion(item)"
          >
            {{ item }}
          </el-button>
        </div>
      </el-card>

      <el-card shadow="never" class="ai-main-card">
        <div class="ask-box">
          <el-input
            v-model="question"
            type="textarea"
            :rows="4"
            maxlength="300"
            show-word-limit
            :placeholder="batchId ? '例如：这个批次现在最明显的风险是什么？' : '例如：当前公司最需要优先处理的风险是什么？'"
          />
          <div class="ask-actions">
            <span class="ask-tip">
              {{ batchId ? '已开启批次聚焦，AI 会优先回答当前批次问题。' : 'AI 只会基于当前公司数据回答。' }}
            </span>
            <el-button type="primary" :loading="loading" :disabled="!question.trim()" @click="handleAsk">
              发送提问
            </el-button>
          </div>
        </div>

        <div v-if="history.length === 0" class="empty-state">
          <div class="empty-title">还没有提问记录</div>
          <div class="empty-desc">从左侧选一个推荐问题，或者直接输入你关心的数据问题。</div>
        </div>

        <div v-else class="chat-list">
          <article v-for="item in history" :key="item.id" class="chat-card">
            <div class="chat-question">
              <div class="chat-role">你问</div>
              <div class="chat-text">{{ item.question }}</div>
            </div>
            <div class="chat-answer">
              <div class="chat-role success">AI 助手</div>
              <div class="chat-context">{{ item.answer.contextTitle }}</div>
              <div class="chat-text">{{ item.answer.answer }}</div>
              <div v-if="item.answer.references?.length" class="chat-refs">
                <button
                  v-for="ref in item.answer.references"
                  :key="`${ref.type}-${ref.label}-${ref.batchId || 0}`"
                  type="button"
                  class="ref-tag ref-button"
                  @click="openReference(ref)"
                >
                  {{ ref.label }}
                </button>
              </div>
            </div>
          </article>
        </div>
      </el-card>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import { askStaffAi, type AiChatAnswer, type AiChatReference } from '../../api/modules/ai'
import { getBatchList } from '../../api/modules/batch'

type BatchOption = { id: number; batchCode: string; productName: string }
type ChatRecord = { id: number; question: string; answer: AiChatAnswer }

const router = useRouter()
const loading = ref(false)
const question = ref('')
const batchId = ref<number | null>(null)
const batches = ref<BatchOption[]>([])
const history = ref<ChatRecord[]>([])

const suggestions = [
  '当前公司最需要优先处理的风险是什么？',
  '最近哪些批次风险最高？',
  '如果我要先补数据，最该补哪几项？',
  '当前选中批次有哪些明显问题？',
]

const focusedBatchLabel = computed(() => {
  const current = batches.value.find((item) => item.id === batchId.value)
  return current ? `${current.batchCode} / ${current.productName}` : '当前批次'
})

function applySuggestion(value: string) {
  question.value = value
}

function openReference(reference: AiChatReference) {
  if (reference.type === 'batch_archive' && reference.batchId) {
    router.push(`/batches/${reference.batchId}/archive`)
    return
  }
  ElMessage.info(reference.label)
}

async function loadBatches() {
  batches.value = (await getBatchList()) as BatchOption[]
}

async function handleAsk() {
  const raw = question.value.trim()
  if (!raw) return
  loading.value = true
  try {
    const answer = await askStaffAi({ question: raw, batchId: batchId.value })
    history.value.unshift({
      id: Date.now(),
      question: raw,
      answer,
    })
    question.value = ''
  } catch {
    ElMessage.error('AI 助手暂时不可用，请稍后重试')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadBatches()
})
</script>

<style scoped>
.ai-page,
.ai-layout {
  display: grid;
  gap: 16px;
}

.ai-layout {
  grid-template-columns: 320px minmax(0, 1fr);
}

.ai-side-card,
.ai-main-card {
  border-radius: 18px;
}

.side-title {
  font-size: 20px;
  font-weight: 700;
  color: #24374e;
}

.side-desc {
  margin-top: 8px;
  color: #64748b;
  line-height: 1.6;
}

.side-block {
  margin-top: 20px;
  display: grid;
  gap: 10px;
}

.side-label {
  font-size: 14px;
  color: #475569;
  font-weight: 600;
}

.focus-tip {
  font-size: 12px;
  color: #64748b;
  line-height: 1.5;
}

.suggest-btn {
  justify-content: flex-start;
  white-space: normal;
  line-height: 1.6;
  padding: 0;
}

.ask-box {
  display: grid;
  gap: 12px;
}

.ask-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.ask-tip,
.chat-context,
.empty-desc {
  color: #64748b;
}

.empty-state {
  margin-top: 28px;
  padding: 36px 18px;
  text-align: center;
  background: #f8fafc;
  border-radius: 18px;
}

.empty-title {
  font-size: 18px;
  font-weight: 700;
  color: #24374e;
}

.chat-list {
  margin-top: 20px;
  display: grid;
  gap: 14px;
}

.chat-card {
  border-radius: 18px;
  background: #f8fafc;
  padding: 16px;
  display: grid;
  gap: 14px;
}

.chat-role {
  font-size: 13px;
  font-weight: 700;
  color: #7c5f34;
}

.chat-role.success {
  color: #45632a;
}

.chat-text {
  margin-top: 6px;
  white-space: pre-wrap;
  line-height: 1.7;
  color: #1f2937;
}

.chat-refs {
  margin-top: 10px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.ref-tag {
  background: #eef6e8;
  color: #4d6b31;
  border-radius: 999px;
  padding: 4px 10px;
  font-size: 12px;
}

.ref-button {
  border: none;
  cursor: pointer;
}

@media (max-width: 960px) {
  .ai-layout {
    grid-template-columns: 1fr;
  }

  .ask-actions {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
