/*
 Navicat Premium Data Transfer

 Source Server         : localhost_15432
 Source Server Type    : PostgreSQL
 Source Server Version : 90204 (90204)
 Source Host           : localhost:15432
 Source Catalog        : yujia_db
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 90204 (90204)
 File Encoding         : 65001

 Date: 23/05/2026 20:39:20
*/


-- ----------------------------
-- Sequence structure for base_info_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "base_info_id_seq";
CREATE SEQUENCE "base_info_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for company_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "company_id_seq";
CREATE SEQUENCE "company_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for inspection_report_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "inspection_report_id_seq";
CREATE SEQUENCE "inspection_report_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for logistics_record_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "logistics_record_id_seq";
CREATE SEQUENCE "logistics_record_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for product_batch_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "product_batch_id_seq";
CREATE SEQUENCE "product_batch_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for product_item_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "product_item_id_seq";
CREATE SEQUENCE "product_item_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for production_record_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "production_record_id_seq";
CREATE SEQUENCE "production_record_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for recall_record_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "recall_record_id_seq";
CREATE SEQUENCE "recall_record_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for scan_log_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "scan_log_id_seq";
CREATE SEQUENCE "scan_log_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for seq_base_code
-- ----------------------------
DROP SEQUENCE IF EXISTS "seq_base_code";
CREATE SEQUENCE "seq_base_code" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for seq_batch_code
-- ----------------------------
DROP SEQUENCE IF EXISTS "seq_batch_code";
CREATE SEQUENCE "seq_batch_code" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for seq_company_code
-- ----------------------------
DROP SEQUENCE IF EXISTS "seq_company_code";
CREATE SEQUENCE "seq_company_code" ;

-- ----------------------------
-- Sequence structure for seq_logistics_code
-- ----------------------------
DROP SEQUENCE IF EXISTS "seq_logistics_code";
CREATE SEQUENCE "seq_logistics_code" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for sys_role_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "sys_role_id_seq";
CREATE SEQUENCE "sys_role_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for sys_user_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "sys_user_id_seq";
CREATE SEQUENCE "sys_user_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for system_task_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "system_task_id_seq";
CREATE SEQUENCE "system_task_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for trace_code_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "trace_code_id_seq";
CREATE SEQUENCE "trace_code_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for user_feedback_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "user_feedback_id_seq";
CREATE SEQUENCE "user_feedback_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Table structure for base_info
-- ----------------------------
DROP TABLE IF EXISTS "base_info";
CREATE TABLE "base_info" (
  "id" int8 NOT NULL DEFAULT nextval('base_info_id_seq'::regclass),
  "base_code" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "base_name" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "manager_name" varchar(64) COLLATE "pg_catalog"."default",
  "contact_phone" varchar(20) COLLATE "pg_catalog"."default",
  "province" varchar(32) COLLATE "pg_catalog"."default",
  "city" varchar(32) COLLATE "pg_catalog"."default",
  "district" varchar(32) COLLATE "pg_catalog"."default",
  "address" varchar(255) COLLATE "pg_catalog"."default",
  "acreage" numeric(10,2),
  "status" int2 NOT NULL DEFAULT 1,
  "created_at" timestamp(6) NOT NULL DEFAULT pg_systimestamp(),
  "updated_at" timestamp(6) NOT NULL DEFAULT pg_systimestamp(),
  "company_id" int8
)
;

-- ----------------------------
-- Records of base_info
-- ----------------------------
BEGIN;
INSERT INTO "base_info" ("id", "base_code", "base_name", "manager_name", "contact_phone", "province", "city", "district", "address", "acreage", "status", "created_at", "updated_at", "company_id") VALUES (1, 'BAK', '新疆建设基地', 'yujia', '11158325698', '新疆', '乌鲁木齐', '天一', '白羊街道', 3.00, 1, '2026-04-15 11:22:44.560134', '2026-04-15 11:22:44.560134', 1), (2, 'GPH', '甘肃天水基地', '', '', '', '', '', '', 9.00, 1, '2026-04-15 11:23:48.144093', '2026-04-15 15:27:15.398501', 1), (3, 'ccb', 'ccb', '', '', '', '', '', '', NULL, 1, '2026-04-15 22:18:05.660058', '2026-04-15 22:18:05.660058', 1), (4, 'sssk', 'ssk', '', '', '', '', '', '', NULL, 0, '2026-04-15 22:18:15.219931', '2026-04-15 22:18:15.219931', 1), (5, '001', '001', '', '', '', '', '', '', NULL, 1, '2026-04-20 20:18:27.477826', '2026-04-20 20:18:27.477826', 1), (6, '002', '002', '', '', '', '', '', '', NULL, 1, '2026-04-20 20:18:32.5808', '2026-04-20 20:18:32.5808', 1), (7, '003', '003', '', '', '', '', '', '', NULL, 1, '2026-04-20 20:18:37.405364', '2026-04-20 20:18:37.405364', 1), (8, '004', '004', '', '', '', '', '', '', NULL, 1, '2026-04-20 20:18:41.796305', '2026-04-20 20:18:41.796305', 1), (10, '006', '006', '', '', '', '', '', '', NULL, 1, '2026-04-20 20:18:52.221686', '2026-04-20 20:18:52.221686', 1), (11, '007', '007', '', '', '', '', '', '', NULL, 1, '2026-04-20 20:18:59.080542', '2026-04-20 20:18:59.080542', 1), (12, '008', '008', '', '', '', '', '', '', NULL, 0, '2026-04-20 20:19:05.730803', '2026-04-20 20:20:23.026859', 1), (9, '005', '005', '', '', '', '', '', '', NULL, 0, '2026-04-20 20:18:46.747314', '2026-04-20 20:20:27.069903', 1), (13, 'YN001', '云南大理基地', 'qqq', '', '', '', '', '', NULL, 1, '2026-04-27 17:26:30.241357', '2026-04-27 17:26:30.241357', 1), (14, 'BASE-20260506-001', '四川省成都市郫都区雨佳草莓基地', '王女士', '15585966469', '四川省', '成都市', '郫都区', '', 1.00, 1, '2026-05-06 18:42:37.42077', '2026-05-06 18:42:37.42077', 1), (15, 'BASE-TEST-001', '玉佳示范草莓基地', '陈建国', '13800001111', '山东省', '潍坊市', '寿光市', '洛城街道现代农业园 1 号', 126.50, 1, '2026-05-07 10:26:25.20603', '2026-05-07 10:26:25.20603', 1), (16, 'BASE-QI-CHERRY-001', '寿光绿源智慧农场', '王建国', '13800010001', '山东省', '潍坊市', '寿光市', '稻田镇现代农业产业园 8 号', 320.00, 1, '2026-05-12 20:07:00.769478', '2026-05-12 20:07:00.769478', 1), (17, 'BASE-QI-LETTUCE-001', '昆明晨露叶菜基地', '李秀兰', '13800010002', '云南省', '昆明市', '呈贡区', '斗南街道高效农业示范区 2 号', 185.00, 1, '2026-05-12 20:10:04.105942', '2026-05-12 20:10:04.105942', 1), (18, 'BASE-QI-RICE-001', '五常稻香标准化种植基地', '孙德昌', '13800010004', '黑龙江省', '哈尔滨市', '五常市', '民乐乡优质稻种植片区 6 号', 860.00, 1, '2026-05-12 20:30:34.998585', '2026-05-12 20:30:34.998585', 1), (19, 'BASE-QI-STRAWBERRY-001', '丹东莓香种植合作社', '赵海峰', '13800010003', '辽宁省', '丹东市', '东港市', '椅圈镇草莓采后处理中心', 210.00, 1, '2026-05-12 20:41:56.704317', '2026-05-12 20:41:56.704317', 1);
COMMIT;

-- ----------------------------
-- Table structure for company
-- ----------------------------
DROP TABLE IF EXISTS "company";
CREATE TABLE "company" (
  "id" int8 NOT NULL DEFAULT nextval('company_id_seq'::regclass),
  "company_code" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "company_name" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "status" int2 NOT NULL DEFAULT 1,
  "created_at" timestamp(6) NOT NULL DEFAULT pg_systimestamp(),
  "updated_at" timestamp(6) NOT NULL DEFAULT pg_systimestamp()
)
;

-- ----------------------------
-- Records of company
-- ----------------------------
BEGIN;
INSERT INTO "company" ("id", "company_code", "company_name", "status", "created_at", "updated_at") VALUES (1, 'CP-20260508-0001', '默认公司', 1, '2026-05-08 12:55:28.91352', '2026-05-08 12:55:28.91352');
COMMIT;

-- ----------------------------
-- Table structure for inspection_report
-- ----------------------------
DROP TABLE IF EXISTS "inspection_report";
CREATE TABLE "inspection_report" (
  "id" int8 NOT NULL DEFAULT nextval('inspection_report_id_seq'::regclass),
  "batch_id" int8 NOT NULL,
  "report_no" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "agency_name" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "inspector_name" varchar(64) COLLATE "pg_catalog"."default",
  "inspection_time" timestamp(6) NOT NULL,
  "result_status" int2 NOT NULL DEFAULT 1,
  "conclusion" text COLLATE "pg_catalog"."default",
  "report_url" varchar(255) COLLATE "pg_catalog"."default",
  "created_at" timestamp(6) NOT NULL DEFAULT pg_systimestamp()
)
;

-- ----------------------------
-- Records of inspection_report
-- ----------------------------
BEGIN;
INSERT INTO "inspection_report" ("id", "batch_id", "report_no", "agency_name", "inspector_name", "inspection_time", "result_status", "conclusion", "report_url", "created_at") VALUES (2, 3, 'XZcsdfgsdf', 'sda', '', '2026-04-15 00:00:00', 0, '', '', '2026-04-15 22:20:19.85777'), (6, 2, 'JC001', '绿源', '李芳', '2026-04-01 00:00:00', 1, '', '/uploads/1a76a4762114493da329e53757e300cf.png', '2026-04-27 14:25:40.564698'), (7, 4, 'JC002', '绿源', '张志龙', '2026-04-07 00:00:00', 1, '', '/uploads/5f7d8bd64b3041e6a09df58af8a055d2.png', '2026-04-27 14:26:23.363212'), (8, 1, 'JC003', '绿源', '王三运', '2026-04-22 00:00:00', 1, '', '/uploads/e3a78703735747c9b918dfe01b635295.png', '2026-04-27 14:27:12.92372'), (9, 7, '997', '绿源', '', '2026-04-27 00:00:00', 1, '', '/uploads/4d8c25de5aee48298f954c9f411c6951.png', '2026-04-27 17:29:17.429591'), (10, 9, 'IR-TEST-20260507-001', '华东农产品质量检测中心', '赵敏', '2026-05-07 09:30:00', 1, '样品感官、农残与微生物指标均符合要求，判定为合格。', 'report-pass-sample.md', '2026-05-07 10:26:25.27141'), (11, 10, 'IR-QI-TOM-20260512-001', '山东农检中心', '陈思远', '2026-05-12 11:30:00', 1, '农残、重金属与感官指标均符合标准，可正常销售。', 'report-pass-sample.md', '2026-05-12 20:07:00.83854'), (12, 11, 'IR-QI-LET-20260512-001', '云南绿色食品检测院', '郑雪', '2026-05-12 09:40:00', 1, '亚硝酸盐与农残指标合格，适合冷链流通。', 'report-pass-sample.md', '2026-05-12 20:10:04.15669'), (13, 12, 'IR-QI-RICE-20260512-001', '黑龙江粮油质量监督站', '吴海宁', '2026-05-12 13:30:00', 1, '水分、碎米率、重金属与富硒指标均符合标准。', 'report-pass-sample.md', '2026-05-12 20:30:35.058128'), (14, 13, 'IR-QI-STR-20260512-001', '辽宁省食品质量检验院', '冯超', '2026-05-12 09:00:00', 0, '复检发现个别包装箱冷链波动明显，存在品质衰减风险，建议立即复核并暂停流通。', 'report-fail-sample.md', '2026-05-12 20:41:56.76725');
COMMIT;

-- ----------------------------
-- Table structure for logistics_record
-- ----------------------------
DROP TABLE IF EXISTS "logistics_record";
CREATE TABLE "logistics_record" (
  "id" int8 NOT NULL DEFAULT nextval('logistics_record_id_seq'::regclass),
  "batch_id" int8 NOT NULL,
  "item_id" int8,
  "logistics_code" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "node_type" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "node_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "operation_time" timestamp(6) NOT NULL,
  "operator_name" varchar(64) COLLATE "pg_catalog"."default",
  "contact_phone" varchar(20) COLLATE "pg_catalog"."default",
  "location" varchar(255) COLLATE "pg_catalog"."default",
  "temperature" varchar(32) COLLATE "pg_catalog"."default",
  "humidity" varchar(32) COLLATE "pg_catalog"."default",
  "attachment_url" varchar(255) COLLATE "pg_catalog"."default",
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "created_at" timestamp(6) NOT NULL DEFAULT pg_systimestamp()
)
;

-- ----------------------------
-- Records of logistics_record
-- ----------------------------
BEGIN;
INSERT INTO "logistics_record" ("id", "batch_id", "item_id", "logistics_code", "node_type", "node_name", "operation_time", "operator_name", "contact_phone", "location", "temperature", "humidity", "attachment_url", "remark", "created_at") VALUES (2, 9, NULL, 'LG-20260507-0002', '仓储', '基地预冷库', '2026-05-07 08:30:00', '冷库管理员赵峰', '13800002222', '寿光基地 A 区预冷库', '3C', '85%', '', '采后预冷 2 小时', '2026-05-07 10:26:25.249843'), (3, 9, NULL, 'LG-20260507-0003', '运输', '冷链干线运输', '2026-05-07 11:00:00', '物流调度周强', '13800003333', '寿光基地 -> 济南分拨中心', '4C', '80%', '', '冷链车辆准点发车', '2026-05-07 10:26:25.260162'), (4, 9, NULL, 'LG-20260507-0004', '配送', '城市前置仓', '2026-05-07 17:40:00', '分拨专员韩琳', '13800004444', '济南历下区前置仓', '5C', '78%', '', '待末端配送', '2026-05-07 10:26:25.268502'), (1, 8, NULL, 'LG-20260506-0001', '入库', '仓库A', '2026-05-06 19:18:18', 'admin', NULL, '纬度 36.059592, 经度 103.735196, 精度约 61 米', NULL, NULL, NULL, '', '2026-05-06 19:18:43.312451'), (5, 10, NULL, 'LG-20260512-0005', '采后处理', '分拣包装中心出库', '2026-05-12 15:40:00', '王建国', '13800010001', '山东省潍坊市寿光市分拣包装中心', '12C', '68%', '', '完成分级装箱并贴码出库。', '2026-05-12 20:07:00.810433'), (6, 10, NULL, 'LG-20260512-0006', '干线运输', '潍坊至济南冷链运输', '2026-05-12 20:10:00', '徐永强', '13800011001', '山东省济南市历城区冷链途中', '8C', '72%', '', '车辆定位正常，中途未开厢。', '2026-05-12 20:07:00.820675'), (7, 10, NULL, 'LG-20260512-0007', '仓储', '济南区域仓入库', '2026-05-13 01:20:00', '赵彤', '13800011002', '山东省济南市历城区生鲜仓', '6C', '70%', '', '完成到仓抽样与入库扫描。', '2026-05-12 20:07:00.827421'), (8, 10, NULL, 'LG-20260512-0008', '门店配送', '社区门店上架', '2026-05-13 08:40:00', '郭峰', '13800011003', '山东省济南市市中区阳光社区店', '10C', '65%', '', '门店签收后完成陈列。', '2026-05-12 20:07:00.834704'), (9, 11, NULL, 'LG-20260512-0009', '采后处理', '预冷中心出库', '2026-05-12 07:20:00', '周小燕', '13800010002', '云南省昆明市呈贡区预冷中心', '4C', '78%', '', '采后预冷达标后装入保温周转箱。', '2026-05-12 20:10:04.133368'), (10, 11, NULL, 'LG-20260512-0010', '干线运输', '昆明至贵阳冷链运输', '2026-05-12 13:10:00', '韩磊', '13800012001', '贵州省贵阳市观山湖区运输途中', '5C', '76%', '', '全程冷机运行正常。', '2026-05-12 20:10:04.140457'), (11, 11, NULL, 'LG-20260512-0011', '仓储', '贵阳区域仓暂存', '2026-05-12 18:35:00', '蒋蓉', '13800012002', '贵州省贵阳市观山湖区生鲜仓', '3C', '80%', '', '完成到货抽检与短暂周转。', '2026-05-12 20:10:04.147608'), (12, 11, NULL, 'LG-20260512-0012', '商超配送', '连锁商超上架', '2026-05-13 06:45:00', '蒋蓉', '13800012002', '贵州省贵阳市南明区商超门店', '7C', '72%', '', '早市前完成上架补货。', '2026-05-12 20:10:04.15426'), (13, 12, NULL, 'LG-20260512-0013', '加工仓', '成品仓出库', '2026-05-12 18:10:00', '马洪涛', '13800010004', '黑龙江省哈尔滨市五常市成品仓', '18C', '50%', '', '打托缠膜后完成出库。', '2026-05-12 20:30:35.032884'), (14, 12, NULL, 'LG-20260512-0014', '干线运输', '五常至长春公路运输', '2026-05-13 04:30:00', '曹岩', '13800014001', '吉林省长春市二道区运输途中', '16C', '48%', '', '包装完整，无受潮风险。', '2026-05-12 20:30:35.039015'), (15, 12, NULL, 'LG-20260512-0015', '仓储', '长春区域仓入库', '2026-05-13 09:20:00', '陶欣', '13800014002', '吉林省长春市二道区民生仓储中心', '17C', '45%', '', '到仓称重正常，外箱完好。', '2026-05-12 20:30:35.045807'), (16, 12, NULL, 'LG-20260512-0016', '零售配送', '电商订单发货', '2026-05-14 10:00:00', '陶欣', '13800014002', '吉林省长春市净月区电商前置仓', '19C', '43%', '', '已完成订单分拨与面单绑定。', '2026-05-12 20:30:35.054663'), (17, 13, NULL, 'LG-20260512-0017', '采后处理', '预冷分拣中心出库', '2026-05-12 08:10:00', '于佳宁', '13800010003', '辽宁省丹东市东港市草莓预冷中心', '2C', '88%', '', '按等级分筐包装并打包贴码。', '2026-05-12 20:41:56.740319'), (18, 13, NULL, 'LG-20260512-0018', '干线运输', '丹东至沈阳冷链运输', '2026-05-12 12:50:00', '董鹏', '13800013001', '辽宁省沈阳市苏家屯区运输途中', '10C', '86%', '', '途中冷机出现异常波动，温度偏高。', '2026-05-12 20:41:56.748785'), (19, 13, NULL, 'LG-20260512-0019', '仓储', '沈阳中转冷库入库', '2026-05-12 17:40:00', '林爽', '13800013002', '辽宁省沈阳市苏家屯区中转冷库', '11C', '84%', '', '入库抽检发现部分箱体表面温度偏高。', '2026-05-12 20:41:56.757159'), (20, 13, NULL, 'LG-20260512-0020', '门店配送', '精品门店签收', '2026-05-13 07:30:00', '林爽', '13800013002', '辽宁省沈阳市和平区精品水果店', '12C', '82%', '', '签收后反馈部分果面状态不稳，触发复检。', '2026-05-12 20:41:56.76355');
COMMIT;

-- ----------------------------
-- Table structure for product_batch
-- ----------------------------
DROP TABLE IF EXISTS "product_batch";
CREATE TABLE "product_batch" (
  "id" int8 NOT NULL DEFAULT nextval('product_batch_id_seq'::regclass),
  "batch_code" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "base_id" int8 NOT NULL,
  "product_name" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "product_category" varchar(64) COLLATE "pg_catalog"."default",
  "planting_date" date,
  "expected_harvest_date" date,
  "actual_harvest_date" date,
  "quantity" numeric(12,2),
  "unit" varchar(16) COLLATE "pg_catalog"."default",
  "batch_status" int2 NOT NULL DEFAULT 1,
  "recall_status" int2 NOT NULL DEFAULT 0,
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "created_at" timestamp(6) NOT NULL DEFAULT pg_systimestamp(),
  "updated_at" timestamp(6) NOT NULL DEFAULT pg_systimestamp(),
  "company_id" int8
)
;

-- ----------------------------
-- Records of product_batch
-- ----------------------------
BEGIN;
INSERT INTO "product_batch" ("id", "batch_code", "base_id", "product_name", "product_category", "planting_date", "expected_harvest_date", "actual_harvest_date", "quantity", "unit", "batch_status", "recall_status", "remark", "created_at", "updated_at", "company_id") VALUES (1, 'BATCH-001', 1, '番茄', '农作物', '2025-04-08', '2026-04-23', '2027-04-15', 4.00, '', 2, 1, '', '2026-04-15 11:24:29.896889', '2026-04-15 15:29:29.448932', 1), (5, '54861', 8, '454', '', NULL, NULL, NULL, NULL, '', 2, 0, '', '2026-04-20 20:24:47.402219', '2026-04-20 20:24:47.402219', 1), (6, 'sefzxd', 10, 'xcb', '', NULL, NULL, NULL, NULL, '', 1, 0, '', '2026-04-20 21:34:38.924753', '2026-04-20 21:34:38.924753', 1), (3, 'test14', 3, '14', '', NULL, NULL, NULL, NULL, '', 2, 0, '', '2026-04-15 22:18:45.024038', '2026-04-20 21:35:18.295707', 1), (2, 'BATCH-212', 2, '猕猴桃', '水果', '2025-04-01', '2026-04-17', '2026-04-18', NULL, '', 2, 1, '', '2026-04-15 15:27:49.879177', '2026-04-20 21:38:47.90485', 1), (4, '3', 3, '桃子', '水果', '2025-04-08', '2026-04-06', '2026-04-17', NULL, '', 2, 0, '', '2026-04-16 11:10:44.175905', '2026-04-27 14:17:28.865295', 1), (7, '云南黄瓜产物', 13, '黄瓜', '蔬菜', NULL, NULL, NULL, NULL, '', 1, 1, '', '2026-04-27 17:27:09.423942', '2026-04-27 17:31:53.714967', 1), (9, 'BATCH-TEST-20260507-001', 15, '奶油草莓', '浆果', '2026-03-01', '2026-05-20', '2026-05-07', 1200.00, '盒', 1, 0, '测试导入用正常批次', '2026-05-07 10:26:25.219365', '2026-05-07 10:26:25.219365', 1), (8, 'BATCH-20260506-0001', 14, '草莓', '', '2024-05-01', '2026-05-05', '2026-05-05', 6.00, 'kg', 2, 1, '', '2026-05-06 18:43:19.304791', '2026-05-12 19:46:39.105849', 1), (10, 'BATCH-QI-TOM-20260512-001', 16, '圣女果', '茄果类', '2026-02-10', '2026-05-15', '2026-05-12', 5600.00, 'kg', 1, 0, '温室滴灌种植批次，适合演示标准全流程导入。', '2026-05-12 20:07:00.782367', '2026-05-12 20:07:00.782367', 1), (11, 'BATCH-QI-LET-20260512-001', 17, '生菜', '叶菜类', '2026-03-08', '2026-05-14', '2026-05-12', 4200.00, 'kg', 1, 0, '预冷后发往区域仓，适合测试冷链链路展示。', '2026-05-12 20:10:04.113897', '2026-05-12 20:10:04.113897', 1), (12, 'BATCH-QI-RICE-20260512-001', 18, '富硒大米', '粮食类', '2025-10-02', '2026-05-15', '2026-05-12', 18000.00, 'kg', 1, 0, '订单加工批次，适合演示收割、加工、仓配全链路。', '2026-05-12 20:30:35.008965', '2026-05-12 20:30:35.008965', 1), (13, 'BATCH-QI-STR-20260512-001', 19, '草莓', '浆果类', '2026-01-18', '2026-05-10', '2026-05-12', 3100.00, 'kg', 1, 0, '包含质检异常与冷链波动场景，用于测试风险任务自动生成。', '2026-05-12 20:41:56.711589', '2026-05-12 20:41:56.711589', 1);
COMMIT;

