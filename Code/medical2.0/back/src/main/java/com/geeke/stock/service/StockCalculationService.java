package com.geeke.stock.service;

import com.geeke.common.persistence.DataEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 库存计算工具服务
 * 提供通用的库存计算逻辑，消除DrugService和StuffService中的重复代码
 */
@Service
public class StockCalculationService {

    /**
     * 计算并设置实体的库存数量
     * 库存 = 入库总量 - 已使用库存 - 报损库存
     *
     * @param entity 药品或材料实体
     */
    public void calculateAndSetInventory(StockEntity entity) {
        if (entity == null || entity.getStock() == null || entity.getStock().getStorageStock() == null) {
            if (entity != null) {
                entity.setInventory(0);
            }
            return;
        }
        StockInfo stock = entity.getStock();
        BigDecimal usedStock = stock.getUsedStock() == null ? BigDecimal.ZERO : stock.getUsedStock();
        BigDecimal reimburseStock = stock.getReimburseStock() == null ? BigDecimal.ZERO : stock.getReimburseStock();
        BigDecimal inventory = stock.getStorageStock().subtract(usedStock.add(reimburseStock));
        entity.setInventory(inventory.setScale(0, RoundingMode.HALF_UP).intValue());
    }

    /**
     * 库存实体接口
     * Drug和Stuff都需要实现此接口
     */
    public interface StockEntity {
        StockInfo getStock();
        void setInventory(int inventory);
    }

    /**
     * 库存信息接口
     */
    public interface StockInfo {
        BigDecimal getStorageStock();
        BigDecimal getUsedStock();
        BigDecimal getReimburseStock();
    }
}
