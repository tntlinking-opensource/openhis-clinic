package com.geeke.common.service;

import java.util.Collections;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.persistence.TreeDao;
import com.geeke.common.persistence.TreeEntity;
import com.geeke.utils.Reflections;
import com.geeke.utils.StringUtils;
import com.google.common.collect.Lists;



/**
 * Service基类
 * @author lys
 * @version 2014-05-16
 */
@Transactional(readOnly = true)
public abstract class TreeService<D extends TreeDao<T>, T extends TreeEntity<T>> extends CrudService<D, T> {
	
	
	@Override
	public T get(String id) {
		T entity = super.get(id);
		if(entity != null && entity.getParent() != null && StringUtils.isBlank(entity.getParent().getId())) {
			entity.setParent(null);
		}
		return entity;
	}
	
	@Override
	@Transactional(readOnly = false)
	public T save(T entity) {
		
		@SuppressWarnings("unchecked")
		Class<T> entityClass = Reflections.getClassGenricType(getClass(), 1);
		
		// 如果没有设置父节点，则代表为跟节点，有则获取父节点实体
		if (entity.getParent() == null || StringUtils.isBlank(entity.getParent().getId())){
			T parentEntity = null;
			try {
				parentEntity = entityClass.newInstance();
			} catch (Exception e) {
				throw new ServiceException(e);
			}
			parentEntity.setIds("0.");
			parentEntity.setId("0");
			entity.setParent(parentEntity);
		} else if(entity.getParent().getId().equals("0")) {	// 根节点对象
			entity.getParent().setIds("0.");
		} else {	// 根节点对象
			entity.setParent(super.get(entity.getParent().getId()));
		}

		
		// 获取修改前的parentIds，用于更新子节点的parentIds，如果是新增模式下，忽略它的ids
		String oldIds = null;
		if (StringUtils.isNotBlank(entity.getId())) {
			oldIds = entity.getIds();
		}
		
	
		// 保存或更新实体
		String id = super.save(entity).getId();
		
		// 修改
		if(StringUtils.isNotBlank(oldIds)) {
	    	List<Parameter> parms = Lists.newArrayList();
	    	parms.add(new Parameter("ids", "right_like", oldIds));
	    	parms.add(new Parameter("id", "<>", entity.getId()));
	    	List<T> children = dao.listAll(new PageRequest(parms));
	    	for(T child : children) {
	    		child.setIds(child.getIds().replace(oldIds, entity.getIds()));
	    		dao.update(child);
	    	}
		}
		
		return entity;
	}
	
	/**
	 * 查询数表数据
	 * @param entity
	 * @return
	 */
	public List<T> tree(List<Parameter> parameters, String orderby) {
		PageRequest pageRequest = new PageRequest(parameters, orderby);
		List<T> list = dao.listAll(pageRequest);
		return bulid(list);
	}
	
    /**
     * 两层循环实现建树
     * @param list 传入的树节点列表
     * 把list数据通过 id，和parentId够造成一个树
     * @return
     */
    private List<T> bulid(List<T> list) {
        List<Integer> childIndex = Lists.newArrayList();
        for (T entity : list) {
        	for(int i = 0; i <= list.size() - 1; i++) {
        		T it = list.get(i);
                if (it.getParent() != null && entity.getId().equals(it.getParent().getId())) {
                    if (entity.getChildren() == null) {
                    	entity.setChildren(Lists.newArrayList());
                    }                	
                	entity.getChildren().add(it);
                	childIndex.add(i);
                }
            }
        }
        Collections.sort(childIndex); 
        for(int i = childIndex.size() - 1; i >= 0; i--) {
        	int idx = (int)childIndex.get(i);
        	list.remove(idx);
        }
        return list;
    }

}
