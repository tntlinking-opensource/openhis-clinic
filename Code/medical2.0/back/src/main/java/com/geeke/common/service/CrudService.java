package com.geeke.common.service;

import com.geeke.common.IActionSaver;
import com.geeke.common.constants.ActionConstants;
import com.geeke.common.data.Page;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.persistence.CrudDao;
import com.geeke.common.persistence.DataEntity;
import com.geeke.gen.entity.GenTable;
import com.geeke.org.service.CompanyService;
import com.geeke.sys.entity.Action;
import com.geeke.sys.entity.ActionRecycle;
import com.geeke.sys.entity.DictItem;
import com.geeke.utils.Reflections;
import com.geeke.utils.SessionUtils;
import com.geeke.utils.StringUtils;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


/**
 * Service基类
 * @author lys
 * @version 2014-05-16
 */
@Transactional
public abstract class CrudService<D extends CrudDao<T>, T extends DataEntity<T>> extends BaseService {
	
	/**
	 * 持久层对象
	 */
	@Autowired
	protected D dao;
	
	@Autowired
	IActionSaver actionSaver;
	@Autowired
	private CompanyService companyService;

	/**
	 * 获取单条数据
	 * @param id
	 * @return
	 */
	public T get(String id) {
		T entity = dao.get(id);
		if (entity != null) {
			verifyTenantOwnership(entity);
		}
		return entity;
	}

	/**
	 * 查询分页数据
	 * @param page 分页对象
	 * @param entity
	 * @return
	 */
	public Page<T> listPage(List<Parameter> parameters, int offset, int limit, String orderby) {
		ensureCompanyFilter(parameters);
		PageRequest pageRequest = new PageRequest(offset, limit, parameters, orderby);
		int total = dao.count(pageRequest);
		List<T> list = total > 0 ? dao.listPage(pageRequest) : java.util.Collections.emptyList();
        return new Page<T>(total, list);
	}


	/**
	 * 查询列表数据
	 * @param entity
	 * @return
	 */
	public List<T> listAll(List<Parameter> parameters, String orderby) {
		ensureCompanyFilter(parameters);
		PageRequest pageRequest = new PageRequest(parameters, orderby);
		return dao.listAll(pageRequest);
	}


	/**
	 * 保存数据（插入或更新）
	 * @param entity
	 */
	public T save(T entity) {
		// 更新操作需要校验租户归属
		if (StringUtils.isNotBlank(entity.getId())) {
			verifyTenantOwnership(entity);
		}
		if (StringUtils.isBlank(entity.getId())){
			entity.preInsert();
			doInsert(entity);
			this.saveAction(this.createAction(ActionConstants.ACTION_CREATED, entity));
		}else{
			entity.preUpdate();
			doUpdate(entity);
			this.saveAction(this.createAction(ActionConstants.ACTION_UPDATED, entity));
		}

		return entity;
	}

	/**
	 * 实体表有删除字段时，采用逻辑删除；否则采用物理删除数据
	 * @param entity
	 */
	public int delete(T entity) {
		verifyTenantOwnership(entity);
		entity.preUpdate();
		int rows = doDelete(entity);
		if(rows > 0) {
			this.saveAction(this.createAction(ActionConstants.ACTION_DELETED, entity));
		}
		return rows;
	}

	/**
	 * 批量添加
	 * @param entitys
	 * @return
	 */
	public List<String> bulkInsert(List<T> entitys) {
		List<String> ids = Lists.newArrayList();
		for(T entity: entitys) {
			entity.preInsert();
			ids.add(entity.getId());
		}
		dao.bulkInsert(entitys);
		return ids;
	}
	
	/**
	 * 批量修改
	 * @param entitys
	 * @return
	 */
	public List<String> bulkUpdate(List<T> entitys) {
		List<String> ids = Lists.newArrayList();
		for(T entity: entitys) {
			verifyTenantOwnership(entity);
			entity.preUpdate();
			ids.add(entity.getId());
		}
		dao.bulkUpdate(entitys);
		return ids;
	}

	/**
	 * 批量删除
	 * @param entitys
	 * @return
	 */
	public int bulkDelete(List<T> entitys) {
		for(T entity: entitys) {
			verifyTenantOwnership(entity);
			entity.preUpdate();
		}
		return dao.bulkDelete(entitys);
	}

