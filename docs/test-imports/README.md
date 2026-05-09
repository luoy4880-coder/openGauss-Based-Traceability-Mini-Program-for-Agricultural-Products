# 测试文件说明

这次补的是“完整批次导入包”样例，不再只是单独一份报告文案。

## 完整导入包

- [full-batch-import-pass.json](F:/GraduationProject-yujia/yujia-test/docs/test-imports/full-batch-import-pass.json)
  - 正常批次
  - 包含基地、批次、生产记录、物流链路、质检报告、单品码生成数量

- [full-batch-import-risk.json](F:/GraduationProject-yujia/yujia-test/docs/test-imports/full-batch-import-risk.json)
  - 异常批次
  - 同样包含完整链路信息
  - 适合测试导入后自动生成风险任务

## 其他辅助样例

- [report-pass-sample.md](F:/GraduationProject-yujia/yujia-test/docs/test-imports/report-pass-sample.md)
- [report-fail-sample.md](F:/GraduationProject-yujia/yujia-test/docs/test-imports/report-fail-sample.md)
- [report-logistics-risk-sample.txt](F:/GraduationProject-yujia/yujia-test/docs/test-imports/report-logistics-risk-sample.txt)
- [feedback-high-risk-sample.txt](F:/GraduationProject-yujia/yujia-test/docs/test-imports/feedback-high-risk-sample.txt)
- [feedback-medium-risk-sample.txt](F:/GraduationProject-yujia/yujia-test/docs/test-imports/feedback-medium-risk-sample.txt)

## 说明

你刚才指出的问题是对的：

- 你要的不是“已有批次上传一个报告”
- 而是“一份文件里把基地、批次、生产记录、物流链路、质检信息一起导入”

所以这两个 `full-batch-import-*.json` 才是现在正确方向的测试文件。
