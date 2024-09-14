package com.geeke.hosdata.entity;

import com.geeke.stock.entity.Drug;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class HisDrugsStorage {
    private String pc;//生产批号
    private Date scrq;//生产日期
    private Date Yxq; //有效期
    private Integer Sl; //数量
    private String ckdw;//出库单位
    private BigDecimal Pfj; //最小单位批发价
    private BigDecimal Ykpfj; //进价
    private String Ypdm;//药品代码
    private String FPh;//发票号
    private String Ypmc;//药品名称
    private String zstbzt; //同步状态
    private String Ckzhyz;//出库转换因子 >1则为标准单位
    private String crkmxId;//库存信息唯一标识
    private String Cksj; //发药时间
}
