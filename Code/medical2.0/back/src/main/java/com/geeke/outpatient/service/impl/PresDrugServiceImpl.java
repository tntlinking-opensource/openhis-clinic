package com.geeke.outpatient.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geeke.outpatient.entity.PresDrug;
import com.geeke.outpatient.service.PresDrugService;
import com.geeke.outpatient.dao.PresDrugMapper;
import org.springframework.stereotype.Service;

/**
* @author
* @description 针对表【pres_drug(医疗目录表)】的数据库操作Service实现
* @createDate 2024-11-08
*/
@Service
public class PresDrugServiceImpl extends ServiceImpl<PresDrugMapper, PresDrug>
implements PresDrugService{

}