	/**
	 * 租户过滤兜底：如果参数中没有 company_id，则自动注入当前登录租户的 company_id
	 * 仅对实体类自身声明了 company 字段的业务实体生效（排除仅从 DataEntity 继承的系统实体）
	 * @param parameters 查询参数列表
	 */
	protected void ensureCompanyFilter(List<Parameter> parameters) {
		if (parameters == null) {
			return;
		}
		// 检查实体类自身是否有 company 字段（不含父类），没有则跳过
		try {
			Class<?> entityClass = Reflections.getClassGenricType(getClass(), 1);
			if (entityClass == null || entityClass == Object.class || !Reflections.hasOwnField(entityClass, "company")) {
				return;
			}
		} catch (Exception e) {
			logger.debug("ensureCompanyFilter check failed: {}", e.getMessage());
			return;
		}
		// 检查是否已经有 company_id 过滤
		boolean hasCompanyFilter = false;
		for (Parameter param : parameters) {
			if (param != null && "company_id".equals(param.getColumnName())) {
				hasCompanyFilter = true;
				break;
			}
		}
		// 如果没有 company_id 过滤，自动注入当前租户ID
		if (!hasCompanyFilter) {
			String tenantId = SessionUtils.getLoginTenantId();
			if (StringUtils.isNotBlank(tenantId) && !"null".equals(tenantId)) {
				parameters.add(new Parameter("company_id", "=", tenantId));
			}
		}
	}

	/**
	 * 校验实体是否属于当前租户
	 * 用于 get/save/delete 等操作的安全校验
	 * @param entity 待校验的实体
	 * @throws ServiceException 如果实体不属于当前租户
	 */
	protected void verifyTenantOwnership(DataEntity<?> entity) {
		if (entity == null || entity.getCompany() == null) {
			return;
		}
		String tenantId = SessionUtils.getLoginTenantId();
		if (StringUtils.isNotBlank(tenantId) && !"null".equals(tenantId)) {
			String entityCompanyId = entity.getCompany().getId();
			if (StringUtils.isNotBlank(entityCompanyId) && !tenantId.equals(entityCompanyId)) {
				throw new ServiceException("无权访问其他租户数据");
			}
		}
	}
	
	
	/**
	 * 构建带租户信息的分页请求
	 * 从参数中提取company_id，获取机构信息，构建PageRequest
	 * @param parameters 查询参数
	 * @param orderby 排序
	 * @return PageRequest
	 */
	protected PageRequest buildTenantPageRequest(List<Parameter> parameters, String orderby) {
		String id = Parameter.extractAndRemoveCompanyId(parameters);
		String institution = companyService.getInstitution(id);
		return new PageRequest(parameters, orderby, id, institution);
	}

	/**
	 * 构建带租户信息的分页请求（带分页参数）
	 * @param parameters 查询参数
	 * @param offset 偏移量
	 * @param limit 每页数量
	 * @param orderby 排序
	 * @return PageRequest
	 */
	protected PageRequest buildTenantPageRequest(List<Parameter> parameters, int offset, int limit, String orderby) {
		String id = Parameter.extractAndRemoveCompanyId(parameters);
		String institution = companyService.getInstitution(id);
		return new PageRequest(offset, limit, parameters, orderby, id, institution);
	}

	/**
	 * 检查是否有重复的数据
	 * @param curdDao   dao
	 * @param entity	业务数据
	 * @param colMaps	检查字段
	 * @return
	 */
	protected boolean exists(CrudDao<?> curdDao, DataEntity<?> entity, Map<String, String> colMaps) {
		if(colMaps == null || colMaps.size() <= 0) {
			return false;
		}

		List<Parameter> params = Lists.newArrayList();

		if(!StringUtils.isBlank(entity.getId())) {
			params.add(new Parameter("id", "<>", entity.getId()));
		}


		for(String column: colMaps.keySet()) {
			params.add(new Parameter(StringUtils.toUnderScoreCase(column), "=", Reflections.invokeGetter(entity, colMaps.get(column))));
		}

		PageRequest pageRequest = new PageRequest(params);
		int row = curdDao.count(pageRequest);
		return row > 0;
	}

	/**
	 * 插入操作
	 * @param entity
	 * @return
	 */
	protected int doInsert(T entity) {
		return dao.insert(entity);
	}


	/**
	 * 更新操作
	 * @param entity
	 * @return
	 */
	protected int doUpdate(T entity) {
		return dao.update(entity);
	}

	/**
	 * 删除操作
	 * @param entity
	 * @return
	 */
	protected int doDelete(T entity) {
		return dao.delete(entity);
	}

