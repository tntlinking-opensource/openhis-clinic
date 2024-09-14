package com.geeke.treatment.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import com.geeke.common.data.Page;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.sequence.service.SequenceService;
import com.geeke.common.service.CrudService;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.org.entity.Company;
import com.geeke.org.service.CompanyService;
import com.geeke.outpatient.entity.RecipelDetail;
import com.geeke.outpatient.entity.RecipelInfo;
import com.geeke.outpatient.service.RecipelDetailService;
import com.geeke.stock.entity.MedicalProject;
import com.geeke.sys.utils.SessionUserDto;
import com.geeke.treatment.dao.CostItemDao;
import com.geeke.treatment.entity.CostItem;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.SessionUtils;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 诊疗项目Service
 * @author txl
 * @version 2022-05-30
 */
 
@Service("costItemService")
public class CostItemService extends CrudService<CostItemDao, CostItem>{
    @Autowired
    SequenceService sequenceService;
    @Autowired
    RecipelDetailService recipelDetailService;

    @Autowired
    private CompanyService companyService;

    @Override
    @Transactional(readOnly = false)
    public CostItem save(CostItem costItem) {
        // 新增时, 处理自动编号字段
        if (StringUtils.isBlank(costItem.getId())){
            String sn = sequenceService.generate(SessionUtils.getLoginTenantId(), "treatment_item_code", costItem);
            costItem.setItemCode(sn);
        }
        String id = this.dao.GetIDByName(costItem.getItemName(),SessionUtils.getLoginTenantId());
        if(null!=id && !id.equals(costItem.getId()))
        {
            throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "已经存在一个相同的项目名，请重新输入！"));
        }
        CostItem costItemTemp = super.save(costItem);
        return costItemTemp;
    }

    @Transactional
    public List<MedicalProject> listPageForMedical(List<Parameter> parameters, String orderby) {
        PageRequest pageRequest = new PageRequest(parameters, orderby);
        int total = this.dao.count(pageRequest);
        List<CostItem> lis = null;
        List<MedicalProject> list = new ArrayList<>();
        if (total > 0) {
            lis = this.dao.listAll(pageRequest);
            lis.stream().forEach(costItem -> {
                costItem.setName(costItem.getItemName());
                MedicalProject medicalProject = new MedicalProject();
                BeanUtils.copyProperties(costItem,medicalProject);
                medicalProject.setIsUnpackSell("0");
                medicalProject.setStuffType("3");
                medicalProject.setDosisUnit(costItem.getUnit());
                medicalProject.setPack(costItem.getUnit());
                list.add(medicalProject);});
        }
        return list;
    }
    @Transactional
    public List<CostItem> getByRecipelInfo(RecipelInfo recipelInfo) {
        List<RecipelDetail> byRecipelInfoId = recipelDetailService.getByRecipelInfoId(recipelInfo.getId());
        List<CostItem> costItems=new ArrayList<>();
        for (RecipelDetail recipelDetail : byRecipelInfoId) {
            CostItem costItem=this.dao.getByRecipelInfo(recipelDetail.getId());

            if(!ObjectUtils.isEmpty(costItem)){
                costItem.setRecipelDetail(recipelDetail);
                costItems.add(costItem);
            }
        }

        return costItems;
    }

    public Page<CostItem> listByInstitution(List<Parameter> params, int offset, int limit, String orderby) {
        Optional<Parameter> cartOptional = params.stream().filter(item -> item.getColumnName().equals("`company_id`")).findFirst();
        params.remove(0);
        String id = (String) cartOptional.get().getValue();
        String institution = companyService.getInstitution(id);

        PageRequest pageRequest = new PageRequest(offset, limit, params, orderby, id, institution);
        int total = dao.countByInstitution(pageRequest);
        List<CostItem> list = null;
        if (total > 0) {
            list = dao.listByInstitution(pageRequest);
        }

        return new Page<>(total, list);
    }

    @Transactional
    public List<String> syncToClinic(List<CostItem> entitys) {
        SessionUserDto userDto = SessionUtils.getUserDto();
        SessionUserDto.CompanyDTO companyDto = userDto.getCompany();
        Company company = BeanUtil.copyProperties(companyDto, Company.class);

        List<String> list = new ArrayList<>();

        entitys.forEach(costItem -> {
            String oldId = costItem.getId();
            String id = this.dao.GetIDByName(costItem.getItemName(), SessionUtils.getLoginTenantId());
            if (ObjUtil.isNotNull(id)) {
                costItem.setCompany(company);
                costItem.setId(null);
                costItem.setSyncId(oldId);
                String sn = sequenceService.generate(SessionUtils.getLoginTenantId(), "treatment_item_code", costItem);
                costItem.setItemCode(sn);
                super.save(costItem);
                list.add(costItem.getId());
            }
        });
        return list;
    }
}