package com.geeke.utils;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SerialNoUtils {
    /** 本地计数器，key=前缀+日期, value=当前序号 */
    private final Map<String, AtomicInteger> counterMap = new ConcurrentHashMap<>();

    /**
     * 生成17位唯一流水号，"LSH"+yyyyMMdd+6位数字
     */
    public String generateSerialNo(String codeKey){
        String todayDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String cacheKey = codeKey + todayDate;

        AtomicInteger counter = counterMap.computeIfAbsent(cacheKey, k -> new AtomicInteger(0));
        int codeNum = counter.incrementAndGet();

        return getCodeOfSix(cacheKey, codeNum);
    }

    private String getCodeOfSix(String prefix, int nowNum) {
        StringBuilder numSb = new StringBuilder();
        if (nowNum < 10) {
            numSb.append("00000").append(nowNum);
        } else if (nowNum < 100) {
            numSb.append("0000").append(nowNum);
        } else if (nowNum < 1000) {
            numSb.append("000").append(nowNum);
        } else if (nowNum < 10000) {
            numSb.append("00").append(nowNum);
        } else if (nowNum < 100000) {
            numSb.append("0").append(nowNum);
        } else {
            numSb.append(nowNum);
        }
        return prefix + numSb.toString();
    }
}
