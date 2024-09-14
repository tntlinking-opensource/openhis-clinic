//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.geeke.org.dao;

import com.geeke.common.persistence.CrudDao;
import com.geeke.org.entity.OrgDictItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrgDictItemDao extends CrudDao<OrgDictItem> {
    OrgDictItem getTrade(String var1);
}
