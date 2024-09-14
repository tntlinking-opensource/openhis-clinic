package com.geeke.taskmanagement.service;

import com.geeke.common.data.Page;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.service.CrudService;
import com.geeke.outpatient.dao.SchedulingDao;
import com.geeke.outpatient.entity.scheduling;
import com.geeke.taskmanagement.dao.ArticleDao;
import com.geeke.taskmanagement.entity.article;
import com.geeke.taskmanagement.entity.taskmanagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;

import java.util.*;

@Service("ArticleService")
@Transactional(readOnly = false)
public class ArticleService extends CrudService<ArticleDao, article>{

    @Autowired
    private ArticleDao articleDao;

    public article listgetid(article article){
        article articelgetid=articleDao.listgetid(article);
        return articelgetid;
    }
    public int update(article article){
        int updatecount=articleDao.update(article);
        return updatecount;
    }

    public Page<article> listPages(List<Parameter> params, int offset, int limit, String orderby) {
        PageRequest pageRequest = new PageRequest(offset, limit, params, orderby);
        int total = this.dao.counts(pageRequest);
        List<article> list = null;
        if (total > 0) {
            list = this.dao.listPages(pageRequest);
        }

        return new Page((long)total, list);
    }
}
