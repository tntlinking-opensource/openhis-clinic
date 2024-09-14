package com.geeke.uid;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baidu.fsg.uid.UidGenerator;
import com.baidu.fsg.uid.impl.CachedUidGenerator;
import com.baidu.fsg.uid.impl.DefaultUidGenerator;
import com.baidu.fsg.uid.worker.WorkerIdAssigner;
import com.geeke.utils.IdGen;

/**
 * @author: lys
 * @description: uidgenerator 配置
 * @date: 2020/07/10 10:10
 */
@Configuration
public class UidConfiguration {
    // private static final Logger logger = LoggerFactory.getLogger(UidConfiguration.class);

   
    @Bean
    public UidGenerator uidGenerator(WorkerIdAssigner workerIdAssigner) {
    	DefaultUidGenerator generator = new CachedUidGenerator();
    	generator.setTimeBits(29);  // 28位最多可支持约8.7年
    	generator.setWorkerBits(21); // 22位最多可支持420万次重启
    	generator.setSeqBits(13);   // 13位可支持每秒8192个并发
    	generator.setEpochStr("2020-07-23");
    	   	
    	generator.setWorkerIdAssigner(workerIdAssigner);
    	
    	// 设置id产生器
        IdGen.setUidGenerator(generator);
    	return generator;
    }
}
