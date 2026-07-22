package com.geeke.org.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.geeke.common.controller.CrudController;
import com.geeke.org.entity.OrgDictItem;
import com.geeke.org.service.OrgDictItemService;

/**
 * 字典项Controller
 */
@RestController
@RequestMapping({"/org/dictItem"})
public class OrgDictItemController extends CrudController<OrgDictItemService, OrgDictItem> {

    @Autowired
    protected OrgDictItemService dictItemService;

    @Override
    protected OrgDictItemService getService() {
        return dictItemService;
    }
}
