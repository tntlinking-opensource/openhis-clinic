package com.geeke.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SerialNoUtils {
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 生成17位唯一流水号，"LSH"+yyyyMMdd+6位数字
     * 6位数字，如：000001
     * @return
     */
    public String generateSerialNo(String codeKey){
        //定义需要返回的流水号
        String serialNo = null;
        //先查询到今天的日期，格式:"yyyyMMdd"
        String todayDate = new SimpleDateFormat("yyyyMMdd")
                .format(new Date());
        //固定字母前缀 拼接 今天日期，组成新的完整的前缀，也就是缓存的key
        String cacheKey = codeKey + todayDate;
        //再通过key查询缓存有没有num数据，缓存操作根据自身项目封装工具类
        Object value = redisTemplate.opsForValue().get(cacheKey);
        Long codeNum;
        //如果缓存查询有值，数值+1，再赋值给下一个流水号
        if (null != value) {

            codeNum = Long.valueOf(String.valueOf(value)) + 1L;
        } else {
            //如果缓存查询没值，直接赋值为1
            codeNum = 1L;
        }
        //流水号 = 缓存key + 拼接的数值 = 前缀 + 日期 + 拼接的数值
        serialNo = getCodeOfSix(cacheKey, codeNum.intValue());
        //设置缓存，调用此方法，会自动将key所对应的value+1，保存时长：今天剩余的时间
        int seconds = getSeconds();
        redisTemplate.opsForValue().set(cacheKey,String.valueOf(codeNum),seconds, TimeUnit.SECONDS);
        return serialNo;
    }


    /**
     * 将数值拼接成对应的位数
     * @param prefix  前缀："LSH"+yyyyMMdd
     * @param nowNum  当前要生成的数字
     * @return 拼接好的流水号
     */
    private String getCodeOfSix(String prefix,int nowNum ) {
        //需要返回的code
        StringBuilder codeSb = new StringBuilder();
        //需要拼接的数字
        StringBuilder numSb = new StringBuilder();
        //封装的数字对象，里面 value 加了 volatile关键字，保证了线程安全
        AtomicInteger count = new AtomicInteger(nowNum);

        //将数值补足为6位字符串
        if (count.get() < 10) {
            numSb.append("00000").append(count.get());
        } else if(count.get() < 100){
            numSb.append("0000").append(count.get());
        }else if(count.get() < 1000){
            numSb.append("000").append(count.get());
        }else if(count.get() < 10000){
            numSb.append("00").append(count.get());
        }else if(count.get() < 100000){
            numSb.append("0").append(count.get());
        } else if (count.get() >= 100000) {
            numSb.append(count.get());
        }

        //先拼接前缀
        codeSb.append(prefix);
        //再拼接数字
        codeSb.append(numSb);
        return codeSb.toString();
    }


    /**
     * 获取当天结束还剩余多少秒
     * @return
     */
    private int getSeconds(){
        //获取今天当前时间
        Calendar curDate = Calendar.getInstance();
        //获取明天凌晨0点的日期
        Calendar tomorrowDate = new GregorianCalendar(
                curDate.get(Calendar.YEAR),
                curDate.get(Calendar.MONTH),
                curDate.get(Calendar.DATE) + 1,
                0, 0, 0);
        //返回 明天凌晨0点 和 今天当前时间 的差值（秒数）
        return (int)(tomorrowDate.getTimeInMillis() - curDate .getTimeInMillis()) / 1000;
    }
}
