package com.geeke.stock.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 动态库存台账记录及控制处理
 * @author helong
 * @date 2022/8/17 14:01
 */
@Service("dynamicStockRecordService")
@Transactional(rollbackFor=Exception.class)
public class DynamicStockRecordService
{
    /**
     * 预占用
     * 目前包含功能：病例填写-完成接诊、病例填写-快速接诊、病例填写-完成接诊后编辑保存
     */
    public void preparedOccupy() {
        //先释放再重新占用

    }

    /**
     * 实际占用
     * 目前包含功能：发药完成
     */
    public void actualOccupy() {
        //先释放再重新占用

        //再实际占用
    }

    /**
     * 退药库存操作
     * 药房实际库存增加，可用库存暂时先不增加，等退费后可用库存再增加
     */
    public void backMedicine() {
        //直接把实际占用的库存还回去
    }

    /**
     * 退费
     */
    public void backCost() {
        //取消预占用

        //如果是退药则可用库存增加
    }
}