	/**
	 * 生成操作日志
	 * @param actionTypeId  操作类型Id
	 * @param entity		操作的实体对象
	 * @return
	 */
	protected Action createAction(String actionTypeId, T entity) {
		// 没有del_flag字段
		if(entity == null || !entity.getBusTableHasDelFlag()) {
			return null;
		}
		Action action = new Action();

		DictItem di = new DictItem();
		di.setValue(actionTypeId);
		action.setActionType(di);
		action.setObjectType(new GenTable(entity.getBusTableId()));
		action.setObjectId(entity.getId());
		action.setObjectName((String)Reflections.invokeGetter(entity, "name"));
		action.setStatus("0");


		List<ActionRecycle> recycles = Lists.newArrayList();
		// 删除时记录把保存的数据保存到回收站
		if(ActionConstants.ACTION_DELETED.equals(actionTypeId)) {
			ActionRecycle recycle = new ActionRecycle();
			recycle.setTableName(entity.getBusTableName());
			recycle.setObjectId(entity.getId());
			recycle.setObjectName((String)Reflections.invokeGetter(entity, "name"));
			recycles.add(recycle);
		}
		action.setActionRecycleList(recycles);
		return action;
	}

	/**
	 * 批量更新之前执行方法，需要手动调用
	 * @param entities
	 * @return
	 */
	protected List<DataEntity> preUpdateList(List<DataEntity> entities) {
		for(DataEntity entity: entities) {
			entity.preUpdate();
		}
		return entities;
	}

	/**
	 * 保存操作日志
	 */
	protected void saveAction(Action action) {
		actionSaver.saveAction(action);
	}

	/**
	 * 分页查询模板方法
	 * @param countFn 查询总数的函数
	 * @param listFn 查询列表的函数
	 * @return 分页结果
	 */
	protected <R> Page<R> paginate(java.util.function.Supplier<Integer> countFn, java.util.function.Supplier<List<R>> listFn) {
		int total = countFn.get();
		List<R> list = total > 0 ? listFn.get() : java.util.Collections.emptyList();
		return new Page<>((long) total, list);
	}

	/**
	 * 构建单条件查询参数
	 * @param column 列名
	 * @param operator 操作符
	 * @param value 值
	 * @return PageRequest
	 */
	protected PageRequest buildPageRequest(String column, String operator, Object value) {
		return new PageRequest(Lists.newArrayList(new Parameter(column, operator, value)));
	}

	/**
	 * 构建多条件精确匹配查询参数
	 * @param exactFilters 精确匹配条件Map
	 * @return PageRequest
	 */
	protected PageRequest buildPageRequest(Map<String, Object> exactFilters) {
		List<Parameter> params = Lists.newArrayList();
		for (Map.Entry<String, Object> entry : exactFilters.entrySet()) {
			params.add(new Parameter(entry.getKey(), "=", entry.getValue()));
		}
		return new PageRequest(params);
	}

	/**
	 * 子表同步：比较旧列表和新列表，执行删除、更新、新增操作
	 * @param oldList 旧数据列表
	 * @param newList 新数据列表
	 * @param childDao 子表DAO
	 * @param idMatcher ID匹配器
	 */
	@SuppressWarnings("unchecked")
	protected <E> void syncChildList(List<E> oldList, List<E> newList,
									 CrudDao<E> childDao, java.util.function.BiPredicate<E, E> idMatcher) {
		List<E> toDelete = new java.util.ArrayList<>();
		List<E> toInsert = new java.util.ArrayList<>();
		List<E> toUpdate = new java.util.ArrayList<>();

		// 用 Set 缓存 newList 的 ID，将 O(n*m) 降为 O(n+m)
		java.util.Set<String> newIds = new java.util.HashSet<>();
		for (E newItem : newList) {
			if (newItem instanceof DataEntity) {
				String id = ((DataEntity<?>) newItem).getId();
				if (StringUtils.isNotBlank(id)) {
					newIds.add(id);
				}
			}
		}

		// 找出需要删除的记录（oldList 中不在 newList 中的）
		for (E oldItem : oldList) {
			if (oldItem instanceof DataEntity) {
				String oldId = ((DataEntity<?>) oldItem).getId();
				if (!newIds.contains(oldId)) {
					toDelete.add(oldItem);
				}
			} else {
				// 非 DataEntity 类型回退到 BiPredicate 匹配
				boolean found = newList.stream().anyMatch(newItem -> idMatcher.test(oldItem, newItem));
				if (!found) {
					toDelete.add(oldItem);
				}
			}
		}

		// 找出需要新增和更新的记录
		for (E newItem : newList) {
			if (newItem instanceof DataEntity) {
				DataEntity<?> entity = (DataEntity<?>) newItem;
				if (StringUtils.isBlank(entity.getId())) {
					entity.preInsert();
					toInsert.add(newItem);
				} else {
					entity.preUpdate();
					toUpdate.add(newItem);
				}
			}
		}

		// 执行批量操作
		if (!toDelete.isEmpty()) {
			childDao.bulkDelete(toDelete);
		}
		if (!toUpdate.isEmpty()) {
			childDao.bulkUpdate(toUpdate);
		}
		if (!toInsert.isEmpty()) {
			childDao.bulkInsert(toInsert);
		}
	}

}
