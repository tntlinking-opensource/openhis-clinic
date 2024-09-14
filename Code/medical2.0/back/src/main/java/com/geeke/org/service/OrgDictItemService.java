//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.geeke.org.service;

import com.geeke.common.service.CrudService;
import com.geeke.org.dao.OrgDictItemDao;
import com.geeke.org.entity.OrgDictItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("orgDictItemService")
@Transactional(
        readOnly = true
)
public class OrgDictItemService extends CrudService<OrgDictItemDao, OrgDictItem> {
    public OrgDictItemService() {
    }
}