-- ----------------------------
-- Table structure for product_item
-- ----------------------------
DROP TABLE IF EXISTS "product_item";
CREATE TABLE "product_item" (
  "id" int8 NOT NULL DEFAULT nextval('product_item_id_seq'::regclass),
  "batch_id" int8 NOT NULL,
  "item_code" varchar(96) COLLATE "pg_catalog"."default" NOT NULL,
  "trace_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "qr_content" text COLLATE "pg_catalog"."default" NOT NULL,
  "sign_value" varchar(255) COLLATE "pg_catalog"."default",
  "item_status" int2 NOT NULL DEFAULT 1,
  "scan_count" int4 NOT NULL DEFAULT 0,
  "first_scanned_at" timestamp(6),
  "last_scanned_at" timestamp(6),
  "generated_at" timestamp(6) NOT NULL DEFAULT pg_systimestamp(),
  "updated_at" timestamp(6) NOT NULL DEFAULT pg_systimestamp()
)
;

-- ----------------------------
-- Records of product_item
-- ----------------------------
BEGIN;
INSERT INTO "product_item" ("id", "batch_id", "item_code", "trace_id", "qr_content", "sign_value", "item_status", "scan_count", "first_scanned_at", "last_scanned_at", "generated_at", "updated_at") VALUES (11, 9, 'ITEM-BATCHTEST20260507001-000001', '00671709e51843569b4e7de1a828827e', '/api/trace/00671709e51843569b4e7de1a828827e?sign=ce72c25e30d082b5823c13e2eae8098e15f62c7468fb943fa40c76f10c6b60db', 'ce72c25e30d082b5823c13e2eae8098e15f62c7468fb943fa40c76f10c6b60db', 1, 0, NULL, NULL, '2026-05-07 10:26:25.27759', '2026-05-07 10:26:25.27759'), (12, 9, 'ITEM-BATCHTEST20260507001-000002', '76fa0435eb86434ba3f10bdfd8455320', '/api/trace/76fa0435eb86434ba3f10bdfd8455320?sign=855ad9c96bac5bbc7d864d6cd282fc2e5880983a62e56b0b033ff4eca1ec9906', '855ad9c96bac5bbc7d864d6cd282fc2e5880983a62e56b0b033ff4eca1ec9906', 1, 0, NULL, NULL, '2026-05-07 10:26:25.284152', '2026-05-07 10:26:25.284152'), (13, 9, 'ITEM-BATCHTEST20260507001-000003', '6eb2a2f99596492fbfeabeb87a3e18da', '/api/trace/6eb2a2f99596492fbfeabeb87a3e18da?sign=d6be3aa6b9b8ca6b121e8293bb3399f3d9c780f7fe8e00a4e9ed8b1922c54c70', 'd6be3aa6b9b8ca6b121e8293bb3399f3d9c780f7fe8e00a4e9ed8b1922c54c70', 1, 0, NULL, NULL, '2026-05-07 10:26:25.286814', '2026-05-07 10:26:25.286814'), (14, 9, 'ITEM-BATCHTEST20260507001-000004', '71dd2ea5221a4849a0d9e1e70ced0ae3', '/api/trace/71dd2ea5221a4849a0d9e1e70ced0ae3?sign=2713106d00ccb925e21dc7a0a2d802bf841dc1d0f0deef7646d1dd6c2e8ac572', '2713106d00ccb925e21dc7a0a2d802bf841dc1d0f0deef7646d1dd6c2e8ac572', 1, 0, NULL, NULL, '2026-05-07 10:26:25.289538', '2026-05-07 10:26:25.289538'), (15, 9, 'ITEM-BATCHTEST20260507001-000005', 'f587fd41c4514173876d1e2d90f3728e', '/api/trace/f587fd41c4514173876d1e2d90f3728e?sign=473b587e740d8b8c9151f822b5d0ef9d52ec7d93455c110cab4139a4da37a340', '473b587e740d8b8c9151f822b5d0ef9d52ec7d93455c110cab4139a4da37a340', 1, 0, NULL, NULL, '2026-05-07 10:26:25.292427', '2026-05-07 10:26:25.292427'), (16, 9, 'ITEM-BATCHTEST20260507001-000006', 'caf13f80feb04137861655bef848d76e', '/api/trace/caf13f80feb04137861655bef848d76e?sign=76b8d7a3eb16209263017b99eaef6024cbfff354869f783584f43cd06fe083c5', '76b8d7a3eb16209263017b99eaef6024cbfff354869f783584f43cd06fe083c5', 1, 0, NULL, NULL, '2026-05-07 10:26:25.295028', '2026-05-07 10:26:25.295028'), (17, 9, 'ITEM-BATCHTEST20260507001-000007', 'bcc35d2e0d764d049c05cae0a2eadaed', '/api/trace/bcc35d2e0d764d049c05cae0a2eadaed?sign=7a7006f4d363db9135617d16b89c1817fa63ab0bfdc365d91347cef71bb26f91', '7a7006f4d363db9135617d16b89c1817fa63ab0bfdc365d91347cef71bb26f91', 1, 0, NULL, NULL, '2026-05-07 10:26:25.297809', '2026-05-07 10:26:25.297809'), (18, 9, 'ITEM-BATCHTEST20260507001-000008', '92717e3f3df74b5b83d4a2c4952d31d9', '/api/trace/92717e3f3df74b5b83d4a2c4952d31d9?sign=c4f9b83458934bd0136200cec0c95bb2637337798b0b2123aba9acecfc9ac55c', 'c4f9b83458934bd0136200cec0c95bb2637337798b0b2123aba9acecfc9ac55c', 1, 0, NULL, NULL, '2026-05-07 10:26:25.299684', '2026-05-07 10:26:25.299684'), (19, 9, 'ITEM-BATCHTEST20260507001-000009', '468c6784017e46f6982e442528bb06e3', '/api/trace/468c6784017e46f6982e442528bb06e3?sign=8257584ff9b72261484117d64cc2d4d576b62f05c16fd976799e7a060a4ded6f', '8257584ff9b72261484117d64cc2d4d576b62f05c16fd976799e7a060a4ded6f', 1, 0, NULL, NULL, '2026-05-07 10:26:25.302101', '2026-05-07 10:26:25.302101'), (20, 9, 'ITEM-BATCHTEST20260507001-000010', '2661c836970e4040adc6823a9b9fdb2a', '/api/trace/2661c836970e4040adc6823a9b9fdb2a?sign=d82472f11309f4c5e51eb3c4b193af447390921d7ed25f62f1ef9001248e05f9', 'd82472f11309f4c5e51eb3c4b193af447390921d7ed25f62f1ef9001248e05f9', 1, 0, NULL, NULL, '2026-05-07 10:26:25.304435', '2026-05-07 10:26:25.304435'), (21, 9, 'ITEM-BATCHTEST20260507001-000011', '9becfaaf3b4845b49991e627889a80ce', '/api/trace/9becfaaf3b4845b49991e627889a80ce?sign=0975f7d0dd4472bac820ff9d49df291a31a2e7fff3013ffe45a16ecccfa18a54', '0975f7d0dd4472bac820ff9d49df291a31a2e7fff3013ffe45a16ecccfa18a54', 1, 0, NULL, NULL, '2026-05-07 10:26:25.306798', '2026-05-07 10:26:25.306798'), (22, 9, 'ITEM-BATCHTEST20260507001-000012', '8bdb70574ef14fdfb2f71b6b37dea586', '/api/trace/8bdb70574ef14fdfb2f71b6b37dea586?sign=fb612be0c07d61292176eb88f23cfa0d8cc499b42313b7f82f142eff2f1cb8d2', 'fb612be0c07d61292176eb88f23cfa0d8cc499b42313b7f82f142eff2f1cb8d2', 1, 0, NULL, NULL, '2026-05-07 10:26:25.30896', '2026-05-07 10:26:25.30896'), (23, 9, 'ITEM-BATCHTEST20260507001-000013', '70fac121e74943d6ac81650b8cd9e9b8', '/api/trace/70fac121e74943d6ac81650b8cd9e9b8?sign=06110998aaa3c46f7d753e14fb9a23a08c002f01c5d7d6c7b48cec42c16376a8', '06110998aaa3c46f7d753e14fb9a23a08c002f01c5d7d6c7b48cec42c16376a8', 1, 0, NULL, NULL, '2026-05-07 10:26:25.311216', '2026-05-07 10:26:25.311216'), (9, 8, 'ITEM-BATCH202605060001-000009', '17a206a190604199885eca508b769183', '/api/trace/17a206a190604199885eca508b769183?sign=ae70c25fd5105ce77afd355869606955880313f6a0589ea9befb508b75bb3d2d', 'ae70c25fd5105ce77afd355869606955880313f6a0589ea9befb508b75bb3d2d', 2, 0, NULL, NULL, '2026-05-06 18:51:51.983852', '2026-05-12 19:46:39.109777'), (8, 8, 'ITEM-BATCH202605060001-000008', 'bbd869b1429243998896b760b3971bcf', '/api/trace/bbd869b1429243998896b760b3971bcf?sign=82a974875678b63942fee91ed683dd0be33b38e0a7c4ee5c240de4aa2b13b26a', '82a974875678b63942fee91ed683dd0be33b38e0a7c4ee5c240de4aa2b13b26a', 2, 0, NULL, NULL, '2026-05-06 18:51:51.982471', '2026-05-12 19:46:39.109777'), (7, 8, 'ITEM-BATCH202605060001-000007', 'a151f47896e1450b83e6905e62fc6410', '/api/trace/a151f47896e1450b83e6905e62fc6410?sign=8491366a2205de9966f53bbe9b288ac905025b506b1b62a974655d3db97184e2', '8491366a2205de9966f53bbe9b288ac905025b506b1b62a974655d3db97184e2', 2, 0, NULL, NULL, '2026-05-06 18:51:51.98088', '2026-05-12 19:46:39.109777'), (24, 9, 'ITEM-BATCHTEST20260507001-000014', '9629a3e723f94f11898eb758dadc6faa', '/api/trace/9629a3e723f94f11898eb758dadc6faa?sign=6c16c648ff39c2b9e76799b8f55879d1c50e8a7820e59cd86ced45f3e51ae3ae', '6c16c648ff39c2b9e76799b8f55879d1c50e8a7820e59cd86ced45f3e51ae3ae', 1, 0, NULL, NULL, '2026-05-07 10:26:25.313084', '2026-05-07 10:26:25.313084'), (25, 9, 'ITEM-BATCHTEST20260507001-000015', '486c32c2f5e64bb5852abfb64a3ddf5b', '/api/trace/486c32c2f5e64bb5852abfb64a3ddf5b?sign=3139f25367825321cba01d4f2c9339856fc8e51fdc088ae1231168cad06b6fe9', '3139f25367825321cba01d4f2c9339856fc8e51fdc088ae1231168cad06b6fe9', 1, 0, NULL, NULL, '2026-05-07 10:26:25.315787', '2026-05-07 10:26:25.315787'), (26, 9, 'ITEM-BATCHTEST20260507001-000016', '6bb211f10ce44702aa390503f8877ac2', '/api/trace/6bb211f10ce44702aa390503f8877ac2?sign=89f8762c456e4c246efad4eb5d181f46418e2f42f8410cadfce82e9a33125ff3', '89f8762c456e4c246efad4eb5d181f46418e2f42f8410cadfce82e9a33125ff3', 1, 0, NULL, NULL, '2026-05-07 10:26:25.318476', '2026-05-07 10:26:25.318476'), (27, 9, 'ITEM-BATCHTEST20260507001-000017', '46a87e08f3c4484ab9b446344aefbf2d', '/api/trace/46a87e08f3c4484ab9b446344aefbf2d?sign=6f5c26d79e665b162462cd7bf7b4ea6384452b9212fa36bd184313e4b612a4bc', '6f5c26d79e665b162462cd7bf7b4ea6384452b9212fa36bd184313e4b612a4bc', 1, 0, NULL, NULL, '2026-05-07 10:26:25.320455', '2026-05-07 10:26:25.320455'), (28, 9, 'ITEM-BATCHTEST20260507001-000018', 'c9d513ef2edb4fb6a9b23ed60186d015', '/api/trace/c9d513ef2edb4fb6a9b23ed60186d015?sign=cc65630f4e1aa0b9425c83d014a892764df7bc015343262767cc1a4b889b0395', 'cc65630f4e1aa0b9425c83d014a892764df7bc015343262767cc1a4b889b0395', 1, 0, NULL, NULL, '2026-05-07 10:26:25.322571', '2026-05-07 10:26:25.322571'), (29, 9, 'ITEM-BATCHTEST20260507001-000019', '8419cd724984467e925c15faa69440ea', '/api/trace/8419cd724984467e925c15faa69440ea?sign=1f6c59edf2daaeed9b837467db53bca6d23834e5b7a84186e1c766c15d181e10', '1f6c59edf2daaeed9b837467db53bca6d23834e5b7a84186e1c766c15d181e10', 1, 0, NULL, NULL, '2026-05-07 10:26:25.324212', '2026-05-07 10:26:25.324212'), (30, 9, 'ITEM-BATCHTEST20260507001-000020', '54b2440832694c26985e680144e76756', '/api/trace/54b2440832694c26985e680144e76756?sign=a91a0cba0459175b0c7fe0b75eb18361cdb177e6384f541149561dffc5ad959b', 'a91a0cba0459175b0c7fe0b75eb18361cdb177e6384f541149561dffc5ad959b', 1, 0, NULL, NULL, '2026-05-07 10:26:25.325944', '2026-05-07 10:26:25.325944'), (1, 8, 'ITEM-BATCH202605060001-000001', '2154a85bc9264a4e8dfa76848143ad4c', '/api/trace/2154a85bc9264a4e8dfa76848143ad4c?sign=ff768568c156f4f74d6a4900b90796679b84388fecb09bad33d79073d2f7eb4d', 'ff768568c156f4f74d6a4900b90796679b84388fecb09bad33d79073d2f7eb4d', 2, 0, NULL, NULL, '2026-05-06 18:51:51.965812', '2026-05-12 19:46:39.109777'), (2, 8, 'ITEM-BATCH202605060001-000002', 'b0123486018f493981bfafd4e9938973', '/api/trace/b0123486018f493981bfafd4e9938973?sign=ec491ec5523a9ce63071d80ba4d73fd7a7f9a2713d9c1cfd018524bf30984931', 'ec491ec5523a9ce63071d80ba4d73fd7a7f9a2713d9c1cfd018524bf30984931', 2, 0, NULL, NULL, '2026-05-06 18:51:51.970332', '2026-05-12 19:46:39.109777'), (3, 8, 'ITEM-BATCH202605060001-000003', '4b7831e3222141c3b18346c8aa0d98c7', '/api/trace/4b7831e3222141c3b18346c8aa0d98c7?sign=21a4f884743ee3a9d054b09af59eb60d6ef6c162e29109930014ca8bfbd4be44', '21a4f884743ee3a9d054b09af59eb60d6ef6c162e29109930014ca8bfbd4be44', 2, 0, NULL, NULL, '2026-05-06 18:51:51.973056', '2026-05-12 19:46:39.109777'), (4, 8, 'ITEM-BATCH202605060001-000004', 'd61ce15f0fb740d59fc4a485efb7630d', '/api/trace/d61ce15f0fb740d59fc4a485efb7630d?sign=57a7e9472f10bf8535792c6ba761fd50dc8188d5ed52eb95ea9462eaab5cb101', '57a7e9472f10bf8535792c6ba761fd50dc8188d5ed52eb95ea9462eaab5cb101', 2, 0, NULL, NULL, '2026-05-06 18:51:51.975258', '2026-05-12 19:46:39.109777'), (5, 8, 'ITEM-BATCH202605060001-000005', 'a6c927921a2a4e1eb494066ba8c5d4f6', '/api/trace/a6c927921a2a4e1eb494066ba8c5d4f6?sign=5c1a4061f3a474b178e103b6a23f898dec9f923c85c00a2d336a4b2c17af0c8a', '5c1a4061f3a474b178e103b6a23f898dec9f923c85c00a2d336a4b2c17af0c8a', 2, 0, NULL, NULL, '2026-05-06 18:51:51.977199', '2026-05-12 19:46:39.109777'), (6, 8, 'ITEM-BATCH202605060001-000006', 'f336afb80626435d80b2fbdac236877f', '/api/trace/f336afb80626435d80b2fbdac236877f?sign=ea4f5d3d8dcec229a245b1a2164f5f40752f1e6bd5b7a4f2309dce0a8d8d963c', 'ea4f5d3d8dcec229a245b1a2164f5f40752f1e6bd5b7a4f2309dce0a8d8d963c', 2, 0, NULL, NULL, '2026-05-06 18:51:51.978844', '2026-05-12 19:46:39.109777'), (10, 8, 'ITEM-BATCH202605060001-000010', '521435a02e1a45beb2962bd5c780d289', '/api/trace/521435a02e1a45beb2962bd5c780d289?sign=f6238096d45fe4e7887f120112152010459a29be52fa0d4ccf3699828062d644', 'f6238096d45fe4e7887f120112152010459a29be52fa0d4ccf3699828062d644', 2, 24, '2026-05-06 19:07:46.109395', '2026-05-12 20:45:22.911607', '2026-05-06 18:51:51.985618', '2026-05-12 20:45:22.911607'), (31, 7, 'ITEM--000001', '0fb5f9119bc545448da031091dc1df57', '/api/trace/0fb5f9119bc545448da031091dc1df57?sign=e6ceeedc30288fe0234cfb4b96de83747886bcf7a7efd6952e0c6581087d041e', 'e6ceeedc30288fe0234cfb4b96de83747886bcf7a7efd6952e0c6581087d041e', 2, 0, NULL, NULL, '2026-05-12 19:45:00.20927', '2026-05-12 19:45:00.20927'), (32, 7, 'ITEM--000002', '010c65e7ef8d468c8df3d5c9e894f29e', '/api/trace/010c65e7ef8d468c8df3d5c9e894f29e?sign=4c28f51f3e33264a92333762a5b242e481db70c1ec1daa24ed27d3b3c9913126', '4c28f51f3e33264a92333762a5b242e481db70c1ec1daa24ed27d3b3c9913126', 2, 0, NULL, NULL, '2026-05-12 19:45:00.231931', '2026-05-12 19:45:00.231931'), (33, 7, 'ITEM--000003', '65f22096b1954e4781c9d282e8a7e5e5', '/api/trace/65f22096b1954e4781c9d282e8a7e5e5?sign=6a8bbf2416e269aab9446d5896c0783fd55f0e54ea6d7d3460a194d90a5eebd7', '6a8bbf2416e269aab9446d5896c0783fd55f0e54ea6d7d3460a194d90a5eebd7', 2, 0, NULL, NULL, '2026-05-12 19:45:00.236221', '2026-05-12 19:45:00.236221'), (34, 7, 'ITEM--000004', 'ae18ee1d911e4e28a7b0af172e68184c', '/api/trace/ae18ee1d911e4e28a7b0af172e68184c?sign=d9795df98ae5dfde0257a441dfe76766fe0ab81725f2ea03faefb4f82005fd2a', 'd9795df98ae5dfde0257a441dfe76766fe0ab81725f2ea03faefb4f82005fd2a', 2, 0, NULL, NULL, '2026-05-12 19:45:00.239654', '2026-05-12 19:45:00.239654'), (35, 7, 'ITEM--000005', '14d0e4e5411b4b748e7397021dbc3508', '/api/trace/14d0e4e5411b4b748e7397021dbc3508?sign=8789b2b2c6b4c2bbd77e02691be6a46e850a03475463365f2fac52f1af64c866', '8789b2b2c6b4c2bbd77e02691be6a46e850a03475463365f2fac52f1af64c866', 2, 0, NULL, NULL, '2026-05-12 19:45:00.242903', '2026-05-12 19:45:00.242903'), (36, 7, 'ITEM--000006', 'b2667f9d9d574a9cb87c9ee31aea779c', '/api/trace/b2667f9d9d574a9cb87c9ee31aea779c?sign=bf393e7e9f534fa2c15e384a7dcddbb568c1c7cfec805a3f3de82ba2d40a6a94', 'bf393e7e9f534fa2c15e384a7dcddbb568c1c7cfec805a3f3de82ba2d40a6a94', 2, 0, NULL, NULL, '2026-05-12 19:45:00.245329', '2026-05-12 19:45:00.245329'), (37, 7, 'ITEM--000007', 'd433c03eb8784b6f8b2f024510ae1a71', '/api/trace/d433c03eb8784b6f8b2f024510ae1a71?sign=2c111c1bee7323ef8bce8955f87d877398c2b8236f174b58c32e4f6487e4a2da', '2c111c1bee7323ef8bce8955f87d877398c2b8236f174b58c32e4f6487e4a2da', 2, 0, NULL, NULL, '2026-05-12 19:45:00.248505', '2026-05-12 19:45:00.248505'), (38, 7, 'ITEM--000008', '468d27d4261549b4969345b027e12545', '/api/trace/468d27d4261549b4969345b027e12545?sign=dd5bcd0627f9a0bcd7c66f7bcb5b8f3dce0093a23c900a5677de63f033165852', 'dd5bcd0627f9a0bcd7c66f7bcb5b8f3dce0093a23c900a5677de63f033165852', 2, 0, NULL, NULL, '2026-05-12 19:45:00.251593', '2026-05-12 19:45:00.251593'), (39, 7, 'ITEM--000009', 'e9025f4c5d184d99b993825f1d941bc4', '/api/trace/e9025f4c5d184d99b993825f1d941bc4?sign=e12e87a584053c2d362e9567243fd32de97e0576ba3e9a004e8b7408f6ffcc21', 'e12e87a584053c2d362e9567243fd32de97e0576ba3e9a004e8b7408f6ffcc21', 2, 0, NULL, NULL, '2026-05-12 19:45:00.25503', '2026-05-12 19:45:00.25503'), (40, 7, 'ITEM--000010', 'c7aa0540d876406ead0513da9a9d050c', '/api/trace/c7aa0540d876406ead0513da9a9d050c?sign=280a6e1b0498bbeb57684a2b5415e142f082427465e9855b0f3bc17efb5de285', '280a6e1b0498bbeb57684a2b5415e142f082427465e9855b0f3bc17efb5de285', 2, 0, NULL, NULL, '2026-05-12 19:45:00.25859', '2026-05-12 19:45:00.25859'), (41, 6, 'ITEM-sefzxd-000001', 'a2396f057b0840cfa980f824d15844d9', '/api/trace/a2396f057b0840cfa980f824d15844d9?sign=f209446c467ff75c28d86658ff5657a165f0d13f4aa63884701d4405db2f46dd', 'f209446c467ff75c28d86658ff5657a165f0d13f4aa63884701d4405db2f46dd', 1, 0, NULL, NULL, '2026-05-12 19:45:03.825823', '2026-05-12 19:45:03.825823'), (42, 6, 'ITEM-sefzxd-000002', '10123833c8524d84a194d1ab50a9649a', '/api/trace/10123833c8524d84a194d1ab50a9649a?sign=fb80353ed677a7a5ca27a107974966632a16b0fa429691db5c3fb10099aba7f2', 'fb80353ed677a7a5ca27a107974966632a16b0fa429691db5c3fb10099aba7f2', 1, 0, NULL, NULL, '2026-05-12 19:45:03.829307', '2026-05-12 19:45:03.829307'), (43, 6, 'ITEM-sefzxd-000003', '6a8b8c9c8d1f44edbb4f03aec429e769', '/api/trace/6a8b8c9c8d1f44edbb4f03aec429e769?sign=b26b9db4088a9c8578d86a7801718214a19add3fd222fe7f175fac9b78c63878', 'b26b9db4088a9c8578d86a7801718214a19add3fd222fe7f175fac9b78c63878', 1, 0, NULL, NULL, '2026-05-12 19:45:03.83333', '2026-05-12 19:45:03.83333'), (44, 6, 'ITEM-sefzxd-000004', '31f542ea152748b9afa005e4655c3117', '/api/trace/31f542ea152748b9afa005e4655c3117?sign=f7104a37caec62cb14313e63533e08011b11bf4c42b9a2213b71010bfc40108d', 'f7104a37caec62cb14313e63533e08011b11bf4c42b9a2213b71010bfc40108d', 1, 0, NULL, NULL, '2026-05-12 19:45:03.837216', '2026-05-12 19:45:03.837216'), (45, 6, 'ITEM-sefzxd-000005', 'adce73a468224a7ea965b284fd40552f', '/api/trace/adce73a468224a7ea965b284fd40552f?sign=1c80ac0078662ed86f5cf205e49566fbb6730df1f1386f8c0a046eddd4bac201', '1c80ac0078662ed86f5cf205e49566fbb6730df1f1386f8c0a046eddd4bac201', 1, 0, NULL, NULL, '2026-05-12 19:45:03.840492', '2026-05-12 19:45:03.840492'), (46, 6, 'ITEM-sefzxd-000006', '6fd2fbfe07e2467e8f3af1e43a65ebb1', '/api/trace/6fd2fbfe07e2467e8f3af1e43a65ebb1?sign=35e6011a544138e8f8f0bbb5bc9270ef1688eac6d51e0a3ecde2797e5295b91e', '35e6011a544138e8f8f0bbb5bc9270ef1688eac6d51e0a3ecde2797e5295b91e', 1, 0, NULL, NULL, '2026-05-12 19:45:03.843067', '2026-05-12 19:45:03.843067'), (47, 6, 'ITEM-sefzxd-000007', '5fc50fe52aa64ad8b9e89b3c2e528603', '/api/trace/5fc50fe52aa64ad8b9e89b3c2e528603?sign=e8a09dd09372e93efca11f73de23574519da2c24ad6d5c48903e5458c07088df', 'e8a09dd09372e93efca11f73de23574519da2c24ad6d5c48903e5458c07088df', 1, 0, NULL, NULL, '2026-05-12 19:45:03.845545', '2026-05-12 19:45:03.845545'), (48, 6, 'ITEM-sefzxd-000008', '7e5461a701a7496b8e52749a490e677e', '/api/trace/7e5461a701a7496b8e52749a490e677e?sign=ca5a609fff3f35ae4b19f54e0789ae965e12830493040f80d6ec1c12d20fe663', 'ca5a609fff3f35ae4b19f54e0789ae965e12830493040f80d6ec1c12d20fe663', 1, 0, NULL, NULL, '2026-05-12 19:45:03.849376', '2026-05-12 19:45:03.849376'), (49, 6, 'ITEM-sefzxd-000009', '5d6f6aa727ed4cfb80d169a1b8f02641', '/api/trace/5d6f6aa727ed4cfb80d169a1b8f02641?sign=00da6dd5a1fc574936d1af0d69d029ceed1ff98c4d32cf25936e5e7dcd04c0aa', '00da6dd5a1fc574936d1af0d69d029ceed1ff98c4d32cf25936e5e7dcd04c0aa', 1, 0, NULL, NULL, '2026-05-12 19:45:03.852469', '2026-05-12 19:45:03.852469'), (50, 6, 'ITEM-sefzxd-000010', '74e04c27fcd54c228a8d2d770f25d772', '/api/trace/74e04c27fcd54c228a8d2d770f25d772?sign=915832d88fb5c103b2e8e0f52736f38a7c403c75e5da5090cea646efaa4b1f1b', '915832d88fb5c103b2e8e0f52736f38a7c403c75e5da5090cea646efaa4b1f1b', 1, 0, NULL, NULL, '2026-05-12 19:45:03.866845', '2026-05-12 19:45:03.866845'), (51, 5, 'ITEM-54861-000001', '5bc4ce2b2b61467bbc7813933231fe3a', '/api/trace/5bc4ce2b2b61467bbc7813933231fe3a?sign=47b09017ce947a74fa0037fd2bb565041cabf4847f3e0c855c7586252ec8a963', '47b09017ce947a74fa0037fd2bb565041cabf4847f3e0c855c7586252ec8a963', 1, 0, NULL, NULL, '2026-05-12 19:45:07.170659', '2026-05-12 19:45:07.170659'), (52, 5, 'ITEM-54861-000002', '43821f7f02944553833577cc8da35c47', '/api/trace/43821f7f02944553833577cc8da35c47?sign=4b52a67714942c512d0ed010d66a21ea0c03eaa576a6edf053aea666123b915c', '4b52a67714942c512d0ed010d66a21ea0c03eaa576a6edf053aea666123b915c', 1, 0, NULL, NULL, '2026-05-12 19:45:07.173413', '2026-05-12 19:45:07.173413'), (53, 5, 'ITEM-54861-000003', 'b8155ff8d31142e8a0cc154b8371dca5', '/api/trace/b8155ff8d31142e8a0cc154b8371dca5?sign=b4444822b5adaf527f993a03bfd5f062a47f795cb764990ab4e124b387c32826', 'b4444822b5adaf527f993a03bfd5f062a47f795cb764990ab4e124b387c32826', 1, 0, NULL, NULL, '2026-05-12 19:45:07.176084', '2026-05-12 19:45:07.176084'), (54, 5, 'ITEM-54861-000004', '997ce8b4b97d474dbf05d3e2094e1cd4', '/api/trace/997ce8b4b97d474dbf05d3e2094e1cd4?sign=fa41f9319b3defa414a2f2352bba5e9975f8a8c7ccd2bdcb9e0a807675feba68', 'fa41f9319b3defa414a2f2352bba5e9975f8a8c7ccd2bdcb9e0a807675feba68', 1, 0, NULL, NULL, '2026-05-12 19:45:07.178708', '2026-05-12 19:45:07.178708'), (55, 5, 'ITEM-54861-000005', '2af11e0f667c4c29b2e919d4ed3ee9cb', '/api/trace/2af11e0f667c4c29b2e919d4ed3ee9cb?sign=f0fce024571f7e68329f3973fcd68e7563506daa3fbc78637df4a7e88fafd55b', 'f0fce024571f7e68329f3973fcd68e7563506daa3fbc78637df4a7e88fafd55b', 1, 0, NULL, NULL, '2026-05-12 19:45:07.181807', '2026-05-12 19:45:07.181807'), (56, 5, 'ITEM-54861-000006', 'f0d6bbbe74d945559c6bd3c8bb275b58', '/api/trace/f0d6bbbe74d945559c6bd3c8bb275b58?sign=a46151ab5e72641071d4a645376f7bdf16e56c6415092974a4f3708e9f95bfbc', 'a46151ab5e72641071d4a645376f7bdf16e56c6415092974a4f3708e9f95bfbc', 1, 0, NULL, NULL, '2026-05-12 19:45:07.185342', '2026-05-12 19:45:07.185342'), (57, 5, 'ITEM-54861-000007', '5ea9a8f816864a7caecc621c5d0a750c', '/api/trace/5ea9a8f816864a7caecc621c5d0a750c?sign=264532af5ebf5ecc1d5fb53ad2d6d5a75c1a0e8628be8a04ce297f0c3b9032ce', '264532af5ebf5ecc1d5fb53ad2d6d5a75c1a0e8628be8a04ce297f0c3b9032ce', 1, 0, NULL, NULL, '2026-05-12 19:45:07.188065', '2026-05-12 19:45:07.188065'), (58, 5, 'ITEM-54861-000008', '5881ef151df545a3b70874c166d8e89f', '/api/trace/5881ef151df545a3b70874c166d8e89f?sign=2bb914359e2fd6a2ddf8ff37ab725f6220f3e7b95f8af26dcba6916c506b0c28', '2bb914359e2fd6a2ddf8ff37ab725f6220f3e7b95f8af26dcba6916c506b0c28', 1, 0, NULL, NULL, '2026-05-12 19:45:07.190692', '2026-05-12 19:45:07.190692'), (59, 5, 'ITEM-54861-000009', '0958ba42f1a34b6491f0d89f56f71aeb', '/api/trace/0958ba42f1a34b6491f0d89f56f71aeb?sign=fd323b464e3eae90827a40834441d8328ee599590d79509e94512f5a8338818b', 'fd323b464e3eae90827a40834441d8328ee599590d79509e94512f5a8338818b', 1, 0, NULL, NULL, '2026-05-12 19:45:07.193394', '2026-05-12 19:45:07.193394'), (60, 5, 'ITEM-54861-000010', 'fe66ecf91210420587b1f49e3c710614', '/api/trace/fe66ecf91210420587b1f49e3c710614?sign=154dbf29700fa8282eccf6ea7682c79b11d5732d77f8974175e6d49f65c06ce4', '154dbf29700fa8282eccf6ea7682c79b11d5732d77f8974175e6d49f65c06ce4', 1, 0, NULL, NULL, '2026-05-12 19:45:07.19567', '2026-05-12 19:45:07.19567'), (61, 5, 'ITEM-54861-000011', '7173c732660140d9bb9282e0e6b40887', '/api/trace/7173c732660140d9bb9282e0e6b40887?sign=f3bc2d19ec569d6c147e5692ab25f20e78e7577d19d504331755c8388b2b5218', 'f3bc2d19ec569d6c147e5692ab25f20e78e7577d19d504331755c8388b2b5218', 1, 0, NULL, NULL, '2026-05-12 19:45:07.198133', '2026-05-12 19:45:07.198133'), (62, 5, 'ITEM-54861-000012', '35235ab79e064ac1a090e8e95326894b', '/api/trace/35235ab79e064ac1a090e8e95326894b?sign=985aad1ab07ce2bf11ef95c70759796f4c83c84044c75e590fdbe2ac6bd0c174', '985aad1ab07ce2bf11ef95c70759796f4c83c84044c75e590fdbe2ac6bd0c174', 1, 0, NULL, NULL, '2026-05-12 19:45:07.200577', '2026-05-12 19:45:07.200577'), (63, 5, 'ITEM-54861-000013', 'b93b1d068b544479a58272bdf4c3bb84', '/api/trace/b93b1d068b544479a58272bdf4c3bb84?sign=24b0ffc0c2d57952f9700ee5ec626317d7987980d7cee51c1e8cf50447794b61', '24b0ffc0c2d57952f9700ee5ec626317d7987980d7cee51c1e8cf50447794b61', 1, 0, NULL, NULL, '2026-05-12 19:45:07.203827', '2026-05-12 19:45:07.203827'), (64, 5, 'ITEM-54861-000014', '83f539d9d0214e4187681b8ee67e89fe', '/api/trace/83f539d9d0214e4187681b8ee67e89fe?sign=ea325d9d82f671cea0024c5b2ef9914cd204cd5d97e721325ddfe77c3727ed88', 'ea325d9d82f671cea0024c5b2ef9914cd204cd5d97e721325ddfe77c3727ed88', 1, 0, NULL, NULL, '2026-05-12 19:45:07.206488', '2026-05-12 19:45:07.206488'), (65, 5, 'ITEM-54861-000015', 'd51d8d8b421c4f4aa63d72d5e154d65a', '/api/trace/d51d8d8b421c4f4aa63d72d5e154d65a?sign=498888047dae1d447010e858d32d53fbebafc28545c2fe8557e4c584ce11890e', '498888047dae1d447010e858d32d53fbebafc28545c2fe8557e4c584ce11890e', 1, 0, NULL, NULL, '2026-05-12 19:45:07.209158', '2026-05-12 19:45:07.209158'), (66, 5, 'ITEM-54861-000016', '4549f5d9905e4522af68002eb432a9b6', '/api/trace/4549f5d9905e4522af68002eb432a9b6?sign=1c66453f81d1d1533ba85ba225d72dfdf4e48baf00030e66ecce42f8ce95944f', '1c66453f81d1d1533ba85ba225d72dfdf4e48baf00030e66ecce42f8ce95944f', 1, 0, NULL, NULL, '2026-05-12 19:45:07.211859', '2026-05-12 19:45:07.211859'), (67, 5, 'ITEM-54861-000017', '9b5feaba80074acf8a285bc4977b2aec', '/api/trace/9b5feaba80074acf8a285bc4977b2aec?sign=1b55dc725f17581e551b05a0ec0590d8b569b5a38eacd77b137de532b642e2ae', '1b55dc725f17581e551b05a0ec0590d8b569b5a38eacd77b137de532b642e2ae', 1, 0, NULL, NULL, '2026-05-12 19:45:07.21424', '2026-05-12 19:45:07.21424'), (68, 5, 'ITEM-54861-000018', '8bd1749570524474b2b37c38798d18e4', '/api/trace/8bd1749570524474b2b37c38798d18e4?sign=3e6add8010b8dc0b7d01d0bb13c881a131e32ea01a5413fc1805b51685428f3d', '3e6add8010b8dc0b7d01d0bb13c881a131e32ea01a5413fc1805b51685428f3d', 1, 0, NULL, NULL, '2026-05-12 19:45:07.216827', '2026-05-12 19:45:07.216827'), (69, 5, 'ITEM-54861-000019', '5beee21faf5d497ba34e6fccc1eb82b1', '/api/trace/5beee21faf5d497ba34e6fccc1eb82b1?sign=2f6661539e72c359cef953bd589fdd09474b6364a1559f529e8456dda38c11e1', '2f6661539e72c359cef953bd589fdd09474b6364a1559f529e8456dda38c11e1', 1, 0, NULL, NULL, '2026-05-12 19:45:07.21988', '2026-05-12 19:45:07.21988'), (70, 5, 'ITEM-54861-000020', '89a5da1315404f5098ee2d5b205a7d26', '/api/trace/89a5da1315404f5098ee2d5b205a7d26?sign=ca30738c5f6884e31e2add964a7ffd506fe8b8c0b2a59317ae7e3d729543f939', 'ca30738c5f6884e31e2add964a7ffd506fe8b8c0b2a59317ae7e3d729543f939', 1, 0, NULL, NULL, '2026-05-12 19:45:07.222549', '2026-05-12 19:45:07.222549'), (71, 5, 'ITEM-54861-000021', '0519d7c3a5e64de884bc23dc17078b49', '/api/trace/0519d7c3a5e64de884bc23dc17078b49?sign=cfb5bf98912d55a94f9549cb5838bc5e7d2eb4d5f3dd98080de28b09870ddb8f', 'cfb5bf98912d55a94f9549cb5838bc5e7d2eb4d5f3dd98080de28b09870ddb8f', 1, 0, NULL, NULL, '2026-05-12 19:45:07.225502', '2026-05-12 19:45:07.225502'), (72, 5, 'ITEM-54861-000022', '8982622ec9724dfa87c9dbb2dcc14a13', '/api/trace/8982622ec9724dfa87c9dbb2dcc14a13?sign=7b3c2e69c55c829fa48c1219444acb31f7876ad71432b1a006d388f00af3e627', '7b3c2e69c55c829fa48c1219444acb31f7876ad71432b1a006d388f00af3e627', 1, 0, NULL, NULL, '2026-05-12 19:45:07.227696', '2026-05-12 19:45:07.227696'), (73, 5, 'ITEM-54861-000023', '86eab64f71f44129ab8472a883350439', '/api/trace/86eab64f71f44129ab8472a883350439?sign=aad5fceb36129f4480aac230176d958c2bc4cc739f83e1fd4d459bbae5bae80a', 'aad5fceb36129f4480aac230176d958c2bc4cc739f83e1fd4d459bbae5bae80a', 1, 0, NULL, NULL, '2026-05-12 19:45:07.230074', '2026-05-12 19:45:07.230074'), (74, 5, 'ITEM-54861-000024', '9fd9f6af17cd442eb810b010754676ca', '/api/trace/9fd9f6af17cd442eb810b010754676ca?sign=b51788a2122b991153ffd195e79dd20b7e0cdd2e5d04f7a2b9a9cbdeb02dda14', 'b51788a2122b991153ffd195e79dd20b7e0cdd2e5d04f7a2b9a9cbdeb02dda14', 1, 0, NULL, NULL, '2026-05-12 19:45:07.232414', '2026-05-12 19:45:07.232414'), (75, 5, 'ITEM-54861-000025', '5d7fa9a8962e4592ba13b7bd89e6b302', '/api/trace/5d7fa9a8962e4592ba13b7bd89e6b302?sign=c600883ecda9c28eab9a988183e48edf2fb5bc286e5a022ff7b9490427a1d4d7', 'c600883ecda9c28eab9a988183e48edf2fb5bc286e5a022ff7b9490427a1d4d7', 1, 0, NULL, NULL, '2026-05-12 19:45:07.235331', '2026-05-12 19:45:07.235331'), (76, 5, 'ITEM-54861-000026', '70dc21a6bdef418fb897385b624e68f9', '/api/trace/70dc21a6bdef418fb897385b624e68f9?sign=41fc27c8e0e32d3d7672254f73d519748739523f931dbea504a64aefbc571494', '41fc27c8e0e32d3d7672254f73d519748739523f931dbea504a64aefbc571494', 1, 0, NULL, NULL, '2026-05-12 19:45:07.237895', '2026-05-12 19:45:07.237895'), (77, 5, 'ITEM-54861-000027', '9967a6bc44e64a39877a3d67a64ecac2', '/api/trace/9967a6bc44e64a39877a3d67a64ecac2?sign=caabd18901be183443ba938194207fca789f2728cb353312c7686fd2d9eb934c', 'caabd18901be183443ba938194207fca789f2728cb353312c7686fd2d9eb934c', 1, 0, NULL, NULL, '2026-05-12 19:45:07.240073', '2026-05-12 19:45:07.240073'), (78, 5, 'ITEM-54861-000028', '5ce766bd95794257a4eaeefc410679f2', '/api/trace/5ce766bd95794257a4eaeefc410679f2?sign=bdf51412a8c3321d2f76cb301b939e9a44f87a1fce466aceb3b624d051f22609', 'bdf51412a8c3321d2f76cb301b939e9a44f87a1fce466aceb3b624d051f22609', 1, 0, NULL, NULL, '2026-05-12 19:45:07.241953', '2026-05-12 19:45:07.241953'), (79, 5, 'ITEM-54861-000029', '7fd2b8956b5e453c88fe6a9792af9ef9', '/api/trace/7fd2b8956b5e453c88fe6a9792af9ef9?sign=3197100b64c1ab443e9b5af5c86e19e18c9cc38eb03e8c09adc708f662f0c03d', '3197100b64c1ab443e9b5af5c86e19e18c9cc38eb03e8c09adc708f662f0c03d', 1, 0, NULL, NULL, '2026-05-12 19:45:07.243991', '2026-05-12 19:45:07.243991'), (80, 5, 'ITEM-54861-000030', 'efbb8805444c4b31a9a311054487aafe', '/api/trace/efbb8805444c4b31a9a311054487aafe?sign=dd6547296a7ebab1579a2103fd825a840c353318ef9a9d7b57c7aaefebeaa8d7', 'dd6547296a7ebab1579a2103fd825a840c353318ef9a9d7b57c7aaefebeaa8d7', 1, 0, NULL, NULL, '2026-05-12 19:45:07.246637', '2026-05-12 19:45:07.246637'), (81, 5, 'ITEM-54861-000031', '3cfbec371dee47549545cb2818d1e082', '/api/trace/3cfbec371dee47549545cb2818d1e082?sign=c9c5155c674cc1870a36e2f721ff7cffa11e838152e39288c34291805fb4e77c', 'c9c5155c674cc1870a36e2f721ff7cffa11e838152e39288c34291805fb4e77c', 1, 0, NULL, NULL, '2026-05-12 19:45:07.249291', '2026-05-12 19:45:07.249291'), (82, 5, 'ITEM-54861-000032', 'cda5328ea12342dd82b912580b7801e0', '/api/trace/cda5328ea12342dd82b912580b7801e0?sign=dae6e93f0aa3bf19d519303fac087496e0e8e304cf316e0232c00489811bdb39', 'dae6e93f0aa3bf19d519303fac087496e0e8e304cf316e0232c00489811bdb39', 1, 0, NULL, NULL, '2026-05-12 19:45:07.252166', '2026-05-12 19:45:07.252166'), (83, 5, 'ITEM-54861-000033', '40390557eefb43af899d986446e152e0', '/api/trace/40390557eefb43af899d986446e152e0?sign=423aa8ab3672ad8b66ada966826e7ec62608db6788dcdec57afffe65c69f5415', '423aa8ab3672ad8b66ada966826e7ec62608db6788dcdec57afffe65c69f5415', 1, 0, NULL, NULL, '2026-05-12 19:45:07.254585', '2026-05-12 19:45:07.254585'), (84, 5, 'ITEM-54861-000034', 'd59c79ac7faa42c4abdae5b3bb2ce9bc', '/api/trace/d59c79ac7faa42c4abdae5b3bb2ce9bc?sign=dd41a69471fb6768b5b0095a96cafabb1e02f6c48d89f68cfcdb6bb958b38bf0', 'dd41a69471fb6768b5b0095a96cafabb1e02f6c48d89f68cfcdb6bb958b38bf0', 1, 0, NULL, NULL, '2026-05-12 19:45:07.257198', '2026-05-12 19:45:07.257198'), (85, 5, 'ITEM-54861-000035', '1112062e7f3040798e1f08688abfb158', '/api/trace/1112062e7f3040798e1f08688abfb158?sign=bcf91e730acee4e5d7370343998cec17abeb0e526a3bd4c24b57277178088b39', 'bcf91e730acee4e5d7370343998cec17abeb0e526a3bd4c24b57277178088b39', 1, 0, NULL, NULL, '2026-05-12 19:45:07.25958', '2026-05-12 19:45:07.25958'), (86, 5, 'ITEM-54861-000036', 'dae42ffd13f5448894369ae5fc00018f', '/api/trace/dae42ffd13f5448894369ae5fc00018f?sign=65cdcc2815cc5e9eca7e6d7630dca68774ab4315ce2a2c88aba270c8bdd2a0d1', '65cdcc2815cc5e9eca7e6d7630dca68774ab4315ce2a2c88aba270c8bdd2a0d1', 1, 0, NULL, NULL, '2026-05-12 19:45:07.262372', '2026-05-12 19:45:07.262372'), (87, 5, 'ITEM-54861-000037', '627dee7baac247c28013edd8923513e9', '/api/trace/627dee7baac247c28013edd8923513e9?sign=8a3f2008860e257da69f41bd8307eb74445b8149d086a193570c6cf0e6d80535', '8a3f2008860e257da69f41bd8307eb74445b8149d086a193570c6cf0e6d80535', 1, 0, NULL, NULL, '2026-05-12 19:45:07.266116', '2026-05-12 19:45:07.266116'), (88, 5, 'ITEM-54861-000038', 'c7c3655dc3644d1e99f3b14df0d678cb', '/api/trace/c7c3655dc3644d1e99f3b14df0d678cb?sign=f0cd13ba7bdf4ee42fe50ca8a26301824e7eca7b21462c81061831a098bc9495', 'f0cd13ba7bdf4ee42fe50ca8a26301824e7eca7b21462c81061831a098bc9495', 1, 0, NULL, NULL, '2026-05-12 19:45:07.269833', '2026-05-12 19:45:07.269833'), (89, 5, 'ITEM-54861-000039', '57ceb37667554e219cd7c89c19389266', '/api/trace/57ceb37667554e219cd7c89c19389266?sign=0d20fd068f291d7baa1eadb49ede07007dfff289f3297875161e0d721a9d0e79', '0d20fd068f291d7baa1eadb49ede07007dfff289f3297875161e0d721a9d0e79', 1, 0, NULL, NULL, '2026-05-12 19:45:07.272928', '2026-05-12 19:45:07.272928'), (90, 5, 'ITEM-54861-000040', '4a0195c60c2b45a7ac200edb05ca421f', '/api/trace/4a0195c60c2b45a7ac200edb05ca421f?sign=1a1a3271abd0e8821a110e59fd2db6745d440043f531504237beeddd9e677b74', '1a1a3271abd0e8821a110e59fd2db6745d440043f531504237beeddd9e677b74', 1, 0, NULL, NULL, '2026-05-12 19:45:07.275549', '2026-05-12 19:45:07.275549'), (91, 5, 'ITEM-54861-000041', 'bc169823bf7e48cf851fde6d3382f88e', '/api/trace/bc169823bf7e48cf851fde6d3382f88e?sign=ec20bf35a061c6ae41d40d37209e8ae53b21e2adda8cd14203ae5b34f8b5d01b', 'ec20bf35a061c6ae41d40d37209e8ae53b21e2adda8cd14203ae5b34f8b5d01b', 1, 0, NULL, NULL, '2026-05-12 19:45:07.278544', '2026-05-12 19:45:07.278544'), (92, 5, 'ITEM-54861-000042', 'f71af3519713479d9e982d43acaa3c83', '/api/trace/f71af3519713479d9e982d43acaa3c83?sign=a1e7f89378bd2f7679868e9d2b46551b579b4eac8028a898dabc80548167429b', 'a1e7f89378bd2f7679868e9d2b46551b579b4eac8028a898dabc80548167429b', 1, 0, NULL, NULL, '2026-05-12 19:45:07.281255', '2026-05-12 19:45:07.281255'), (93, 5, 'ITEM-54861-000043', '81e1680146754a70acbb62a36a2bb895', '/api/trace/81e1680146754a70acbb62a36a2bb895?sign=4fee24c354fd6f338d4f7b0d55bdddd8b2e1f8416571999ea0f858bf2d20aad0', '4fee24c354fd6f338d4f7b0d55bdddd8b2e1f8416571999ea0f858bf2d20aad0', 1, 0, NULL, NULL, '2026-05-12 19:45:07.283998', '2026-05-12 19:45:07.283998'), (94, 5, 'ITEM-54861-000044', '43f11a18857641bcb6a0b6e85a98573c', '/api/trace/43f11a18857641bcb6a0b6e85a98573c?sign=c0fdb9cd7a51c983b50df3ad6446a46cbfcf1656533da05c271327b5865661c7', 'c0fdb9cd7a51c983b50df3ad6446a46cbfcf1656533da05c271327b5865661c7', 1, 0, NULL, NULL, '2026-05-12 19:45:07.287022', '2026-05-12 19:45:07.287022'), (95, 5, 'ITEM-54861-000045', '48b810339ce54275a6c211475ca06e44', '/api/trace/48b810339ce54275a6c211475ca06e44?sign=6aad4003f9d2fc922f701fdb554d0b754e12d9add48cbe768de154c9d3fcb47b', '6aad4003f9d2fc922f701fdb554d0b754e12d9add48cbe768de154c9d3fcb47b', 1, 0, NULL, NULL, '2026-05-12 19:45:07.289525', '2026-05-12 19:45:07.289525'), (96, 5, 'ITEM-54861-000046', '9484f6cb04574c558e8b76d50ae20302', '/api/trace/9484f6cb04574c558e8b76d50ae20302?sign=dcd513333d1abf7c14d888cbc1a147fcbf7594675ecd36bd2a269f104980b57d', 'dcd513333d1abf7c14d888cbc1a147fcbf7594675ecd36bd2a269f104980b57d', 1, 0, NULL, NULL, '2026-05-12 19:45:07.2925', '2026-05-12 19:45:07.2925'), (97, 5, 'ITEM-54861-000047', 'db3ffe68defa49a695bf2b1bf1726d54', '/api/trace/db3ffe68defa49a695bf2b1bf1726d54?sign=b969e841dae71c06eef65c504e150fff30e41c2724ecf0113f005132d055f724', 'b969e841dae71c06eef65c504e150fff30e41c2724ecf0113f005132d055f724', 1, 0, NULL, NULL, '2026-05-12 19:45:07.294962', '2026-05-12 19:45:07.294962'), (98, 5, 'ITEM-54861-000048', '8bb44d61242a48d2984fda3b7b092a75', '/api/trace/8bb44d61242a48d2984fda3b7b092a75?sign=66872077a10814e245758a08887ca645d8552ac8983516fd246d6a3a4fef1855', '66872077a10814e245758a08887ca645d8552ac8983516fd246d6a3a4fef1855', 1, 0, NULL, NULL, '2026-05-12 19:45:07.297789', '2026-05-12 19:45:07.297789'), (99, 5, 'ITEM-54861-000049', '1396d7adcfbf48638783df3995fc6df4', '/api/trace/1396d7adcfbf48638783df3995fc6df4?sign=0cf19e043cfe3c666d36f85a3f9ac39c367402b41031ed958cca1eef849c6a35', '0cf19e043cfe3c666d36f85a3f9ac39c367402b41031ed958cca1eef849c6a35', 1, 0, NULL, NULL, '2026-05-12 19:45:07.301575', '2026-05-12 19:45:07.301575'), (100, 5, 'ITEM-54861-000050', '505bcaf771d543d9a0a2a255f283a3ff', '/api/trace/505bcaf771d543d9a0a2a255f283a3ff?sign=d5666b99c50d60d0f9c6dc6bd4d661511f76ef33f3a388ca14aa2f6be9827639', 'd5666b99c50d60d0f9c6dc6bd4d661511f76ef33f3a388ca14aa2f6be9827639', 1, 0, NULL, NULL, '2026-05-12 19:45:07.305831', '2026-05-12 19:45:07.305831'), (101, 10, 'ITEM-BATCHQITOM20260512001-000001', '02685a50184e463aa9575c39c1170bdb', '/api/trace/02685a50184e463aa9575c39c1170bdb?sign=f335d00feb4d31a628966d0a213aa90b97bebba4d751cd0fef5baeb203701ef5', 'f335d00feb4d31a628966d0a213aa90b97bebba4d751cd0fef5baeb203701ef5', 1, 0, NULL, NULL, '2026-05-12 20:07:00.844439', '2026-05-12 20:07:00.844439'), (102, 10, 'ITEM-BATCHQITOM20260512001-000002', 'de36e0dfc034418ea22b3098d5e5bbd7', '/api/trace/de36e0dfc034418ea22b3098d5e5bbd7?sign=0157689ba694963b485e1772f567f748f3e5383cdd7737e914e15c55a7880ebc', '0157689ba694963b485e1772f567f748f3e5383cdd7737e914e15c55a7880ebc', 1, 0, NULL, NULL, '2026-05-12 20:07:00.848817', '2026-05-12 20:07:00.848817'), (103, 10, 'ITEM-BATCHQITOM20260512001-000003', '502937571d88456ca889c82173568cbe', '/api/trace/502937571d88456ca889c82173568cbe?sign=ae68611a844d73dd445204e14f84a05840a620a2d00fe66397694dbdfb100529', 'ae68611a844d73dd445204e14f84a05840a620a2d00fe66397694dbdfb100529', 1, 0, NULL, NULL, '2026-05-12 20:07:00.851503', '2026-05-12 20:07:00.851503'), (104, 10, 'ITEM-BATCHQITOM20260512001-000004', '40f3f6452a2a4377aa170378fd0745fd', '/api/trace/40f3f6452a2a4377aa170378fd0745fd?sign=53b1abdd0f76df908470a98c13839a6de798becd3c8c28a4577a028b5a8a5f69', '53b1abdd0f76df908470a98c13839a6de798becd3c8c28a4577a028b5a8a5f69', 1, 0, NULL, NULL, '2026-05-12 20:07:00.854455', '2026-05-12 20:07:00.854455'), (105, 10, 'ITEM-BATCHQITOM20260512001-000005', 'b4baa83fdff34dac88308b6e0dacff28', '/api/trace/b4baa83fdff34dac88308b6e0dacff28?sign=bf334cbb5bc34cde29accc4326e9804dc17a30aa63416f996ca06b90a51fad5c', 'bf334cbb5bc34cde29accc4326e9804dc17a30aa63416f996ca06b90a51fad5c', 1, 0, NULL, NULL, '2026-05-12 20:07:00.857137', '2026-05-12 20:07:00.857137'), (106, 10, 'ITEM-BATCHQITOM20260512001-000006', 'ba35ebe12a85494dab5d5bb119467e46', '/api/trace/ba35ebe12a85494dab5d5bb119467e46?sign=3e1c817731cb6f7dc9cfffeed39469af5c6bbc4fec72650eedafb7818d864d5a', '3e1c817731cb6f7dc9cfffeed39469af5c6bbc4fec72650eedafb7818d864d5a', 1, 0, NULL, NULL, '2026-05-12 20:07:00.860233', '2026-05-12 20:07:00.860233'), (107, 10, 'ITEM-BATCHQITOM20260512001-000007', 'e8dfcb44b6114d39a35adb6f530a4047', '/api/trace/e8dfcb44b6114d39a35adb6f530a4047?sign=fe15a7e6de07e492527160105c78cf622b4f1971df7b6de82b33ecfa5e67ac3f', 'fe15a7e6de07e492527160105c78cf622b4f1971df7b6de82b33ecfa5e67ac3f', 1, 0, NULL, NULL, '2026-05-12 20:07:00.862959', '2026-05-12 20:07:00.862959'), (108, 10, 'ITEM-BATCHQITOM20260512001-000008', 'b3e1a67b13d548fb96d6de3eb53f986c', '/api/trace/b3e1a67b13d548fb96d6de3eb53f986c?sign=938545c9b54bd32e196782faeac35ada5650ae9261c42bfca13e642011bf595b', '938545c9b54bd32e196782faeac35ada5650ae9261c42bfca13e642011bf595b', 1, 0, NULL, NULL, '2026-05-12 20:07:00.865139', '2026-05-12 20:07:00.865139'), (109, 10, 'ITEM-BATCHQITOM20260512001-000009', '2447894e6fe840ce8412ae0910b30e49', '/api/trace/2447894e6fe840ce8412ae0910b30e49?sign=29ca80de7df9b948ed7a839ff853bbf61f082500a43e5d2b00e36a882b9a4966', '29ca80de7df9b948ed7a839ff853bbf61f082500a43e5d2b00e36a882b9a4966', 1, 0, NULL, NULL, '2026-05-12 20:07:00.867578', '2026-05-12 20:07:00.867578'), (110, 10, 'ITEM-BATCHQITOM20260512001-000010', '6c3b962bbc3342b2b7aac41b371553e4', '/api/trace/6c3b962bbc3342b2b7aac41b371553e4?sign=8fd3c2b332e0c20697577e6cece4c554b5415352df89c581c068fda5f3d5d6e8', '8fd3c2b332e0c20697577e6cece4c554b5415352df89c581c068fda5f3d5d6e8', 1, 0, NULL, NULL, '2026-05-12 20:07:00.87022', '2026-05-12 20:07:00.87022'), (111, 10, 'ITEM-BATCHQITOM20260512001-000011', '94b3b16f25f846398e9470e29b44bf53', '/api/trace/94b3b16f25f846398e9470e29b44bf53?sign=d94a7ec0f35ef20c0be50b273ced72eb48e4c66dd8484c0eb48afb81e6d77231', 'd94a7ec0f35ef20c0be50b273ced72eb48e4c66dd8484c0eb48afb81e6d77231', 1, 0, NULL, NULL, '2026-05-12 20:07:00.87209', '2026-05-12 20:07:00.87209'), (112, 10, 'ITEM-BATCHQITOM20260512001-000012', 'f02cb33dc7c4462680982e0077b25e9b', '/api/trace/f02cb33dc7c4462680982e0077b25e9b?sign=4d206a27db406845b519d33461baebd59d578a695cb9be3448860a98c8158e13', '4d206a27db406845b519d33461baebd59d578a695cb9be3448860a98c8158e13', 1, 0, NULL, NULL, '2026-05-12 20:07:00.87445', '2026-05-12 20:07:00.87445'), (113, 10, 'ITEM-BATCHQITOM20260512001-000013', '12647ba60a6c46828082dbffdaf6c36c', '/api/trace/12647ba60a6c46828082dbffdaf6c36c?sign=14bcf390931290c2f472ec5e97c1fab2fb8956322053409b38c36d72abd5f67a', '14bcf390931290c2f472ec5e97c1fab2fb8956322053409b38c36d72abd5f67a', 1, 0, NULL, NULL, '2026-05-12 20:07:00.876516', '2026-05-12 20:07:00.876516'), (114, 10, 'ITEM-BATCHQITOM20260512001-000014', '0eeba262645c405cb94ea34c845fbf7d', '/api/trace/0eeba262645c405cb94ea34c845fbf7d?sign=d3ef4623c77114dfb2d30f2c4a9924d44fc63b23e70f686df44a64ecfd616abd', 'd3ef4623c77114dfb2d30f2c4a9924d44fc63b23e70f686df44a64ecfd616abd', 1, 0, NULL, NULL, '2026-05-12 20:07:00.879917', '2026-05-12 20:07:00.879917'), (115, 10, 'ITEM-BATCHQITOM20260512001-000015', '13f071baa8f14749b68a5b6d1f905f87', '/api/trace/13f071baa8f14749b68a5b6d1f905f87?sign=bb0152479323b813b1271e7f2b3a725e417f54a58d076588b62dfb5931645196', 'bb0152479323b813b1271e7f2b3a725e417f54a58d076588b62dfb5931645196', 1, 0, NULL, NULL, '2026-05-12 20:07:00.882392', '2026-05-12 20:07:00.882392'), (116, 10, 'ITEM-BATCHQITOM20260512001-000016', 'ef0e3eb1e5ae417b9202219065ebe17c', '/api/trace/ef0e3eb1e5ae417b9202219065ebe17c?sign=8f5f9f9326178d8c52eae293296ffafbe6bf3ae7e6c11b425516597acf34180a', '8f5f9f9326178d8c52eae293296ffafbe6bf3ae7e6c11b425516597acf34180a', 1, 0, NULL, NULL, '2026-05-12 20:07:00.884522', '2026-05-12 20:07:00.884522'), (117, 10, 'ITEM-BATCHQITOM20260512001-000017', '70bd741cb5f14108a6c4237c2b5033f4', '/api/trace/70bd741cb5f14108a6c4237c2b5033f4?sign=5c3553f2e5af5100da4865427ee05da90411f11d625d41a33287e8d8a2914ee2', '5c3553f2e5af5100da4865427ee05da90411f11d625d41a33287e8d8a2914ee2', 1, 0, NULL, NULL, '2026-05-12 20:07:00.886081', '2026-05-12 20:07:00.886081'), (118, 10, 'ITEM-BATCHQITOM20260512001-000018', '4e8b7ddf7b9f4ef79a35f15d9b735d0f', '/api/trace/4e8b7ddf7b9f4ef79a35f15d9b735d0f?sign=ce2f5130d92f530edae5bbf3bf957a3861318114363dffe113379f99a2b4bc6e', 'ce2f5130d92f530edae5bbf3bf957a3861318114363dffe113379f99a2b4bc6e', 1, 0, NULL, NULL, '2026-05-12 20:07:00.888563', '2026-05-12 20:07:00.888563'), (119, 10, 'ITEM-BATCHQITOM20260512001-000019', '38160be27ea948428d45fb20d8a91313', '/api/trace/38160be27ea948428d45fb20d8a91313?sign=6af1fd5230e5fc4c4f66ab5408209d058f2f6b15971efe07fd797911120d65db', '6af1fd5230e5fc4c4f66ab5408209d058f2f6b15971efe07fd797911120d65db', 1, 0, NULL, NULL, '2026-05-12 20:07:00.890224', '2026-05-12 20:07:00.890224'), (120, 10, 'ITEM-BATCHQITOM20260512001-000020', 'f698f1ebdf7248b18791197a95d0e68e', '/api/trace/f698f1ebdf7248b18791197a95d0e68e?sign=87b37320e7ae91dc185e8a7646c4d8aa751a8dabab8b2071ef934155fbb608a6', '87b37320e7ae91dc185e8a7646c4d8aa751a8dabab8b2071ef934155fbb608a6', 1, 0, NULL, NULL, '2026-05-12 20:07:00.892296', '2026-05-12 20:07:00.892296'), (121, 10, 'ITEM-BATCHQITOM20260512001-000021', '6849f3493bce41c68e13c73c2f03e5b6', '/api/trace/6849f3493bce41c68e13c73c2f03e5b6?sign=23d465021a62e288f1fa18c5163189d9706394f1313247a47826d5361fc9453e', '23d465021a62e288f1fa18c5163189d9706394f1313247a47826d5361fc9453e', 1, 0, NULL, NULL, '2026-05-12 20:07:00.894525', '2026-05-12 20:07:00.894525'), (122, 10, 'ITEM-BATCHQITOM20260512001-000022', '80ddb872b0164fc69cc859bef6c8058f', '/api/trace/80ddb872b0164fc69cc859bef6c8058f?sign=d22463d9e3be8c5c72a2f667eda6df6fd6c46ad983ec3a33f54bab02661fe1c4', 'd22463d9e3be8c5c72a2f667eda6df6fd6c46ad983ec3a33f54bab02661fe1c4', 1, 0, NULL, NULL, '2026-05-12 20:07:00.896887', '2026-05-12 20:07:00.896887'), (124, 10, 'ITEM-BATCHQITOM20260512001-000024', 'c944ddd2b79b42519dd69b819f993c46', '/api/trace/c944ddd2b79b42519dd69b819f993c46?sign=310d11452df86c8c9ff21fea9d3f35731fa356c62e841746f7d5db6eff1fb064', '310d11452df86c8c9ff21fea9d3f35731fa356c62e841746f7d5db6eff1fb064', 1, 2, '2026-05-12 20:08:18.99342', '2026-05-12 20:08:19.442528', '2026-05-12 20:07:00.902583', '2026-05-12 20:08:19.442528'), (143, 12, 'ITEM-BATCHQIRICE20260512001-000001', '93b0664fdf6f48fb8b3c9757561c2652', '/api/trace/93b0664fdf6f48fb8b3c9757561c2652?sign=ded64e702faf1d04c5dd2fa6cec81ac3909356495d4e3415ceedc47b9b89cf48', 'ded64e702faf1d04c5dd2fa6cec81ac3909356495d4e3415ceedc47b9b89cf48', 1, 0, NULL, NULL, '2026-05-12 20:30:35.06215', '2026-05-12 20:30:35.06215'), (125, 11, 'ITEM-BATCHQILET20260512001-000001', '8c5b39d7463e43f9a36b8a975021ee8d', '/api/trace/8c5b39d7463e43f9a36b8a975021ee8d?sign=55f52441b71fa0a23827d35d0cd2b6f09227d2f94288fcd3916c9d101618a115', '55f52441b71fa0a23827d35d0cd2b6f09227d2f94288fcd3916c9d101618a115', 1, 0, NULL, NULL, '2026-05-12 20:10:04.159356', '2026-05-12 20:10:04.159356'), (126, 11, 'ITEM-BATCHQILET20260512001-000002', '437b4956e2ef479381da95e89e5c2b71', '/api/trace/437b4956e2ef479381da95e89e5c2b71?sign=40935226ca71bc308515ce91bd58724094606b9f7390bf777a177e0cf39a3370', '40935226ca71bc308515ce91bd58724094606b9f7390bf777a177e0cf39a3370', 1, 0, NULL, NULL, '2026-05-12 20:10:04.16216', '2026-05-12 20:10:04.16216'), (127, 11, 'ITEM-BATCHQILET20260512001-000003', 'b839ea383b40476c89f4d365e9595c01', '/api/trace/b839ea383b40476c89f4d365e9595c01?sign=0472745ad0f855c7943b0ed32280247e812f655c38cb37e22e25a4468f92532d', '0472745ad0f855c7943b0ed32280247e812f655c38cb37e22e25a4468f92532d', 1, 0, NULL, NULL, '2026-05-12 20:10:04.164986', '2026-05-12 20:10:04.164986'), (128, 11, 'ITEM-BATCHQILET20260512001-000004', '037ccfce33704453b6c788aa1a4b7142', '/api/trace/037ccfce33704453b6c788aa1a4b7142?sign=ea1b804dbad7e502034e13cd1fac8c9f75654407bdb974f64c7e99e44538a597', 'ea1b804dbad7e502034e13cd1fac8c9f75654407bdb974f64c7e99e44538a597', 1, 0, NULL, NULL, '2026-05-12 20:10:04.167099', '2026-05-12 20:10:04.167099'), (129, 11, 'ITEM-BATCHQILET20260512001-000005', 'f53d3064c9934cb9b74cb3b161a241f5', '/api/trace/f53d3064c9934cb9b74cb3b161a241f5?sign=93f6647f3c968a30c21eb5c89534a5c32274d20e57767dcdb777100ff0eb9999', '93f6647f3c968a30c21eb5c89534a5c32274d20e57767dcdb777100ff0eb9999', 1, 0, NULL, NULL, '2026-05-12 20:10:04.169547', '2026-05-12 20:10:04.169547'), (130, 11, 'ITEM-BATCHQILET20260512001-000006', '05dee6b534d94e54bc650a68fbb33317', '/api/trace/05dee6b534d94e54bc650a68fbb33317?sign=0e3a48616f3af8efca20a6c067b9e52ed75d1b389a4e704fa1108a0c873265b1', '0e3a48616f3af8efca20a6c067b9e52ed75d1b389a4e704fa1108a0c873265b1', 1, 0, NULL, NULL, '2026-05-12 20:10:04.172395', '2026-05-12 20:10:04.172395'), (131, 11, 'ITEM-BATCHQILET20260512001-000007', 'b3ffc791620949a0bbb178b7fb4b2ca4', '/api/trace/b3ffc791620949a0bbb178b7fb4b2ca4?sign=e5762a2e0ecfe203bfe6ae3cfa3f0704b97ca759304836aa3a12a7d23c908209', 'e5762a2e0ecfe203bfe6ae3cfa3f0704b97ca759304836aa3a12a7d23c908209', 1, 0, NULL, NULL, '2026-05-12 20:10:04.174534', '2026-05-12 20:10:04.174534'), (132, 11, 'ITEM-BATCHQILET20260512001-000008', 'edcd6f827128451f91a1ec1d908bb019', '/api/trace/edcd6f827128451f91a1ec1d908bb019?sign=0f1064b2de598e8490bfff6d50719233bf4a9364f49dbb87f0ada12ea3c85082', '0f1064b2de598e8490bfff6d50719233bf4a9364f49dbb87f0ada12ea3c85082', 1, 0, NULL, NULL, '2026-05-12 20:10:04.176589', '2026-05-12 20:10:04.176589'), (133, 11, 'ITEM-BATCHQILET20260512001-000009', 'db09d14bbdf24a6894b9fa0935afd167', '/api/trace/db09d14bbdf24a6894b9fa0935afd167?sign=51e3ed877f130aeea101b6a17c8291a1dd244596cdd10bde4e8a30c0852dab0e', '51e3ed877f130aeea101b6a17c8291a1dd244596cdd10bde4e8a30c0852dab0e', 1, 0, NULL, NULL, '2026-05-12 20:10:04.17857', '2026-05-12 20:10:04.17857'), (134, 11, 'ITEM-BATCHQILET20260512001-000010', 'fb72c77f1c2343089a586675037f290f', '/api/trace/fb72c77f1c2343089a586675037f290f?sign=541be3eeb58c59ef03244cac54a2074de9b31e0002a13c4277e6ac62b128819e', '541be3eeb58c59ef03244cac54a2074de9b31e0002a13c4277e6ac62b128819e', 1, 0, NULL, NULL, '2026-05-12 20:10:04.181372', '2026-05-12 20:10:04.181372'), (135, 11, 'ITEM-BATCHQILET20260512001-000011', '02ee785a13fd491e804991fcfcbcc0c7', '/api/trace/02ee785a13fd491e804991fcfcbcc0c7?sign=4ebbe1789c29b76cc516bbc0aed469cea6297d5ffdeb52e477dcdf4938fa8a5d', '4ebbe1789c29b76cc516bbc0aed469cea6297d5ffdeb52e477dcdf4938fa8a5d', 1, 0, NULL, NULL, '2026-05-12 20:10:04.183489', '2026-05-12 20:10:04.183489'), (136, 11, 'ITEM-BATCHQILET20260512001-000012', '9ccdcab624dc42b48adaf17c5d960d72', '/api/trace/9ccdcab624dc42b48adaf17c5d960d72?sign=0ac3e237b5118ea3519037d5c667317899aa66bd8c2e6aa67388fa3c8edda7c4', '0ac3e237b5118ea3519037d5c667317899aa66bd8c2e6aa67388fa3c8edda7c4', 1, 0, NULL, NULL, '2026-05-12 20:10:04.185566', '2026-05-12 20:10:04.185566'), (137, 11, 'ITEM-BATCHQILET20260512001-000013', '699f8059689b44f3bb413db7088083fb', '/api/trace/699f8059689b44f3bb413db7088083fb?sign=1f1314ba7bc760142291a5ab935cc1426efc57929b51c763804fe56155635fa2', '1f1314ba7bc760142291a5ab935cc1426efc57929b51c763804fe56155635fa2', 1, 0, NULL, NULL, '2026-05-12 20:10:04.187266', '2026-05-12 20:10:04.187266'), (138, 11, 'ITEM-BATCHQILET20260512001-000014', '4963d14878374d62934cd7d104009eef', '/api/trace/4963d14878374d62934cd7d104009eef?sign=d737348f4b5e63e1414b630b5caad72c9d696b937e3b3697035109c21758bc58', 'd737348f4b5e63e1414b630b5caad72c9d696b937e3b3697035109c21758bc58', 1, 0, NULL, NULL, '2026-05-12 20:10:04.189187', '2026-05-12 20:10:04.189187'), (139, 11, 'ITEM-BATCHQILET20260512001-000015', '223767cff5ff4d4fbc0ceaff49aacc22', '/api/trace/223767cff5ff4d4fbc0ceaff49aacc22?sign=1681ec591cdd724ee0fd83e2797a9b25fa1aecea0a4423a5f19d6090141fe599', '1681ec591cdd724ee0fd83e2797a9b25fa1aecea0a4423a5f19d6090141fe599', 1, 0, NULL, NULL, '2026-05-12 20:10:04.191724', '2026-05-12 20:10:04.191724'), (140, 11, 'ITEM-BATCHQILET20260512001-000016', '285ca26f1fce42919484a6486411cc17', '/api/trace/285ca26f1fce42919484a6486411cc17?sign=ca3e730df8674583fcace6eaaed05453125c6ccbb0831fc9966423d6037f59ad', 'ca3e730df8674583fcace6eaaed05453125c6ccbb0831fc9966423d6037f59ad', 1, 0, NULL, NULL, '2026-05-12 20:10:04.193397', '2026-05-12 20:10:04.193397'), (141, 11, 'ITEM-BATCHQILET20260512001-000017', '76c60e6f77ec4a62a6896778d5d4b78a', '/api/trace/76c60e6f77ec4a62a6896778d5d4b78a?sign=f1b2e75fc344696e6767c9521007950fc1d3da1bc571430898394e48b998ae15', 'f1b2e75fc344696e6767c9521007950fc1d3da1bc571430898394e48b998ae15', 1, 0, NULL, NULL, '2026-05-12 20:10:04.195258', '2026-05-12 20:10:04.195258'), (142, 11, 'ITEM-BATCHQILET20260512001-000018', '1c129896bd824e2dbb7d8b88d7ef82a4', '/api/trace/1c129896bd824e2dbb7d8b88d7ef82a4?sign=dae3de57688e24c90d51012c53c88ad24d74c1baddb1a6712683efcabe25aa89', 'dae3de57688e24c90d51012c53c88ad24d74c1baddb1a6712683efcabe25aa89', 1, 0, NULL, NULL, '2026-05-12 20:10:04.197577', '2026-05-12 20:10:04.197577'), (144, 12, 'ITEM-BATCHQIRICE20260512001-000002', '5f95384f55114f76be25297de73f3e3a', '/api/trace/5f95384f55114f76be25297de73f3e3a?sign=d21eaecf8d4e52e83db22b898cfec39416ac3258008522ef27077ab9f4853d5d', 'd21eaecf8d4e52e83db22b898cfec39416ac3258008522ef27077ab9f4853d5d', 1, 0, NULL, NULL, '2026-05-12 20:30:35.066717', '2026-05-12 20:30:35.066717'), (145, 12, 'ITEM-BATCHQIRICE20260512001-000003', '58a25753004a45828e5516ee302bb4b7', '/api/trace/58a25753004a45828e5516ee302bb4b7?sign=1250fb80f99192402ef14e6c6464b2a0ca48875a6ed7b5537e68d9fabee424de', '1250fb80f99192402ef14e6c6464b2a0ca48875a6ed7b5537e68d9fabee424de', 1, 0, NULL, NULL, '2026-05-12 20:30:35.069039', '2026-05-12 20:30:35.069039'), (146, 12, 'ITEM-BATCHQIRICE20260512001-000004', 'b23daba3af6c4fa68bcb8c47993c47d7', '/api/trace/b23daba3af6c4fa68bcb8c47993c47d7?sign=273896532e9188a215008efe98b19f74a1c7a38ab8ca255f68b41dbab253e805', '273896532e9188a215008efe98b19f74a1c7a38ab8ca255f68b41dbab253e805', 1, 0, NULL, NULL, '2026-05-12 20:30:35.071912', '2026-05-12 20:30:35.071912'), (147, 12, 'ITEM-BATCHQIRICE20260512001-000005', 'f0d94e96139d49898e4ea6e259c11da3', '/api/trace/f0d94e96139d49898e4ea6e259c11da3?sign=7e0209a4fdbd618dd8e36528daf30c0882160a98b2b2d754dae76e83dcdc8f84', '7e0209a4fdbd618dd8e36528daf30c0882160a98b2b2d754dae76e83dcdc8f84', 1, 0, NULL, NULL, '2026-05-12 20:30:35.075998', '2026-05-12 20:30:35.075998'), (148, 12, 'ITEM-BATCHQIRICE20260512001-000006', '8902ec828ccb4f4398ba1a655e3c193a', '/api/trace/8902ec828ccb4f4398ba1a655e3c193a?sign=05a589deb585ced6074a1a7dbb0e3fe3a3e3d7c7deb56e50dbd31b83304cfb9d', '05a589deb585ced6074a1a7dbb0e3fe3a3e3d7c7deb56e50dbd31b83304cfb9d', 1, 0, NULL, NULL, '2026-05-12 20:30:35.079567', '2026-05-12 20:30:35.079567'), (149, 12, 'ITEM-BATCHQIRICE20260512001-000007', 'b3c2fc17268f4a098cfd9395a3e313eb', '/api/trace/b3c2fc17268f4a098cfd9395a3e313eb?sign=151bd5f0465c36c73f15c4c9b6fa5a0f2b0bcf8b47c056a4d0dc600e498037c5', '151bd5f0465c36c73f15c4c9b6fa5a0f2b0bcf8b47c056a4d0dc600e498037c5', 1, 0, NULL, NULL, '2026-05-12 20:30:35.084644', '2026-05-12 20:30:35.084644'), (150, 12, 'ITEM-BATCHQIRICE20260512001-000008', '03b2f924cb6141cf921be9d3f9c8baef', '/api/trace/03b2f924cb6141cf921be9d3f9c8baef?sign=855cb5ebb647a463d73f090ea684d2af9117b0f3c2c0110055b02f1b7f24849e', '855cb5ebb647a463d73f090ea684d2af9117b0f3c2c0110055b02f1b7f24849e', 1, 0, NULL, NULL, '2026-05-12 20:30:35.087739', '2026-05-12 20:30:35.087739'), (151, 12, 'ITEM-BATCHQIRICE20260512001-000009', 'bcf69c4e694c4001bd51bcac5fb2daa5', '/api/trace/bcf69c4e694c4001bd51bcac5fb2daa5?sign=63b5bdcbf5f2f60e69edcf647dfbefa6184b8547fb7993e26befcf03295b2916', '63b5bdcbf5f2f60e69edcf647dfbefa6184b8547fb7993e26befcf03295b2916', 1, 0, NULL, NULL, '2026-05-12 20:30:35.090131', '2026-05-12 20:30:35.090131'), (152, 12, 'ITEM-BATCHQIRICE20260512001-000010', '8e2ebc0c3d9b425fab55d911ecd67fb0', '/api/trace/8e2ebc0c3d9b425fab55d911ecd67fb0?sign=18b961959626946848d691fe7d254b7ea978977201116e648e1947ad3dd9da94', '18b961959626946848d691fe7d254b7ea978977201116e648e1947ad3dd9da94', 1, 0, NULL, NULL, '2026-05-12 20:30:35.093278', '2026-05-12 20:30:35.093278'), (153, 12, 'ITEM-BATCHQIRICE20260512001-000011', '380a62dd476748789c8fd8cf4326cac7', '/api/trace/380a62dd476748789c8fd8cf4326cac7?sign=cd5a95a802ac9612192807174f7a8cad988b54dd116de1dbcb99107a9921ab62', 'cd5a95a802ac9612192807174f7a8cad988b54dd116de1dbcb99107a9921ab62', 1, 0, NULL, NULL, '2026-05-12 20:30:35.096181', '2026-05-12 20:30:35.096181'), (154, 12, 'ITEM-BATCHQIRICE20260512001-000012', 'c4fbdb3fe13e4b37ba5c379ea0318f21', '/api/trace/c4fbdb3fe13e4b37ba5c379ea0318f21?sign=d9562e15ac7334ae4db0ca4c64b0bb14401d144b23ccd2dbd3d3f56a2d07ce2d', 'd9562e15ac7334ae4db0ca4c64b0bb14401d144b23ccd2dbd3d3f56a2d07ce2d', 1, 0, NULL, NULL, '2026-05-12 20:30:35.098584', '2026-05-12 20:30:35.098584'), (155, 12, 'ITEM-BATCHQIRICE20260512001-000013', '241b9927cf5d4347bfd5d4f197259057', '/api/trace/241b9927cf5d4347bfd5d4f197259057?sign=b2d7e99d35a75f139a12154e9a708a727a77dd51de3aab239b66cb26a82bc0b9', 'b2d7e99d35a75f139a12154e9a708a727a77dd51de3aab239b66cb26a82bc0b9', 1, 0, NULL, NULL, '2026-05-12 20:30:35.1007', '2026-05-12 20:30:35.1007'), (156, 12, 'ITEM-BATCHQIRICE20260512001-000014', 'c84b3e1a5c1e44ad87e3020c078b15d4', '/api/trace/c84b3e1a5c1e44ad87e3020c078b15d4?sign=792f1866c5bc208a870b03efcea7c36e9f89d20e521e34f7decec251a846e6e0', '792f1866c5bc208a870b03efcea7c36e9f89d20e521e34f7decec251a846e6e0', 1, 0, NULL, NULL, '2026-05-12 20:30:35.103516', '2026-05-12 20:30:35.103516'), (157, 12, 'ITEM-BATCHQIRICE20260512001-000015', '8832048c09284f4cae15846c4f8ca997', '/api/trace/8832048c09284f4cae15846c4f8ca997?sign=0e7c6fec906b1b2384dd2142ba53496f5ff278c564cf29a8c9a68bec85862ba9', '0e7c6fec906b1b2384dd2142ba53496f5ff278c564cf29a8c9a68bec85862ba9', 1, 0, NULL, NULL, '2026-05-12 20:30:35.106471', '2026-05-12 20:30:35.106471'), (158, 12, 'ITEM-BATCHQIRICE20260512001-000016', '900a75b00445485eaaf91144e61168e0', '/api/trace/900a75b00445485eaaf91144e61168e0?sign=88ef537a44fd621260883c1834182ef7d0c84df5576620fd76f1d0ac9083b233', '88ef537a44fd621260883c1834182ef7d0c84df5576620fd76f1d0ac9083b233', 1, 0, NULL, NULL, '2026-05-12 20:30:35.109072', '2026-05-12 20:30:35.109072'), (159, 12, 'ITEM-BATCHQIRICE20260512001-000017', 'f2903a0d13244f759ba1b153f07a60af', '/api/trace/f2903a0d13244f759ba1b153f07a60af?sign=b0964b156b39e2fbce5bbd7101f57937af67806864a8a716998fed07e833581e', 'b0964b156b39e2fbce5bbd7101f57937af67806864a8a716998fed07e833581e', 1, 0, NULL, NULL, '2026-05-12 20:30:35.111082', '2026-05-12 20:30:35.111082'), (160, 12, 'ITEM-BATCHQIRICE20260512001-000018', 'ec1c06080d02433e874b13081b975695', '/api/trace/ec1c06080d02433e874b13081b975695?sign=b3040d65580f39d12d4428e8a73029b7934ce4968b1e84997786c1422d2b67f9', 'b3040d65580f39d12d4428e8a73029b7934ce4968b1e84997786c1422d2b67f9', 1, 0, NULL, NULL, '2026-05-12 20:30:35.113792', '2026-05-12 20:30:35.113792'), (161, 12, 'ITEM-BATCHQIRICE20260512001-000019', '9366293b187e41779630248cef490b65', '/api/trace/9366293b187e41779630248cef490b65?sign=d4d597a0279ba3be23ad3005ff0b4af7749236f354ec522e9aa49393f6cd4dad', 'd4d597a0279ba3be23ad3005ff0b4af7749236f354ec522e9aa49393f6cd4dad', 1, 0, NULL, NULL, '2026-05-12 20:30:35.117063', '2026-05-12 20:30:35.117063'), (162, 12, 'ITEM-BATCHQIRICE20260512001-000020', '2132c129c8f44540be1310b26415c941', '/api/trace/2132c129c8f44540be1310b26415c941?sign=db23364d7a5738c81088073b47338f35e7d95f931b8daab2c58d27f83a27e40c', 'db23364d7a5738c81088073b47338f35e7d95f931b8daab2c58d27f83a27e40c', 1, 0, NULL, NULL, '2026-05-12 20:30:35.119235', '2026-05-12 20:30:35.119235'), (163, 12, 'ITEM-BATCHQIRICE20260512001-000021', 'c8d8fc12031648a594fb446602a15bb7', '/api/trace/c8d8fc12031648a594fb446602a15bb7?sign=c376dcf9dc8f4ead6fc30f9f230b871be56f414f89072b19d4b23af948f093a7', 'c376dcf9dc8f4ead6fc30f9f230b871be56f414f89072b19d4b23af948f093a7', 1, 0, NULL, NULL, '2026-05-12 20:30:35.121501', '2026-05-12 20:30:35.121501'), (164, 12, 'ITEM-BATCHQIRICE20260512001-000022', '6f5b2199883e47f1adbd1e4c2395df73', '/api/trace/6f5b2199883e47f1adbd1e4c2395df73?sign=b3e48194678a1a2c7f2e1577fd4b6c9fe2659e8144363ba04ab734c17e89c010', 'b3e48194678a1a2c7f2e1577fd4b6c9fe2659e8144363ba04ab734c17e89c010', 1, 0, NULL, NULL, '2026-05-12 20:30:35.12348', '2026-05-12 20:30:35.12348'), (165, 12, 'ITEM-BATCHQIRICE20260512001-000023', '86ba3c7d9f7d4f7dac896ef9facc9d4a', '/api/trace/86ba3c7d9f7d4f7dac896ef9facc9d4a?sign=696d08baea891b43a7c2f7e08c3a34b1ee9683b5dd647060f0a752cac6097695', '696d08baea891b43a7c2f7e08c3a34b1ee9683b5dd647060f0a752cac6097695', 1, 0, NULL, NULL, '2026-05-12 20:30:35.125823', '2026-05-12 20:30:35.125823'), (166, 12, 'ITEM-BATCHQIRICE20260512001-000024', '158d011ab06f4500ab93abb3fee2c926', '/api/trace/158d011ab06f4500ab93abb3fee2c926?sign=904cb101b3f90e54bc70e3ff8b6230e52f9eba736ffcc9fec45257dc10a85623', '904cb101b3f90e54bc70e3ff8b6230e52f9eba736ffcc9fec45257dc10a85623', 1, 0, NULL, NULL, '2026-05-12 20:30:35.128276', '2026-05-12 20:30:35.128276'), (167, 12, 'ITEM-BATCHQIRICE20260512001-000025', '773b9dedd69244728104104ae0d9fd79', '/api/trace/773b9dedd69244728104104ae0d9fd79?sign=7b96e6ef765e2876494ffd0ba93f0a3cb83706edd5e98f5a739f1a1e5a65dac0', '7b96e6ef765e2876494ffd0ba93f0a3cb83706edd5e98f5a739f1a1e5a65dac0', 1, 0, NULL, NULL, '2026-05-12 20:30:35.130762', '2026-05-12 20:30:35.130762'), (168, 12, 'ITEM-BATCHQIRICE20260512001-000026', '0ea3fe6d36554924ba9d68695e4244e3', '/api/trace/0ea3fe6d36554924ba9d68695e4244e3?sign=f04dd12dcc26302be05a7af6b2c7aa9bd866352a972442f6cb57fefc939343a7', 'f04dd12dcc26302be05a7af6b2c7aa9bd866352a972442f6cb57fefc939343a7', 1, 0, NULL, NULL, '2026-05-12 20:30:35.132826', '2026-05-12 20:30:35.132826'), (169, 12, 'ITEM-BATCHQIRICE20260512001-000027', '9343d23e85ef44ee970cb5033808494f', '/api/trace/9343d23e85ef44ee970cb5033808494f?sign=9b0521032994d2e11cbb78587aaa3cf9b884f2e2b0dca4d0872b1f67a021605b', '9b0521032994d2e11cbb78587aaa3cf9b884f2e2b0dca4d0872b1f67a021605b', 1, 0, NULL, NULL, '2026-05-12 20:30:35.134822', '2026-05-12 20:30:35.134822'), (170, 12, 'ITEM-BATCHQIRICE20260512001-000028', 'adae9473a25d48c78b7c5f8c73d2a1a5', '/api/trace/adae9473a25d48c78b7c5f8c73d2a1a5?sign=01e4d2f1ec2718b663b227b5eb35233e52c9ef593b7eaf0a5fbe0b9be1609593', '01e4d2f1ec2718b663b227b5eb35233e52c9ef593b7eaf0a5fbe0b9be1609593', 1, 0, NULL, NULL, '2026-05-12 20:30:35.137883', '2026-05-12 20:30:35.137883'), (171, 12, 'ITEM-BATCHQIRICE20260512001-000029', '99fd6f5e23bc450b9d60d61454ed075a', '/api/trace/99fd6f5e23bc450b9d60d61454ed075a?sign=f59fc2018cc14064157e4cef667fa1a0d4da46ff9c92bc3427e6a9d16f61dc4c', 'f59fc2018cc14064157e4cef667fa1a0d4da46ff9c92bc3427e6a9d16f61dc4c', 1, 0, NULL, NULL, '2026-05-12 20:30:35.139778', '2026-05-12 20:30:35.139778'), (123, 10, 'ITEM-BATCHQITOM20260512001-000023', '685244cb64e44be5bacc1150ac1f5ee4', '/api/trace/685244cb64e44be5bacc1150ac1f5ee4?sign=15957bf6357d648d2f459e76f8081f9336b4a0f490af848984ca62342a415277', '15957bf6357d648d2f459e76f8081f9336b4a0f490af848984ca62342a415277', 1, 10, '2026-05-12 20:08:27.372569', '2026-05-12 20:32:15.895755', '2026-05-12 20:07:00.899312', '2026-05-12 20:32:15.895755'), (173, 13, 'ITEM-BATCHQISTR20260512001-000001', '9a2c9ce086454d49adb36d9480d29ca5', '/api/trace/9a2c9ce086454d49adb36d9480d29ca5?sign=2cdf8b317bf1ffa68e238fad9f1be6fe51050c8f988d5029386b3d5d025b156c', '2cdf8b317bf1ffa68e238fad9f1be6fe51050c8f988d5029386b3d5d025b156c', 1, 0, NULL, NULL, '2026-05-12 20:41:56.771865', '2026-05-12 20:41:56.771865'), (174, 13, 'ITEM-BATCHQISTR20260512001-000002', 'c438f1cf20094f3e86b5aba21f4f9a5a', '/api/trace/c438f1cf20094f3e86b5aba21f4f9a5a?sign=13cd52afba5e29f92619c5d6e6367830483ca2620fe01b59e6e66f769b67cbe5', '13cd52afba5e29f92619c5d6e6367830483ca2620fe01b59e6e66f769b67cbe5', 1, 0, NULL, NULL, '2026-05-12 20:41:56.775221', '2026-05-12 20:41:56.775221'), (175, 13, 'ITEM-BATCHQISTR20260512001-000003', '88725faf59e24d988b9cf1b791caeed7', '/api/trace/88725faf59e24d988b9cf1b791caeed7?sign=f810f71f1ca40cf8af6bca489853c0038038aed466b384ae7ecdd4a7411bcff9', 'f810f71f1ca40cf8af6bca489853c0038038aed466b384ae7ecdd4a7411bcff9', 1, 0, NULL, NULL, '2026-05-12 20:41:56.778749', '2026-05-12 20:41:56.778749'), (176, 13, 'ITEM-BATCHQISTR20260512001-000004', '800f361369b947e4a67db1e984ee410e', '/api/trace/800f361369b947e4a67db1e984ee410e?sign=7d48f767c843e722e4c6f009cd28ce80cea38025bb6a3c8c03fc9eb847cefad1', '7d48f767c843e722e4c6f009cd28ce80cea38025bb6a3c8c03fc9eb847cefad1', 1, 0, NULL, NULL, '2026-05-12 20:41:56.78114', '2026-05-12 20:41:56.78114'), (177, 13, 'ITEM-BATCHQISTR20260512001-000005', '7511d107796b4ec1b28e0d9b2f3f8717', '/api/trace/7511d107796b4ec1b28e0d9b2f3f8717?sign=3209aaa4a925c5e311ee4a2f208f9ffb367385d3b6ae45dfcdf886b64d13cb82', '3209aaa4a925c5e311ee4a2f208f9ffb367385d3b6ae45dfcdf886b64d13cb82', 1, 0, NULL, NULL, '2026-05-12 20:41:56.784021', '2026-05-12 20:41:56.784021'), (178, 13, 'ITEM-BATCHQISTR20260512001-000006', '68af8e1e8e3c409489f2b849a3f1f36b', '/api/trace/68af8e1e8e3c409489f2b849a3f1f36b?sign=f0efedddb9d990a0e30e61132f589edcedb18b1f485d9a8a70430c4fe8094fdd', 'f0efedddb9d990a0e30e61132f589edcedb18b1f485d9a8a70430c4fe8094fdd', 1, 0, NULL, NULL, '2026-05-12 20:41:56.786485', '2026-05-12 20:41:56.786485'), (179, 13, 'ITEM-BATCHQISTR20260512001-000007', 'efae97b9783d48488347ca406dadfe65', '/api/trace/efae97b9783d48488347ca406dadfe65?sign=f02bceb128382891c2e27237d0f53c1c67b09794751f8affd4d5d1f1af284b62', 'f02bceb128382891c2e27237d0f53c1c67b09794751f8affd4d5d1f1af284b62', 1, 0, NULL, NULL, '2026-05-12 20:41:56.788913', '2026-05-12 20:41:56.788913'), (180, 13, 'ITEM-BATCHQISTR20260512001-000008', 'e2022ccc12c447828ca8d15c12361ba5', '/api/trace/e2022ccc12c447828ca8d15c12361ba5?sign=d16d817f337272303220cc4a4db2a524754cccbb379ecffb8ac51ac1075c021d', 'd16d817f337272303220cc4a4db2a524754cccbb379ecffb8ac51ac1075c021d', 1, 0, NULL, NULL, '2026-05-12 20:41:56.790372', '2026-05-12 20:41:56.790372'), (181, 13, 'ITEM-BATCHQISTR20260512001-000009', 'fb5370a3eeea44a8acfd4e01dbf200e8', '/api/trace/fb5370a3eeea44a8acfd4e01dbf200e8?sign=b5bf543d68d6b780db2feacd388d7a06eed33f1e407b2fe2678b8e090801db85', 'b5bf543d68d6b780db2feacd388d7a06eed33f1e407b2fe2678b8e090801db85', 1, 0, NULL, NULL, '2026-05-12 20:41:56.791863', '2026-05-12 20:41:56.791863'), (182, 13, 'ITEM-BATCHQISTR20260512001-000010', '89efc78bf666484cb89e4cf1cf4f1d85', '/api/trace/89efc78bf666484cb89e4cf1cf4f1d85?sign=57fdae89da65b14cc552d14e5f15bb38cdcdcb0991126ae762f5d2eeb15a8626', '57fdae89da65b14cc552d14e5f15bb38cdcdcb0991126ae762f5d2eeb15a8626', 1, 0, NULL, NULL, '2026-05-12 20:41:56.793894', '2026-05-12 20:41:56.793894'), (183, 13, 'ITEM-BATCHQISTR20260512001-000011', '82d6b9b9909d45e48306d0f3a9ccba19', '/api/trace/82d6b9b9909d45e48306d0f3a9ccba19?sign=2d3a661291e6ff2427e59664cd303e132c921c7ce531ff72c36c7bfb61a23ad7', '2d3a661291e6ff2427e59664cd303e132c921c7ce531ff72c36c7bfb61a23ad7', 1, 0, NULL, NULL, '2026-05-12 20:41:56.795731', '2026-05-12 20:41:56.795731'), (184, 13, 'ITEM-BATCHQISTR20260512001-000012', '7534bfa6b41841a29d12cfbafa4b1cdc', '/api/trace/7534bfa6b41841a29d12cfbafa4b1cdc?sign=5a76b031fb2cebecbec2e12fb0874de44cf8fd5c93bc8f4b8c4c8740712ccf2b', '5a76b031fb2cebecbec2e12fb0874de44cf8fd5c93bc8f4b8c4c8740712ccf2b', 1, 0, NULL, NULL, '2026-05-12 20:41:56.797936', '2026-05-12 20:41:56.797936'), (185, 13, 'ITEM-BATCHQISTR20260512001-000013', '0c6d7f72be7140febf74c94a25c37b16', '/api/trace/0c6d7f72be7140febf74c94a25c37b16?sign=05caaf8fa3fe94d62f82e3382769e0a00ce5b48875839ae3886bf07b7ca1cb1c', '05caaf8fa3fe94d62f82e3382769e0a00ce5b48875839ae3886bf07b7ca1cb1c', 1, 0, NULL, NULL, '2026-05-12 20:41:56.800199', '2026-05-12 20:41:56.800199'), (186, 13, 'ITEM-BATCHQISTR20260512001-000014', 'b047f10eecbe4b28a9ca07095d73a40a', '/api/trace/b047f10eecbe4b28a9ca07095d73a40a?sign=4903bdf40820a944942746896b25c8da60335c60ed92d948f1e4ebce88c308e3', '4903bdf40820a944942746896b25c8da60335c60ed92d948f1e4ebce88c308e3', 1, 0, NULL, NULL, '2026-05-12 20:41:56.802308', '2026-05-12 20:41:56.802308'), (187, 13, 'ITEM-BATCHQISTR20260512001-000015', '038f4ef6b4704fb3b0a0a2295f0471bc', '/api/trace/038f4ef6b4704fb3b0a0a2295f0471bc?sign=c463586b542cb192f54045e6379618bf4654a658b246aca6607f55a541033984', 'c463586b542cb192f54045e6379618bf4654a658b246aca6607f55a541033984', 1, 0, NULL, NULL, '2026-05-12 20:41:56.804794', '2026-05-12 20:41:56.804794'), (188, 13, 'ITEM-BATCHQISTR20260512001-000016', '8d263681181046fe9a99dec37d488ab4', '/api/trace/8d263681181046fe9a99dec37d488ab4?sign=b8d2c149f72ffe31bf00667970fc0a0f740c01f323d5ee63057c91f1914ed27a', 'b8d2c149f72ffe31bf00667970fc0a0f740c01f323d5ee63057c91f1914ed27a', 1, 0, NULL, NULL, '2026-05-12 20:41:56.807876', '2026-05-12 20:41:56.807876'), (172, 12, 'ITEM-BATCHQIRICE20260512001-000030', 'cbf40659f5fa48a0b9d00a4c389309c0', '/api/trace/cbf40659f5fa48a0b9d00a4c389309c0?sign=ef3c194e712b854bc3846a48921a8d6da8f308435bce9ed135a54c3a34dd9f04', 'ef3c194e712b854bc3846a48921a8d6da8f308435bce9ed135a54c3a34dd9f04', 1, 3, '2026-05-12 20:44:02.246542', '2026-05-12 20:44:32.476019', '2026-05-12 20:30:35.142153', '2026-05-12 20:44:32.476019');
COMMIT;

