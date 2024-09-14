package com.geeke.taskmanagement.dao;


import com.geeke.common.data.PageRequest;
import org.apache.ibatis.annotations.Mapper;
import com.geeke.common.persistence.CrudDao;
import com.geeke.taskmanagement.entity.article;

import java.util.List;

/**
 * 文章管理数据DAO接口
 * @author txl
 * @version 2023-02-09
 */
@Mapper
public interface ArticleDao extends CrudDao<article>{

    article listgetid(article rcvo);

    int update(article article);

    int counts(PageRequest var1);

    List<article> listPages(PageRequest var1);
}
