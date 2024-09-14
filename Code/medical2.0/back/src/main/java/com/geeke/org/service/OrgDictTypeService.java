//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.geeke.org.service;

import java.util.Iterator;
import java.util.List;

import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.service.CrudService;
import com.geeke.org.dao.OrgDictItemDao;
import com.geeke.org.dao.OrgDictTypeDao;
import com.geeke.org.entity.OrgDictItem;
import com.geeke.org.entity.OrgDictType;
import com.geeke.utils.StringUtils;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("orgDictTypeService")
@Transactional(
        readOnly = true
)
public class OrgDictTypeService extends CrudService<OrgDictTypeDao, OrgDictType> {
    @Autowired
    private OrgDictItemDao dictItemDao;

    public OrgDictTypeService() {
    }

    public OrgDictType get(String id) {
        OrgDictType dictType = (OrgDictType)super.get(id);
        List<Parameter> params = null;
        params = Lists.newArrayList();
        params.add(new Parameter("dict_type_id", "=", dictType.getId()));
        params.add(new Parameter("company_id", "=", dictType.getCompany().getId()));
        PageRequest pageRequest = new PageRequest(params);
        List<OrgDictItem> orgDictItems = this.dictItemDao.listAll(pageRequest);
        dictType.setDictItemList(orgDictItems);
        return dictType;
    }

    @Transactional(
            readOnly = false
    )
    public OrgDictType save(OrgDictType dictType) {
        OrgDictType dictTypeTemp = (OrgDictType)super.save(dictType);
        if (StringUtils.isNoneBlank(new CharSequence[]{dictTypeTemp.getId()})) {
            this.saveDictItemList(dictTypeTemp);
        }

        return dictTypeTemp;
    }

    @Transactional(
            readOnly = false
    )
    public OrgDictType importJson(OrgDictType dictType) {
        if (this.get(dictType.getId()) == null) {
            dictType.preInsert();
            this.doInsert(dictType);
            this.saveAction(this.createAction("created", dictType));
        }

        OrgDictType dictTypeTemp = this.save(dictType);
        return dictTypeTemp;
    }

    @Transactional(
            readOnly = false
    )
    public int delete(OrgDictType dictType) {
        List<Parameter> params = null;
        params = Lists.newArrayList();
        params.add(new Parameter("dict_type_id", "=", dictType.getId()));
        PageRequest pageRequest = new PageRequest(params);
        dictType.setDictItemList(this.dictItemDao.listAll(pageRequest));
        if (dictType.getDictItemList() != null && dictType.getDictItemList().size() > 0) {
            this.dictItemDao.bulkDelete(dictType.getDictItemList());
        }

        int rows = super.delete(dictType);
        return rows;
    }

//    protected Action createAction(String actionTypeId, OrgDictType entity) {
//        Action action = super.createAction(actionTypeId, entity);
//        if (action == null) {
//            return null;
//        } else {
//            if ("deleted".equals(actionTypeId)) {
//                Iterator var4 = entity.getDictItemList().iterator();
//
//                while(var4.hasNext()) {
//                    OrgDictItem child = (OrgDictItem)var4.next();
//                    ActionRecycle recycle = new ActionRecycle();
//                    recycle.setTableName(child.getBusTableName());
//                    recycle.setObjectId(child.getId());
//                    recycle.setObjectName((String)Reflections.invokeGetter(child, "name"));
//                    action.getActionRecycleList().add(recycle);
//                }
//            }
//
//            return action;
//        }
//    }

    private void saveDictItemList(OrgDictType dictType) {
        List<Parameter> params = Lists.newArrayList();
        params.add(new Parameter("dict_type_id", "=", dictType.getId()));
        PageRequest pageRequest = new PageRequest(params);
        List<OrgDictItem> list_DictItem = this.dictItemDao.listAll(pageRequest);
        List<OrgDictItem> deletes = Lists.newArrayList();
        List<OrgDictItem> inserts = Lists.newArrayList();
        List<OrgDictItem> updates = Lists.newArrayList();
        Iterator var8 = list_DictItem.iterator();

        OrgDictItem dictItem;
        while(var8.hasNext()) {
            dictItem = (OrgDictItem)var8.next();
            boolean found = false;
            Iterator var11 = dictType.getDictItemList().iterator();

            while(var11.hasNext()) {
                OrgDictItem dictItem1 = (OrgDictItem)var11.next();
                if (dictItem1.getId().equals(dictItem.getId())) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                deletes.add(dictItem);
            }
        }

        if (deletes.size() > 0) {
            this.dictItemDao.bulkDelete(deletes);
        }

        var8 = dictType.getDictItemList().iterator();

        while(var8.hasNext()) {
            dictItem = (OrgDictItem)var8.next();
            if (StringUtils.isBlank(dictItem.getId())) {
                dictItem.setDictType(dictType);
                dictItem.preInsert();
                inserts.add(dictItem);
            } else {
                dictItem.preUpdate();
                updates.add(dictItem);
            }
        }

        if (updates.size() > 0) {
            this.dictItemDao.bulkUpdate(updates);
        }

        if (inserts.size() > 0) {
            this.dictItemDao.bulkInsert(inserts);
        }

    }
}
