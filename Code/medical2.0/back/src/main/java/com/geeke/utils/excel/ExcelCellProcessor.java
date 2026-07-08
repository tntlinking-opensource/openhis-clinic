package com.geeke.utils.excel;
import com.geeke.common.constants.BizConstants;

import java.util.HashMap;
import java.util.Map;

public class ExcelCellProcessor {
    private Map<String, ValueConverter> converterMap = new HashMap<>();

    public ExcelCellProcessor() {
        // 注册值转换器
        /** 性别 */
        converterMap.put("OutpatientLog-gender", value -> {
            String strValue = (String) value;
            if (BizConstants.GENDER_MALE.equals(strValue)) {
                return "男";
            } else if (BizConstants.GENDER_FEMALE.equals(strValue)) {
                return "女";
            }
            return "未知";
        });
        /** 初/复诊 */
        converterMap.put("OutpatientLog-initialVisit", value -> {
            String strValue = (String) value;
            if (BizConstants.REG_SOURCE_NORMAL.equals(strValue)) {
                return "初诊";
            } else {
                return "复诊";
            }
        });
        /** 传染病 */
        converterMap.put("OutpatientLog-infect", value -> {
            String strValue = (String) value;
            if (BizConstants.INFECT_TYPE_NONE.equals(strValue)) {
                return "是";
            } else {
                return "否";
            }
        });
    }

    public String convert(String propertyName, Object propertyValue) {
        // 获取值转换器
        ValueConverter converter = converterMap.get(propertyName);
        if (converter != null) {
            return converter.convert(propertyValue);
        }
        // 默认情况下返回属性值的字符串形式
        return String.valueOf(propertyValue);
    }

    // 值转换器接口
    interface ValueConverter {
        String convert(Object obj);
    }
}
