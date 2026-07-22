-- ============================================================
-- P0 性能优化：为高频查询列添加二级索引
-- 执行前请在测试环境验证，确认无锁表风险后在低峰期执行
-- 执行方式：mysql -u root -p dbname < add_indexes_p0.sql
-- ============================================================

-- 药品表：租户 + 编码 + 名称 高频查询
ALTER TABLE drug ADD INDEX idx_drug_company (company_id);
ALTER TABLE drug ADD INDEX idx_drug_code (company_id, code);
ALTER TABLE drug ADD INDEX idx_drug_goods_name (company_id, goods_name);

-- 患者表：手机号/卡号/租户
ALTER TABLE patient ADD INDEX idx_patient_company (company_id);
ALTER TABLE patient ADD INDEX idx_patient_phone (company_id, phone);
ALTER TABLE patient ADD INDEX idx_patient_card (company_id, card);

-- 挂号表
ALTER TABLE registration ADD INDEX idx_reg_company (company_id);
ALTER TABLE registration ADD INDEX idx_reg_patient (patient_id);
ALTER TABLE registration ADD INDEX idx_reg_status (company_id, status);

-- 处方信息表
ALTER TABLE recipel_info ADD INDEX idx_ri_registration (registration_id);
ALTER TABLE recipel_info ADD INDEX idx_ri_company (company_id);

-- 库存总控表
ALTER TABLE medicinal_stock_control ADD INDEX idx_msc_drug_stuff (drug_stuff_id);
ALTER TABLE medicinal_stock_control ADD INDEX idx_msc_company (company_id);

-- 库存明细表
ALTER TABLE medicinal_stock_record ADD INDEX idx_msr_drug_stuff (drug_stuff_id);

-- 供应商库存表
ALTER TABLE supplier_stock ADD INDEX idx_ss_company (company_id);
ALTER TABLE supplier_stock ADD INDEX idx_ss_drug (drug_id);

-- 任务管理表
ALTER TABLE taskmanagement ADD INDEX idx_tm_company (company_id);
ALTER TABLE taskmanagement ADD INDEX idx_tm_status (company_id, taskstatus);
ALTER TABLE feedbacktable ADD INDEX idx_fb_task (taskmanagement_id);

-- 病历模板表
ALTER TABLE casehistorymbgl ADD INDEX idx_chmb_company (company_id);

-- 文章表
ALTER TABLE article ADD INDEX idx_article_company (company_id);