-- ----------------------------
-- Table structure for production_record
-- ----------------------------
DROP TABLE IF EXISTS "production_record";
CREATE TABLE "production_record" (
  "id" int8 NOT NULL DEFAULT nextval('production_record_id_seq'::regclass),
  "batch_id" int8 NOT NULL,
  "record_type" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "operation_time" timestamp(6) NOT NULL,
  "operator_name" varchar(64) COLLATE "pg_catalog"."default",
  "material_name" varchar(128) COLLATE "pg_catalog"."default",
  "dosage" varchar(64) COLLATE "pg_catalog"."default",
  "content" text COLLATE "pg_catalog"."default" NOT NULL,
  "attachment_url" varchar(255) COLLATE "pg_catalog"."default",
  "created_at" timestamp(6) NOT NULL DEFAULT pg_systimestamp()
)
;

-- ----------------------------
-- Records of production_record
-- ----------------------------
BEGIN;
INSERT INTO "production_record" ("id", "batch_id", "record_type", "operation_time", "operator_name", "material_name", "dosage", "content", "attachment_url", "created_at") VALUES (1, 1, '测试', '2026-03-04 00:00:00', '', '', '', '测试', '/uploads/9589a2c02a1641fea6725ed358163891.png', '2026-04-15 15:18:21.356383'), (2, 1, '采收', '2026-04-08 00:00:00', 'yujia', '1', '1', 'test', '/uploads/a099bcfdd86e4fbea6c7270be17013d3.png', '2026-04-15 15:20:16.515553'), (3, 2, '每平方浇水记录', '2026-04-15 00:00:00', '', '', '', '记录', '/uploads/96cbfc869b2843a486a570b5e8559d1c.doc', '2026-04-15 15:28:46.310866'), (4, 3, 'shadius', '2026-04-08 00:00:00', 'sadad', '', '', 'qwesdad', '/uploads/ace8a48ee7514f36b2648ec5e447fe30.json', '2026-04-15 22:19:25.439198'), (5, 4, '施肥', '2026-04-14 00:00:00', '', '', '', '施肥记录', '/uploads/ce1bd2ecaaf440378ce678c9f6db6b14.docx', '2026-04-27 16:50:50.399057'), (6, 7, '浇水', '2026-04-26 00:00:00', '', '', '', '426浇水', '', '2026-04-27 17:28:19.110306'), (7, 9, '播种', '2026-03-01 08:30:00', '王海涛', '脱毒草莓苗', '800 株', '完成 A 区草莓苗定植，并建立批次标识。', '', '2026-05-07 10:26:25.224172'), (8, 9, '施肥', '2026-03-18 09:20:00', '李春霞', '有机复合肥', '25kg', '完成生长期追肥，并记录土壤墒情。', '', '2026-05-07 10:26:25.229672'), (9, 9, '病虫害防治', '2026-04-06 15:00:00', '孙志刚', '生物防治药剂', '2L', '例行病虫害巡检，未发现重大异常，完成预防性处理。', '', '2026-05-07 10:26:25.232111'), (10, 9, '采收', '2026-05-07 06:40:00', '张秀兰', '采收周转箱', '60 个', '完成成熟果实采收、分拣和预冷入库。', '', '2026-05-07 10:26:25.234826'), (11, 10, '育苗定植', '2026-02-12 09:00:00', '王建国', '樱桃番茄种苗', '18000 株', '完成温室番茄育苗定植，苗情整齐，建立批次标识。', '', '2026-05-12 20:07:00.788532'), (12, 10, '追肥管理', '2026-03-05 08:30:00', '刘德海', '水溶肥 20-20-20', '25 kg', '滴灌追肥一次，促进坐果与均匀生长。', '', '2026-05-12 20:07:00.793741'), (13, 10, '病虫防治', '2026-04-06 07:40:00', '刘德海', '黄板与生物菌剂', '黄板 60 张', '开展白粉虱绿色防控并补充生物菌剂喷施。', '', '2026-05-12 20:07:00.796889'), (14, 10, '采收分拣', '2026-05-12 05:10:00', '王建国', '人工分拣筐', '120 个', '清晨分批采收并完成一级果分拣与装箱。', '', '2026-05-12 20:07:00.799329'), (15, 11, '整地施肥', '2026-03-09 08:00:00', '李秀兰', '有机基肥', '800 kg', '完成叶菜区翻耕整地并施入基肥。', '', '2026-05-12 20:10:04.118178'), (16, 11, '播种育苗', '2026-03-12 09:20:00', '李秀兰', '奶油生菜种子', '6 kg', '穴盘播种后移入育苗棚，湿度保持稳定。', '', '2026-05-12 20:10:04.120759'), (17, 11, '灌溉管理', '2026-04-08 07:50:00', '周小燕', '净化灌溉水', '18 吨', '按计划进行喷灌补水，控制叶面洁净。', '', '2026-05-12 20:10:04.122535'), (18, 11, '采收预冷', '2026-05-12 04:50:00', '周小燕', '周转筐', '90 个', '采收后 30 分钟内完成预冷并装箱。', '', '2026-05-12 20:10:04.124432'), (19, 12, '育秧', '2025-10-08 09:30:00', '孙德昌', '优选稻种', '320 kg', '完成浸种催芽与秧盘育秧。', '', '2026-05-12 20:30:35.013745'), (20, 12, '田间管理', '2025-12-16 08:50:00', '马洪涛', '有机肥', '1500 kg', '返青期补充有机肥并巡检田块长势。', '', '2026-05-12 20:30:35.016011'), (21, 12, '收割', '2026-05-12 06:20:00', '孙德昌', '联合收割机', '2 台', '完成整块收割并运至烘干中心。', '', '2026-05-12 20:30:35.017548'), (22, 12, '加工包装', '2026-05-12 15:00:00', '马洪涛', '真空包装袋', '6000 个', '完成碾米、分级与 5kg 成品包装。', '', '2026-05-12 20:30:35.021026'), (23, 13, '定植', '2026-01-20 10:00:00', '赵海峰', '章姬草莓苗', '12500 株', '高架基质槽定植完成，开始温湿度联控。', '', '2026-05-12 20:41:56.716167'), (24, 13, '施肥', '2026-02-28 08:10:00', '赵海峰', '腐殖酸水溶肥', '18 kg', '花期前补肥一次，提高坐果率。', '', '2026-05-12 20:41:56.720083'), (25, 13, '品质管理', '2026-04-15 09:00:00', '于佳宁', '糖度抽检工具', '3 套', '采前抽检糖度与果径，执行分级标准。', '', '2026-05-12 20:41:56.723856'), (26, 13, '采收', '2026-05-12 05:00:00', '于佳宁', '冷藏周转箱', '110 个', '草莓分棚采收并立即入冷库预冷。', '', '2026-05-12 20:41:56.727429');
COMMIT;

-- ----------------------------
-- Table structure for recall_record
-- ----------------------------
DROP TABLE IF EXISTS "recall_record";
CREATE TABLE "recall_record" (
  "id" int8 NOT NULL DEFAULT nextval('recall_record_id_seq'::regclass),
  "batch_id" int8 NOT NULL,
  "recall_level" int2 NOT NULL DEFAULT 1,
  "reason" text COLLATE "pg_catalog"."default" NOT NULL,
  "recall_status" int2 NOT NULL DEFAULT 1,
  "notice_time" timestamp(6) NOT NULL DEFAULT pg_systimestamp(),
  "closed_at" timestamp(6),
  "created_at" timestamp(6) NOT NULL DEFAULT pg_systimestamp()
)
;

-- ----------------------------
-- Records of recall_record
-- ----------------------------
BEGIN;
INSERT INTO "recall_record" ("id", "batch_id", "recall_level", "reason", "recall_status", "notice_time", "closed_at", "created_at") VALUES (1, 1, 2, 'test', 1, '2026-04-15 15:29:29.718406', NULL, '2026-04-15 15:29:29.444327'), (2, 3, 2, '我发给和你见面
', 0, '2026-04-15 22:20:31.08791', '2026-04-20 21:35:24.982122', '2026-04-15 22:20:31.092001'), (4, 2, 2, 'sdfgh', 1, '2026-04-20 21:38:54.629405', NULL, '2026-04-20 21:38:47.882355'), (3, 4, 3, '798465', 0, '2026-04-16 11:12:08.393464', '2026-04-27 14:17:28.859199', '2026-04-16 11:12:07.780944'), (5, 7, 1, '产品存在问题', 1, '2026-04-27 17:31:53.686072', NULL, '2026-04-27 17:31:53.694234'), (6, 8, 1, '由反馈 #13 触发召回：我的草莓发霉了！！', 1, '2026-05-08 20:24:29.255896', NULL, '2026-05-08 20:24:26.849788'), (7, 8, 1, '可能存在农残。', 1, '2026-05-10 14:22:36.058533', NULL, '2026-05-10 14:22:36.067279'), (8, 8, 1, '由反馈#15 触发召回，我发现我的这个草莓有问题的，发霉了', 1, '2026-05-12 19:46:39.622968', NULL, '2026-05-12 19:46:39.09915');
COMMIT;

-- ----------------------------
-- Table structure for scan_log
-- ----------------------------
DROP TABLE IF EXISTS "scan_log";
CREATE TABLE "scan_log" (
  "id" int8 NOT NULL DEFAULT nextval('scan_log_id_seq'::regclass),
  "trace_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "item_id" int8,
  "batch_id" int8,
  "scan_source" varchar(32) COLLATE "pg_catalog"."default",
  "ip_address" varchar(64) COLLATE "pg_catalog"."default",
  "user_agent" varchar(255) COLLATE "pg_catalog"."default",
  "verify_result" int2 NOT NULL DEFAULT 1,
  "risk_message" varchar(255) COLLATE "pg_catalog"."default",
  "scanned_at" timestamp(6) NOT NULL DEFAULT pg_systimestamp()
)
;

-- ----------------------------
-- Records of scan_log
-- ----------------------------
BEGIN;
INSERT INTO "scan_log" ("id", "trace_id", "item_id", "batch_id", "scan_source", "ip_address", "user_agent", "verify_result", "risk_message", "scanned_at") VALUES (1, '521435a02e1a45beb2962bd5c780d289', 10, 8, 'MINIAPP', NULL, NULL, 1, NULL, '2026-05-06 19:07:46.086874'), (2, '521435a02e1a45beb2962bd5c780d289', 10, 8, 'MINIAPP', NULL, NULL, 1, NULL, '2026-05-06 19:07:47.340581'), (3, '521435a02e1a45beb2962bd5c780d289', 10, 8, 'MINIAPP', NULL, NULL, 1, NULL, '2026-05-08 20:22:23.681134'), (4, '521435a02e1a45beb2962bd5c780d289', 10, 8, 'MINIAPP', NULL, NULL, 1, NULL, '2026-05-08 20:22:24.265662'), (5, '521435a02e1a45beb2962bd5c780d289', 10, 8, 'MINIAPP', NULL, NULL, 1, NULL, '2026-05-08 20:22:24.270506'), (7, '521435a02e1a45beb2962bd5c780d289', 10, 8, 'MINIAPP', NULL, NULL, 0, '该码扫码次数较多，请核对包装信息', '2026-05-08 20:22:35.397779'), (6, '521435a02e1a45beb2962bd5c780d289', 10, 8, 'MINIAPP', NULL, NULL, 0, '该码扫码次数较多，请核对包装信息', '2026-05-08 20:22:35.397744'), (8, '521435a02e1a45beb2962bd5c780d289', 10, 8, 'MINIAPP', NULL, NULL, 0, '该码扫码次数较多，请核对包装信息', '2026-05-08 20:23:37.064095'), (9, '521435a02e1a45beb2962bd5c780d289', 10, 8, 'MINIAPP', NULL, NULL, 0, '该码扫码次数较多，请核对包装信息', '2026-05-08 20:23:37.837289'), (10, '521435a02e1a45beb2962bd5c780d289', 10, 8, 'MINIAPP', NULL, NULL, 0, '该码扫码次数较多，请核对包装信息', '2026-05-08 20:23:37.837734'), (14, '8c3d51b8f2b741abb2e7c752444f1cc9', NULL, 3, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '溯源码签名异常，存在伪造风险', '2026-05-12 16:08:27.103985'), (15, '8c3d51b8f2b741abb2e7c752444f1cc9', NULL, 3, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '溯源码签名异常，存在伪造风险', '2026-05-12 16:08:33.690328'), (16, '8c3d51b8f2b741abb2e7c752444f1cc9', NULL, 3, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '溯源码签名异常，存在伪造风险', '2026-05-12 16:08:34.075579'), (17, '8c3d51b8f2b741abb2e7c752444f1cc9', NULL, 3, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '溯源码签名异常，存在伪造风险', '2026-05-12 16:08:34.428659'), (18, '8c3d51b8f2b741abb2e7c752444f1cc9', NULL, 3, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '溯源码签名异常，存在伪造风险', '2026-05-12 16:09:27.738199'), (19, '521435a02e1a45beb2962bd5c780d289', 10, 8, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '该批次已被召回，请停止食用并联系商家', '2026-05-12 16:09:59.417012'), (20, '521435a02e1a45beb2962bd5c780d289', 10, 8, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '该批次已被召回，请停止食用并联系商家', '2026-05-12 16:09:59.87688'), (21, '521435a02e1a45beb2962bd5c780d289', 10, 8, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '该批次已被召回，请停止食用并联系商家', '2026-05-12 16:10:02.705075'), (22, '521435a02e1a45beb2962bd5c780d289', 10, 8, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '该批次已被召回，请停止食用并联系商家', '2026-05-12 19:42:25.584947'), (23, '521435a02e1a45beb2962bd5c780d289', 10, 8, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '该批次已被召回，请停止食用并联系商家', '2026-05-12 19:42:28.133901'), (24, '521435a02e1a45beb2962bd5c780d289', 10, 8, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '该批次已被召回，请停止食用并联系商家', '2026-05-12 19:42:36.103158'), (25, '521435a02e1a45beb2962bd5c780d289', 10, 8, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '该批次已被召回，请停止食用并联系商家', '2026-05-12 19:51:15.242183'), (26, '521435a02e1a45beb2962bd5c780d289', 10, 8, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '该批次已被召回，请停止食用并联系商家', '2026-05-12 19:51:16.572773'), (27, '8c3d51b8f2b741abb2e7c752444f1cc9', NULL, 3, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '溯源码签名异常，存在伪造风险', '2026-05-12 19:51:43.954945'), (28, '8c3d51b8f2b741abb2e7c752444f1cc9', NULL, 3, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '溯源码签名异常，存在伪造风险', '2026-05-12 19:51:44.465879'), (29, '8c3d51b8f2b741abb2e7c752444f1cc9', NULL, 3, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '溯源码签名异常，存在伪造风险', '2026-05-12 20:03:28.38335'), (30, '8c3d51b8f2b741abb2e7c752444f1cc9', NULL, 3, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '溯源码签名异常，存在伪造风险', '2026-05-12 20:03:28.856514'), (31, '521435a02e1a45beb2962bd5c780d289', 10, 8, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '该批次已被召回，请停止食用并联系商家', '2026-05-12 20:03:45.942348'), (32, '521435a02e1a45beb2962bd5c780d289', 10, 8, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '该批次已被召回，请停止食用并联系商家', '2026-05-12 20:03:46.312272'), (39, '685244cb64e44be5bacc1150ac1f5ee4', 123, 10, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 1, NULL, '2026-05-12 20:08:39.078377'), (33, 'f7ef48fe3f414fd48251ac51a5e49a58', NULL, 2, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '溯源码签名异常，存在伪造风险', '2026-05-12 20:03:53.84618'), (34, 'f7ef48fe3f414fd48251ac51a5e49a58', NULL, 2, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '溯源码签名异常，存在伪造风险', '2026-05-12 20:03:54.239007'), (35, 'c944ddd2b79b42519dd69b819f993c46', 124, 10, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 1, NULL, '2026-05-12 20:08:18.987323'), (38, '685244cb64e44be5bacc1150ac1f5ee4', 123, 10, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 1, NULL, '2026-05-12 20:08:28.534461'), (36, 'c944ddd2b79b42519dd69b819f993c46', 124, 10, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 1, NULL, '2026-05-12 20:08:19.437998'), (37, '685244cb64e44be5bacc1150ac1f5ee4', 123, 10, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 1, NULL, '2026-05-12 20:08:27.366889'), (40, '685244cb64e44be5bacc1150ac1f5ee4', 123, 10, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 1, NULL, '2026-05-12 20:28:07.776774'), (41, '685244cb64e44be5bacc1150ac1f5ee4', 123, 10, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 1, NULL, '2026-05-12 20:28:08.381444'), (42, '685244cb64e44be5bacc1150ac1f5ee4', 123, 10, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '该码扫码次数较多，请核对包装信息', '2026-05-12 20:28:22.046208'), (43, '685244cb64e44be5bacc1150ac1f5ee4', 123, 10, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '该码扫码次数较多，请核对包装信息', '2026-05-12 20:32:07.104021'), (44, '685244cb64e44be5bacc1150ac1f5ee4', 123, 10, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '该码扫码次数较多，请核对包装信息', '2026-05-12 20:32:07.49883'), (45, '685244cb64e44be5bacc1150ac1f5ee4', 123, 10, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '该码扫码次数较多，请核对包装信息', '2026-05-12 20:32:10.373544'), (46, '685244cb64e44be5bacc1150ac1f5ee4', 123, 10, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '该码扫码次数较多，请核对包装信息', '2026-05-12 20:32:15.889056'), (47, '521435a02e1a45beb2962bd5c780d289', 10, 8, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '该批次已被召回，请停止食用并联系商家', '2026-05-12 20:34:08.266082'), (48, '521435a02e1a45beb2962bd5c780d289', 10, 8, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '该批次已被召回，请停止食用并联系商家', '2026-05-12 20:34:08.92989'), (49, 'cbf40659f5fa48a0b9d00a4c389309c0', 172, 12, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 1, NULL, '2026-05-12 20:44:02.242079'), (50, 'cbf40659f5fa48a0b9d00a4c389309c0', 172, 12, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 1, NULL, '2026-05-12 20:44:03.037864'), (51, 'cbf40659f5fa48a0b9d00a4c389309c0', 172, 12, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 1, NULL, '2026-05-12 20:44:32.469801'), (52, '521435a02e1a45beb2962bd5c780d289', 10, 8, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '该批次已被召回，请停止食用并联系商家', '2026-05-12 20:45:22.358575'), (53, '521435a02e1a45beb2962bd5c780d289', 10, 8, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '该批次已被召回，请停止食用并联系商家', '2026-05-12 20:45:22.905645'), (54, 'f7ef48fe3f414fd48251ac51a5e49a58', NULL, 2, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '溯源码签名异常，存在伪造风险', '2026-05-12 20:45:59.393871'), (55, 'f7ef48fe3f414fd48251ac51a5e49a58', NULL, 2, 'MINIAPP', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/475519892 sid/AZk0WTlamw token/408715cc72', 0, '溯源码签名异常，存在伪造风险', '2026-05-12 20:45:59.99266');
COMMIT;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS "sys_role";
CREATE TABLE "sys_role" (
  "id" int8 NOT NULL DEFAULT nextval('sys_role_id_seq'::regclass),
  "role_code" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "role_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "created_at" timestamp(6) NOT NULL DEFAULT pg_systimestamp()
)
;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
BEGIN;
INSERT INTO "sys_role" ("id", "role_code", "role_name", "remark", "created_at") VALUES (1, 'ADMIN', '系统管理员', '默认管理员角色', '2026-04-14 11:23:30.667295'), (2, 'OPERATOR', '业务员', '默认业务操作角色', '2026-04-14 11:24:47.280243'), (3, 'USER', '小程序用户', '默认小程序用户角色', '2026-04-16 08:36:04.121026');
COMMIT;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS "sys_user";
CREATE TABLE "sys_user" (
  "id" int8 NOT NULL DEFAULT nextval('sys_user_id_seq'::regclass),
  "username" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "password" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "real_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "phone" varchar(20) COLLATE "pg_catalog"."default",
  "status" int2 NOT NULL DEFAULT 1,
  "created_at" timestamp(6) NOT NULL DEFAULT pg_systimestamp(),
  "updated_at" timestamp(6) NOT NULL DEFAULT pg_systimestamp(),
  "openid" varchar(100) COLLATE "pg_catalog"."default",
  "company_id" int8
)
;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO "sys_user" ("id", "username", "password", "real_name", "phone", "status", "created_at", "updated_at", "openid", "company_id") VALUES (1, 'admin', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'admin', '13800000000', 1, '2026-04-15 11:15:28.245775', '2026-04-15 16:10:46.489306', NULL, 1), (10, 'yujiaweixin', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '雨佳', '19945866325', 1, '2026-04-16 16:27:16.312564', '2026-04-16 16:40:04.320755', 'MOCK_OPENID_0c3D09ll2Dt9yh4d6Mml24RWBp1D09lc', 1), (11, '王二', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '王二', '12345678994', 1, '2026-04-16 16:41:14.971824', '2026-04-16 16:41:14.971824', NULL, 1), (12, '张三', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '张三', '', 1, '2026-04-16 16:48:11.817795', '2026-04-16 16:49:04.798788', NULL, 1), (13, '雨佳', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '雨佳', '', 1, '2026-04-16 16:49:28.319158', '2026-04-16 16:49:49.378235', 'MOCK_OPENID_0c341AFa1PfUxL05w4Ha1LEn1e341AFM', 1), (14, 'zhangsan', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '张三', '', 1, '2026-04-27 16:13:10.184957', '2026-04-27 16:53:26.51045', NULL, 1), (15, 'xiaochengxu', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'xiaochengxu', '', 1, '2026-05-08 20:23:29.164741', '2026-05-08 20:23:29.164741', NULL, 1), (2, 'yujia', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '罗雨佳', '12345678900', 1, '2026-04-15 15:30:04.855852', '2026-05-08 21:03:13.191905', NULL, 1), (16, 'wx_8cdfb10d', 'd7539cebb57f79952837b2d875fa0e25033da62e661e058a4a32eeb99d288ee6', '微信用户', NULL, 1, '2026-05-12 15:54:06.782701', '2026-05-12 15:54:06.782701', 'WX_OPENID_d718467b27a838b78f980aa08cdfb10d', NULL), (17, 'yj', 'ea2cd6783250ffde96c5e8f8db81b386a9d9965ceef1c37017872315dc7e4c3d', '雨佳', '', 1, '2026-05-12 15:54:22.903982', '2026-05-12 15:54:22.903982', NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS "sys_user_role";
CREATE TABLE "sys_user_role" (
  "user_id" int8 NOT NULL,
  "role_id" int8 NOT NULL
)
;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
BEGIN;
INSERT INTO "sys_user_role" ("user_id", "role_id") VALUES (2, 2), (1, 1), (10, 2), (11, 3), (12, 3), (13, 3), (14, 2), (15, 2), (16, 3), (17, 3);
COMMIT;

-- ----------------------------
-- Table structure for system_task
-- ----------------------------
DROP TABLE IF EXISTS "system_task";
CREATE TABLE "system_task" (
  "id" int8 NOT NULL DEFAULT nextval('system_task_id_seq'::regclass),
  "task_type" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "biz_type" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "biz_id" int8 NOT NULL,
  "title" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "description" varchar(500) COLLATE "pg_catalog"."default",
  "priority" int2 NOT NULL DEFAULT 3,
  "status" int2 NOT NULL DEFAULT 0,
  "assignee_user_id" int8,
  "source_type" varchar(32) COLLATE "pg_catalog"."default" NOT NULL DEFAULT 'SYSTEM'::character varying,
  "due_at" timestamp(6),
  "completed_at" timestamp(6),
  "created_at" timestamp(6) NOT NULL DEFAULT pg_systimestamp(),
  "updated_at" timestamp(6) NOT NULL DEFAULT pg_systimestamp(),
  "claimed_at" timestamp(6),
  "completed_by_user_id" int8
)
;
COMMENT ON COLUMN "system_task"."claimed_at" IS '任务认领时间';
COMMENT ON COLUMN "system_task"."completed_by_user_id" IS '任务完成人用户ID';

-- ----------------------------
-- Records of system_task
-- ----------------------------
BEGIN;
INSERT INTO "system_task" ("id", "task_type", "biz_type", "biz_id", "title", "description", "priority", "status", "assignee_user_id", "source_type", "due_at", "completed_at", "created_at", "updated_at", "claimed_at", "completed_by_user_id") VALUES (27, 'ABNORMAL_SCAN', 'BATCH', 10, '排查异常扫码 - BATCH-QI-TOM-20260512-001', '该批次异常扫码次数偏高，请核验防伪和流通状态。', 1, 0, NULL, 'SYSTEM', '2026-05-13 20:52:22.047519', NULL, '2026-05-12 20:32:38.380102', '2026-05-12 20:52:23.27786', NULL, NULL), (2, 'MISSING_INSPECTION', 'BATCH', 8, '补上传质检报告 - BATCH-20260506-0001', '该批次缺少质检报告，消费者无法查看质量证明。', 1, 0, NULL, 'SYSTEM', '2026-05-13 20:52:22.145784', NULL, '2026-05-06 20:25:25.549394', '2026-05-12 20:52:23.375162', NULL, NULL), (22, 'HIGH_PRIORITY_FEEDBACK', 'BATCH', 8, '处理高优反馈 - BATCH-20260506-0001', '该批次存在高优先级反馈，需要尽快跟进处理。', 1, 0, 1, 'SYSTEM', '2026-05-13 20:52:22.15223', NULL, '2026-05-08 21:03:23.817321', '2026-05-12 20:52:23.382009', '2026-05-08 21:03:25.532532', NULL), (23, 'ABNORMAL_SCAN', 'BATCH', 8, '排查异常扫码 - BATCH-20260506-0001', '该批次异常扫码次数偏高，请核验防伪和流通状态。', 1, 2, 2, 'SYSTEM', '2026-05-13 20:52:22.158819', '2026-05-10 13:56:46.229115', '2026-05-08 21:03:23.826064', '2026-05-12 20:52:23.388625', '2026-05-08 21:03:30.822467', 2), (30, 'HIGH_PRIORITY_FEEDBACK', 'BATCH', 10, '处理高优反馈 - BATCH-QI-TOM-20260512-001', '该批次存在高优先级反馈，需要尽快跟进处理。', 1, 0, NULL, 'SYSTEM', '2026-05-13 20:52:22.04037', NULL, '2026-05-12 20:50:16.301167', '2026-05-12 20:52:23.270446', NULL, NULL), (5, 'LOW_COMPLETENESS', 'BATCH', 7, '提升档案完整度 - 云南黄瓜产物', '该批次档案完整度低于 70%，建议优先补充关键信息。', 2, 3, NULL, 'SYSTEM', '2026-05-15 19:44:45.135361', NULL, '2026-05-06 20:25:25.622196', '2026-05-12 19:45:27.900843', NULL, NULL), (1, 'MISSING_PRODUCTION', 'BATCH', 8, '补录生产记录 - BATCH-20260506-0001', '该批次尚未录入生产记录，会影响追溯完整度。', 2, 0, NULL, 'SYSTEM', '2026-05-15 20:52:22.138393', NULL, '2026-05-06 20:25:25.536967', '2026-05-12 20:52:23.36844', NULL, NULL), (3, 'LOW_COMPLETENESS', 'BATCH', 8, '提升档案完整度 - BATCH-20260506-0001', '该批次档案完整度低于 70%，建议优先补充关键信息。', 2, 0, NULL, 'SYSTEM', '2026-05-15 20:52:22.164382', NULL, '2026-05-06 20:25:25.567931', '2026-05-12 20:52:23.393819', NULL, NULL), (4, 'MISSING_LOGISTICS', 'BATCH', 7, '补录物流节点 - 云南黄瓜产物', '该批次暂无流通记录，建议至少补齐一个物流节点。', 2, 0, NULL, 'SYSTEM', '2026-05-15 20:52:22.201956', NULL, '2026-05-06 20:25:25.614179', '2026-05-12 20:52:23.430867', NULL, NULL), (6, 'MISSING_PRODUCTION', 'BATCH', 6, '补录生产记录 - sefzxd', '该批次尚未录入生产记录，会影响追溯完整度。', 2, 0, NULL, 'SYSTEM', '2026-05-15 20:52:22.241047', NULL, '2026-05-06 20:25:25.658649', '2026-05-12 20:52:23.469865', NULL, NULL), (8, 'MISSING_LOGISTICS', 'BATCH', 6, '补录物流节点 - sefzxd', '该批次暂无流通记录，建议至少补齐一个物流节点。', 2, 0, NULL, 'SYSTEM', '2026-05-15 20:52:22.253953', NULL, '2026-05-06 20:25:25.675146', '2026-05-12 20:52:23.483234', NULL, NULL), (7, 'MISSING_INSPECTION', 'BATCH', 6, '补上传质检报告 - sefzxd', '该批次缺少质检报告，消费者无法查看质量证明。', 1, 1, 1, 'SYSTEM', '2026-05-13 20:52:22.247729', NULL, '2026-05-06 20:25:25.667661', '2026-05-12 20:52:23.47669', '2026-05-10 14:26:01.132256', NULL), (9, 'LOW_COMPLETENESS', 'BATCH', 6, '提升档案完整度 - sefzxd', '该批次档案完整度低于 70%，建议优先补充关键信息。', 2, 0, NULL, 'SYSTEM', '2026-05-15 20:52:22.259918', NULL, '2026-05-06 20:25:25.683771', '2026-05-12 20:52:23.488452', NULL, NULL), (10, 'MISSING_PRODUCTION', 'BATCH', 5, '补录生产记录 - 54861', '该批次尚未录入生产记录，会影响追溯完整度。', 2, 0, NULL, 'SYSTEM', '2026-05-15 20:52:22.295923', NULL, '2026-05-06 20:25:25.720247', '2026-05-12 20:52:23.524545', NULL, NULL), (11, 'MISSING_INSPECTION', 'BATCH', 5, '补上传质检报告 - 54861', '该批次缺少质检报告，消费者无法查看质量证明。', 1, 2, 1, 'SYSTEM', '2026-05-13 20:52:22.302843', '2026-05-06 21:20:35.086988', '2026-05-06 20:25:25.729378', '2026-05-12 20:52:23.531617', '2026-05-06 21:20:28.959213', 1), (12, 'MISSING_LOGISTICS', 'BATCH', 5, '补录物流节点 - 54861', '该批次暂无流通记录，建议至少补齐一个物流节点。', 2, 0, NULL, 'SYSTEM', '2026-05-15 20:52:22.308581', NULL, '2026-05-06 20:25:25.737622', '2026-05-12 20:52:23.538034', NULL, NULL), (13, 'LOW_COMPLETENESS', 'BATCH', 5, '提升档案完整度 - 54861', '该批次档案完整度低于 70%，建议优先补充关键信息。', 2, 0, NULL, 'SYSTEM', '2026-05-15 20:52:22.315293', NULL, '2026-05-06 20:25:25.746335', '2026-05-12 20:52:23.543791', NULL, NULL), (14, 'MISSING_LOGISTICS', 'BATCH', 4, '补录物流节点 - 3', '该批次暂无流通记录，建议至少补齐一个物流节点。', 2, 0, NULL, 'SYSTEM', '2026-05-15 20:52:22.353228', NULL, '2026-05-06 20:25:25.77902', '2026-05-12 20:52:23.581338', NULL, NULL), (15, 'LOW_COMPLETENESS', 'BATCH', 4, '提升档案完整度 - 3', '该批次档案完整度低于 70%，建议优先补充关键信息。', 2, 0, NULL, 'SYSTEM', '2026-05-15 20:52:22.359754', NULL, '2026-05-06 20:25:25.785969', '2026-05-12 20:52:23.587346', NULL, NULL), (16, 'MISSING_LOGISTICS', 'BATCH', 3, '补录物流节点 - test14', '该批次暂无流通记录，建议至少补齐一个物流节点。', 2, 0, NULL, 'SYSTEM', '2026-05-15 20:52:22.39145', NULL, '2026-05-06 20:25:25.823989', '2026-05-12 20:52:23.618969', NULL, NULL), (25, 'ABNORMAL_SCAN', 'BATCH', 3, '排查异常扫码 - test14', '该批次异常扫码次数偏高，请核验防伪和流通状态。', 1, 0, NULL, 'SYSTEM', '2026-05-13 20:52:22.395748', NULL, '2026-05-12 19:24:50.604833', '2026-05-12 20:52:23.623545', NULL, NULL), (17, 'LOW_COMPLETENESS', 'BATCH', 3, '提升档案完整度 - test14', '该批次档案完整度低于 70%，建议优先补充关键信息。', 2, 0, NULL, 'SYSTEM', '2026-05-15 20:52:22.401513', NULL, '2026-05-06 20:25:25.832635', '2026-05-12 20:52:23.629273', NULL, NULL), (18, 'MISSING_LOGISTICS', 'BATCH', 2, '补录物流节点 - BATCH-212', '该批次暂无流通记录，建议至少补齐一个物流节点。', 2, 0, NULL, 'SYSTEM', '2026-05-15 20:52:22.435367', NULL, '2026-05-06 20:25:25.869153', '2026-05-12 20:52:23.662566', NULL, NULL), (31, 'ABNORMAL_SCAN', 'BATCH', 2, '排查异常扫码 - BATCH-212', '该批次异常扫码次数偏高，请核验防伪和流通状态。', 1, 0, NULL, 'SYSTEM', '2026-05-13 20:52:22.440976', NULL, '2026-05-12 20:50:16.67162', '2026-05-12 20:52:23.668272', NULL, NULL), (19, 'LOW_COMPLETENESS', 'BATCH', 2, '提升档案完整度 - BATCH-212', '该批次档案完整度低于 70%，建议优先补充关键信息。', 2, 0, NULL, 'SYSTEM', '2026-05-15 20:52:22.446104', NULL, '2026-05-06 20:25:25.877353', '2026-05-12 20:52:23.674029', NULL, NULL), (20, 'MISSING_LOGISTICS', 'BATCH', 1, '补录物流节点 - BATCH-001', '该批次暂无流通记录，建议至少补齐一个物流节点。', 2, 0, NULL, 'SYSTEM', '2026-05-15 20:52:22.483551', NULL, '2026-05-06 20:25:25.920133', '2026-05-12 20:52:23.710505', NULL, NULL), (29, 'HIGH_RISK_FEEDBACK', 'FEEDBACK', 16, '处理高风险反馈#16', 'AI 判定为高风险反馈，请优先处理。分类=质量，溯源码=685244cb64e44be5bacc1150ac1f5ee4，内容=圣女果坏掉了，不能吃', 1, 0, NULL, 'FEEDBACK_AI', '2026-05-13 08:42:48.59741', NULL, '2026-05-12 20:42:49.236572', '2026-05-12 20:52:23.723942', NULL, NULL), (26, 'HIGH_RISK_FEEDBACK', 'FEEDBACK', 15, '处理高风险反馈#15', 'AI 判定为高风险反馈，请优先处理。分类=质量，溯源码=521435a02e1a45beb2962bd5c780d289，内容=我发现我的这个草莓有问题的，发霉了', 1, 2, 2, 'FEEDBACK_AI', '2026-05-13 07:39:05.341956', '2026-05-12 19:46:55.096301', '2026-05-12 19:39:06.072919', '2026-05-12 20:52:23.73026', '2026-05-12 19:46:55.096301', 1), (24, 'HIGH_RISK_FEEDBACK', 'FEEDBACK', 14, '处理高风险反馈#14', 'AI 判定为高风险反馈，请优先处理。分类=质量，溯源码=521435a02e1a45beb2962bd5c780d289，内容=草莓有很重的药味道，小孩吃了一直拉肚子。', 1, 2, 1, 'FEEDBACK_AI', '2026-05-11 02:20:05.972221', '2026-05-12 15:35:31.782574', '2026-05-10 14:20:05.970518', '2026-05-12 20:52:23.736567', '2026-05-10 14:20:05.972221', NULL), (21, 'HIGH_RISK_FEEDBACK', 'FEEDBACK', 13, '处理高风险反馈 #13', 'AI 判定为高风险反馈，请优先处理。分类=质量，溯源码=521435a02e1a45beb2962bd5c780d289，内容=我的草莓发霉了！！', 1, 2, 1, 'FEEDBACK_AI', '2026-05-09 08:23:55.494397', '2026-05-08 20:24:39.344872', '2026-05-08 20:23:52.614429', '2026-05-12 20:52:23.743342', '2026-05-08 20:23:55.494397', 1), (28, 'IMPORT_RISK_REVIEW', 'BATCH', 13, '处理异常导入批次 - IR-QI-STR-20260512-001', '快速导入的批次质检结果异常，请优先复核。报告编号=IR-QI-STR-20260512-001；结论：复检发现个别包装箱冷链波动明显，存在品质衰减风险，建议立即复核并暂停流通。', 1, 0, NULL, 'QUICK_IMPORT', '2026-05-13 08:52:22.525103', NULL, '2026-05-12 20:41:56.814533', '2026-05-12 20:52:23.751603', NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for trace_code
-- ----------------------------
DROP TABLE IF EXISTS "trace_code";
CREATE TABLE "trace_code" (
  "id" int8 NOT NULL DEFAULT nextval('trace_code_id_seq'::regclass),
  "trace_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "batch_id" int8 NOT NULL,
  "qr_content" text COLLATE "pg_catalog"."default" NOT NULL,
  "sign_value" varchar(255) COLLATE "pg_catalog"."default",
  "code_status" int2 NOT NULL DEFAULT 1,
  "generated_at" timestamp(6) NOT NULL DEFAULT pg_systimestamp()
)
;

-- ----------------------------
-- Records of trace_code
-- ----------------------------
BEGIN;
INSERT INTO "trace_code" ("id", "trace_id", "batch_id", "qr_content", "sign_value", "code_status", "generated_at") VALUES (1, '8c3d51b8f2b741abb2e7c752444f1cc9', 3, '/api/trace/8c3d51b8f2b741abb2e7c752444f1cc9', 'd45a083c8ccb1deff55e9c1d0a1282a12694659676c647872a754360a1f61130', 1, '2026-04-16 10:57:31.705042'), (2, 'f7ef48fe3f414fd48251ac51a5e49a58', 2, '/api/trace/f7ef48fe3f414fd48251ac51a5e49a58', '7c010a329421c0f753f2d62176306c9854a9dfd911397bd6fc53cb2a1f76472a', 1, '2026-04-16 11:04:50.79318'), (3, 'cda49a8cb8c04d57839d6322293061b6', 4, '/api/trace/cda49a8cb8c04d57839d6322293061b6', '0a65d360b843103e15a8e97cf07a025b582a2b23ec3c674cba6e0752d5d5140a', 1, '2026-04-16 11:10:50.716432'), (4, 'c0132b5fdc4744eda58b3371d6b4405c', 5, '/api/trace/c0132b5fdc4744eda58b3371d6b4405c', 'cd6168d2aacc28e4cf76a4aa33e9302a279c4477eb055909752f2c4c03de1799', 1, '2026-04-20 20:24:51.168324'), (5, 'e9514ca7dc99400d8da90943f6b749ef', 6, '/api/trace/e9514ca7dc99400d8da90943f6b749ef', '14ebe466b0aef5bb883b37e089cd075fc0f82b9f95bf136611c3e205a734b6b3', 1, '2026-04-20 21:34:52.888895'), (6, 'cbf75df94118417abcfb0a43d0fca313', 7, '/api/trace/cbf75df94118417abcfb0a43d0fca313', '8cf8478408f63489750fc008a29c85f881bc698422804e51c2cd176b0002c743', 1, '2026-04-27 17:27:35.218799');
COMMIT;

-- ----------------------------
-- Table structure for user_feedback
-- ----------------------------
DROP TABLE IF EXISTS "user_feedback";
CREATE TABLE "user_feedback" (
  "id" int8 NOT NULL DEFAULT nextval('user_feedback_id_seq'::regclass),
  "user_id" int8,
  "type" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "content" text COLLATE "pg_catalog"."default" NOT NULL,
  "contact" varchar(128) COLLATE "pg_catalog"."default",
  "trace_id" varchar(64) COLLATE "pg_catalog"."default",
  "status" int2 NOT NULL DEFAULT 0,
  "created_at" timestamp(6) NOT NULL DEFAULT pg_systimestamp(),
  "ai_category" varchar(32) COLLATE "pg_catalog"."default",
  "ai_priority" int2,
  "ai_summary" varchar(255) COLLATE "pg_catalog"."default",
  "assignee_user_id" int8,
  "handle_note" varchar(255) COLLATE "pg_catalog"."default",
  "handled_at" timestamp(6),
  "updated_at" timestamp(6) DEFAULT pg_systimestamp(),
  "risk_level" varchar(16) COLLATE "pg_catalog"."default",
  "urgent_flag" int2 DEFAULT 0,
  "linked_task_id" int8,
  "linked_recall_id" int8,
  "batch_id" int8,
  "company_id" int8
)
;

-- ----------------------------
-- Records of user_feedback
-- ----------------------------
BEGIN;
INSERT INTO "user_feedback" ("id", "user_id", "type", "content", "contact", "trace_id", "status", "created_at", "ai_category", "ai_priority", "ai_summary", "assignee_user_id", "handle_note", "handled_at", "updated_at", "risk_level", "urgent_flag", "linked_task_id", "linked_recall_id", "batch_id", "company_id") VALUES (4, 2, '其他意见', '这批番茄有明显酸臭味，切开后里面发黑，怀疑已经变质了。', '', NULL, 2, '2026-04-27 16:23:15.613107', '质量', 1, '疑似质量安全问题，建议优先核查对应批次与质检记录。', 1, NULL, '2026-05-10 14:26:51.082689', '2026-05-10 14:26:51.082689', NULL, 0, NULL, NULL, NULL, 1), (6, 2, '功能建议', '订单显示昨天已发货，但到现在物流没有任何更新。', '', NULL, 2, '2026-04-27 16:23:43.434901', '物流', 2, '疑似物流履约问题，建议核查配送链路与签收节点。', 1, NULL, '2026-05-10 14:26:54.217761', '2026-05-10 14:26:54.217761', NULL, 0, NULL, NULL, NULL, 1), (13, 15, '其他意见', '我的草莓发霉了！！', '', '521435a02e1a45beb2962bd5c780d289', 2, '2026-05-08 20:23:52.608585', '质量', 1, '建议立即核查该批次草莓的仓储与质检记录，并优先联系用户处理退换货。', 1, NULL, '2026-05-08 20:24:37.770025', '2026-05-08 20:24:37.770025', 'HIGH', 1, 21, 6, 8, 1), (1, 11, '错误反馈', '的反驳对方公司给发士大夫', '', NULL, 0, '2026-04-16 16:46:12.071383', '其他', 3, '历史数据未自动分类', NULL, NULL, NULL, '2026-04-27 08:22:04.7386', NULL, 0, NULL, NULL, NULL, 1), (3, 13, '功能建议', '色弱体育挺烦人的士大夫', '', NULL, 0, '2026-04-20 21:39:32.246553', '其他', 3, '历史数据未自动分类', NULL, NULL, NULL, '2026-04-27 08:22:04.7386', NULL, 0, NULL, NULL, NULL, 1), (5, 2, '功能建议', '收到的生菜叶片发黄发烂，和页面展示差异很大，请尽快处理。', '', NULL, 0, '2026-04-27 16:23:28.86871', '其他', 3, '未命中明确问题类型，建议人工复核后分派。', 1, '系统自动分派', NULL, '2026-04-27 16:23:28.86871', NULL, 0, NULL, NULL, NULL, 1), (8, 2, '功能建议', '收到的生菜叶片发黄发烂，和页面展示差异很大，请尽快处理。', '', NULL, 0, '2026-04-27 16:24:32.401882', '其他', 3, '未命中明确问题类型，建议人工复核后分派。', 1, '系统自动分派', NULL, '2026-04-27 16:24:32.401882', NULL, 0, NULL, NULL, NULL, 1), (9, 2, '功能建议', '收到的生菜叶发黄发烂，吃不了。', '', NULL, 0, '2026-04-27 16:32:13.098031', '其他', 3, '未命中明确问题类型，建议人工复核后分派。', 1, '系统自动分派', NULL, '2026-04-27 16:32:13.098031', NULL, 0, NULL, NULL, NULL, 1), (10, 2, '功能建议', '收到的生菜叶发黄发烂，吃不了。', '', NULL, 1, '2026-04-27 16:33:15.945124', '其他', 3, '未命中明确问题类型，建议人工复核后分派。', 1, NULL, NULL, '2026-04-27 16:33:30.741339', NULL, 0, NULL, NULL, NULL, 1), (12, 2, '功能建议', '宣传部v洗发水自行车v', '', NULL, 2, '2026-04-27 17:31:29.310646', '其他', 3, '建议先确认反馈证据并联系处理人跟进闭环。', 1, NULL, '2026-05-10 14:26:58.659037', '2026-05-10 14:26:58.659037', NULL, 0, NULL, NULL, NULL, 1), (11, 2, '功能建议', '收到的生菜叶发黄发烂，吃不了。', '', NULL, 2, '2026-04-27 16:38:22.233809', '其他', 3, '建议先确认反馈证据并联系处理人跟进闭环。', 1, NULL, '2026-05-10 14:27:05.295589', '2026-05-10 14:27:05.295589', NULL, 0, NULL, NULL, NULL, 1), (2, 12, '其他意见', '豆腐干士大夫地方', '', NULL, 2, '2026-04-16 16:49:18.525909', '其他', 3, '历史数据未自动分类', NULL, NULL, '2026-05-10 14:27:12.600301', '2026-05-10 14:27:12.600301', NULL, 0, NULL, NULL, NULL, 1), (7, 2, '功能建议', '这批番茄有明显酸臭味，切开后里面发黑，怀疑已经变质了。', '', NULL, 2, '2026-04-27 16:24:20.974594', '质量', 1, '疑似质量安全问题，建议优先核查对应批次与质检记录。', 1, NULL, '2026-05-06 20:22:56.491012', '2026-05-06 20:22:56.491012', NULL, 0, NULL, NULL, NULL, 1), (14, 15, '产品质量', '草莓有很重的药味道，小孩吃了一直拉肚子。', '', '521435a02e1a45beb2962bd5c780d289', 2, '2026-05-10 14:20:05.954349', '质量', 1, '立即召回同批次产品，送检农残，并联系用户就医跟进。', 1, NULL, '2026-05-10 15:57:13.187619', '2026-05-10 15:57:13.187619', 'HIGH', 1, 24, 7, 8, 1), (15, 17, '产品质量', '我发现我的这个草莓有问题的，发霉了', '', '521435a02e1a45beb2962bd5c780d289', 2, '2026-05-12 19:39:06.048695', '质量', 1, '建议立即核查批次质检与仓储记录，并优先联系用户处理。', 2, '系统判定为高风险反馈，已自动生成待办任务', '2026-05-12 19:46:53.486642', '2026-05-12 19:46:53.486642', 'HIGH', 1, 26, 8, 8, 1), (16, 17, '产品质量', '圣女果坏掉了，不能吃', '', '685244cb64e44be5bacc1150ac1f5ee4', 0, '2026-05-12 20:42:49.228512', '质量', 1, '建议立即核查该批次圣女果的质检与仓储记录，并优先联系用户处理退款或补发。', NULL, '系统判定为高风险反馈，已自动生成待办任务', NULL, '2026-05-12 20:42:49.238962', 'HIGH', 1, 29, NULL, 10, 1);
COMMIT;

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "base_info_id_seq"
OWNED BY "base_info"."id";
SELECT setval('"base_info_id_seq"', 19, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "company_id_seq"
OWNED BY "company"."id";
SELECT setval('"company_id_seq"', 1, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "inspection_report_id_seq"
OWNED BY "inspection_report"."id";
SELECT setval('"inspection_report_id_seq"', 14, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "logistics_record_id_seq"
OWNED BY "logistics_record"."id";
SELECT setval('"logistics_record_id_seq"', 20, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "product_batch_id_seq"
OWNED BY "product_batch"."id";
SELECT setval('"product_batch_id_seq"', 13, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "product_item_id_seq"
OWNED BY "product_item"."id";
SELECT setval('"product_item_id_seq"', 188, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "production_record_id_seq"
OWNED BY "production_record"."id";
SELECT setval('"production_record_id_seq"', 26, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "recall_record_id_seq"
OWNED BY "recall_record"."id";
SELECT setval('"recall_record_id_seq"', 8, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "scan_log_id_seq"
OWNED BY "scan_log"."id";
SELECT setval('"scan_log_id_seq"', 55, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"seq_base_code"', 1, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"seq_batch_code"', 1, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"seq_logistics_code"', 20, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "sys_role_id_seq"
OWNED BY "sys_role"."id";
SELECT setval('"sys_role_id_seq"', 3, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "sys_user_id_seq"
OWNED BY "sys_user"."id";
SELECT setval('"sys_user_id_seq"', 17, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "system_task_id_seq"
OWNED BY "system_task"."id";
SELECT setval('"system_task_id_seq"', 31, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "trace_code_id_seq"
OWNED BY "trace_code"."id";
SELECT setval('"trace_code_id_seq"', 6, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "user_feedback_id_seq"
OWNED BY "user_feedback"."id";
SELECT setval('"user_feedback_id_seq"', 16, true);

-- ----------------------------
-- Indexes structure for table base_info
-- ----------------------------
CREATE INDEX "idx_base_info_company_id" ON "base_info" USING btree (
  "company_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Uniques structure for table base_info
-- ----------------------------
ALTER TABLE "base_info" ADD CONSTRAINT "base_info_base_code_key" UNIQUE ("base_code");

-- ----------------------------
-- Primary Key structure for table base_info
-- ----------------------------
ALTER TABLE "base_info" ADD CONSTRAINT "base_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Uniques structure for table company
-- ----------------------------
ALTER TABLE "company" ADD CONSTRAINT "company_company_code_key" UNIQUE ("company_code");
ALTER TABLE "company" ADD CONSTRAINT "company_company_name_key" UNIQUE ("company_name");

-- ----------------------------
-- Primary Key structure for table company
-- ----------------------------
ALTER TABLE "company" ADD CONSTRAINT "company_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table inspection_report
-- ----------------------------
CREATE INDEX "idx_inspection_report_batch_id" ON "inspection_report" USING btree (
  "batch_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Uniques structure for table inspection_report
-- ----------------------------
ALTER TABLE "inspection_report" ADD CONSTRAINT "inspection_report_report_no_key" UNIQUE ("report_no");

-- ----------------------------
-- Primary Key structure for table inspection_report
-- ----------------------------
ALTER TABLE "inspection_report" ADD CONSTRAINT "inspection_report_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table logistics_record
-- ----------------------------
CREATE INDEX "idx_logistics_record_batch_id" ON "logistics_record" USING btree (
  "batch_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_logistics_record_item_id" ON "logistics_record" USING btree (
  "item_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table logistics_record
-- ----------------------------
ALTER TABLE "logistics_record" ADD CONSTRAINT "logistics_record_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table product_batch
-- ----------------------------
CREATE INDEX "idx_product_batch_base_id" ON "product_batch" USING btree (
  "base_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_product_batch_company_id" ON "product_batch" USING btree (
  "company_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Uniques structure for table product_batch
-- ----------------------------
ALTER TABLE "product_batch" ADD CONSTRAINT "product_batch_batch_code_key" UNIQUE ("batch_code");

-- ----------------------------
-- Primary Key structure for table product_batch
-- ----------------------------
ALTER TABLE "product_batch" ADD CONSTRAINT "product_batch_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table product_item
-- ----------------------------
CREATE INDEX "idx_product_item_batch_id" ON "product_item" USING btree (
  "batch_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_product_item_trace_id" ON "product_item" USING btree (
  "trace_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Uniques structure for table product_item
-- ----------------------------
ALTER TABLE "product_item" ADD CONSTRAINT "product_item_item_code_key" UNIQUE ("item_code");
ALTER TABLE "product_item" ADD CONSTRAINT "product_item_trace_id_key" UNIQUE ("trace_id");

-- ----------------------------
-- Primary Key structure for table product_item
-- ----------------------------
ALTER TABLE "product_item" ADD CONSTRAINT "product_item_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table production_record
-- ----------------------------
CREATE INDEX "idx_production_record_batch_id" ON "production_record" USING btree (
  "batch_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_production_record_operation_time" ON "production_record" USING btree (
  "operation_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table production_record
-- ----------------------------
ALTER TABLE "production_record" ADD CONSTRAINT "production_record_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table recall_record
-- ----------------------------
CREATE INDEX "idx_recall_record_batch_id" ON "recall_record" USING btree (
  "batch_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table recall_record
-- ----------------------------
ALTER TABLE "recall_record" ADD CONSTRAINT "recall_record_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table scan_log
-- ----------------------------
CREATE INDEX "idx_scan_log_scanned_at" ON "scan_log" USING btree (
  "scanned_at" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);
CREATE INDEX "idx_scan_log_trace_id" ON "scan_log" USING btree (
  "trace_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table scan_log
-- ----------------------------
ALTER TABLE "scan_log" ADD CONSTRAINT "scan_log_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Uniques structure for table sys_role
-- ----------------------------
ALTER TABLE "sys_role" ADD CONSTRAINT "sys_role_role_code_key" UNIQUE ("role_code");

-- ----------------------------
-- Primary Key structure for table sys_role
-- ----------------------------
ALTER TABLE "sys_role" ADD CONSTRAINT "sys_role_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_user
-- ----------------------------
CREATE INDEX "idx_sys_user_company_id" ON "sys_user" USING btree (
  "company_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_user_openid" ON "sys_user" USING btree (
  "openid" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Uniques structure for table sys_user
-- ----------------------------
ALTER TABLE "sys_user" ADD CONSTRAINT "sys_user_username_key" UNIQUE ("username");
ALTER TABLE "sys_user" ADD CONSTRAINT "sys_user_openid_key" UNIQUE ("openid");

-- ----------------------------
-- Primary Key structure for table sys_user
-- ----------------------------
ALTER TABLE "sys_user" ADD CONSTRAINT "sys_user_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table sys_user_role
-- ----------------------------
ALTER TABLE "sys_user_role" ADD CONSTRAINT "sys_user_role_pkey" PRIMARY KEY ("user_id", "role_id");

-- ----------------------------
-- Indexes structure for table system_task
-- ----------------------------
CREATE INDEX "idx_system_task_assignee" ON "system_task" USING btree (
  "assignee_user_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_system_task_status_priority" ON "system_task" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST,
  "priority" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_system_task_unique" ON "system_task" USING btree (
  "task_type" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "biz_type" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "biz_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table system_task
-- ----------------------------
ALTER TABLE "system_task" ADD CONSTRAINT "system_task_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table trace_code
-- ----------------------------
CREATE INDEX "idx_trace_code_batch_id" ON "trace_code" USING btree (
  "batch_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Uniques structure for table trace_code
-- ----------------------------
ALTER TABLE "trace_code" ADD CONSTRAINT "trace_code_trace_id_key" UNIQUE ("trace_id");

-- ----------------------------
-- Primary Key structure for table trace_code
-- ----------------------------
ALTER TABLE "trace_code" ADD CONSTRAINT "trace_code_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table user_feedback
-- ----------------------------
CREATE INDEX "idx_user_feedback_assignee" ON "user_feedback" USING btree (
  "assignee_user_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_user_feedback_batch_id" ON "user_feedback" USING btree (
  "batch_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_user_feedback_company_id" ON "user_feedback" USING btree (
  "company_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_user_feedback_created_at" ON "user_feedback" USING btree (
  "created_at" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);
CREATE INDEX "idx_user_feedback_risk_level" ON "user_feedback" USING btree (
  "risk_level" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE INDEX "idx_user_feedback_status_priority" ON "user_feedback" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST,
  "ai_priority" "pg_catalog"."int2_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table user_feedback
-- ----------------------------
ALTER TABLE "user_feedback" ADD CONSTRAINT "user_feedback_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Foreign Keys structure for table base_info
-- ----------------------------
ALTER TABLE "base_info" ADD CONSTRAINT "fk_base_info_company" FOREIGN KEY ("company_id") REFERENCES "company" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table product_batch
-- ----------------------------
ALTER TABLE "product_batch" ADD CONSTRAINT "fk_product_batch_company" FOREIGN KEY ("company_id") REFERENCES "company" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table sys_user
-- ----------------------------
ALTER TABLE "sys_user" ADD CONSTRAINT "fk_sys_user_company" FOREIGN KEY ("company_id") REFERENCES "company" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table user_feedback
-- ----------------------------
ALTER TABLE "user_feedback" ADD CONSTRAINT "fk_user_feedback_company" FOREIGN KEY ("company_id") REFERENCES "company" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
