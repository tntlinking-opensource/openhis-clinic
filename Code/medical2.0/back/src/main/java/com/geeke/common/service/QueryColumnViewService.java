package com.geeke.common.service;

import java.util.Arrays;
import java.util.List;

import com.geeke.gen.dao.GenSchemeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.gen.dao.GenTableColumnDao;
import com.geeke.gen.dao.GenTableDao;
import com.geeke.gen.entity.GenScheme;
import com.geeke.gen.entity.GenTable;
import com.geeke.gen.entity.GenTableColumn;
import com.geeke.sys.dao.DictItemDao;
import com.geeke.sys.entity.DictItem;
import com.geeke.utils.StringUtils;
import com.google.common.collect.Lists;

/**
 * 查询条件的业务字段Service
 * 
 * @author lys
 * @version 2020/8/17
 */
@Service
@Transactional(readOnly = true)
public class QueryColumnViewService {

	
	@Autowired
	private GenTableColumnDao genTableColumnDao;
	
	@Autowired
	private GenTableDao genTableDao;

	@Autowired
	private GenSchemeDao genSchemeDao;
	
	@Autowired
	private DictItemDao dictItemDao;

	@Transactional(readOnly = false)
	public List<GenTableColumn> listColumn(String tableId, String schemeId) {
		List<DictItem> itemList = getItemList();
		
		List<Parameter> Params = Lists.newArrayList();
		Params.add(new Parameter("gen_table_id", "=", tableId));
		
		// 排除不在列表中显示的字段
		String[] strings = {"del_flag"};
		Params.add(new Parameter("name", "not in", Arrays.asList(strings)));
		Params.add(new Parameter("show_type", "<>", "'PassWordInput'"));
		Params.add(new Parameter("show_type", "<>", "'MultiFileUpload'"));
		
		PageRequest pageRequest = new PageRequest(Params);
		List<GenTableColumn> columns = genTableColumnDao.listAll(pageRequest);
		
		
		//自定义java类型
		for(GenTableColumn column: columns) {
			if("ids".equals(column.getName())) {
				column.setJavaType(getDictItem(itemList, "5001", "This"));
				column.setJavaField("parent.ids|name");
				column.setShowType(getDictItem(itemList, "5003", "SelectTree"));
				column.setQueryType(getDictItem(itemList, "5002", "right_like"));
			
				GenTable table = genTableDao.get(tableId);
				GenScheme scheme = genSchemeDao.get(schemeId);
				scheme.setGenTable(table);
				column.setCustomType(scheme);				
			} else if (column.getJavaType() != null && "This".equals(column.getJavaType().getValue())) {
				GenTable table = genTableDao.get(tableId);
				GenScheme scheme = genSchemeDao.get(schemeId);
				scheme.setGenTable(table);
				column.setCustomType(scheme);
			} else if(column.getCustomType() != null && !StringUtils.isNullOrEmpty(column.getCustomType().getId())) {
				GenScheme scheme = genSchemeDao.get(column.getCustomType().getId());
				if(scheme != null) {
					GenTable table = genTableDao.get(scheme.getGenTable().getId());
					scheme.setGenTable(table);
					column.setCustomType(scheme);
				}
				
				column.setCustomType(scheme);
			}
			
		}
		
		return columns;
		
	}
	
	
	private List<DictItem> getItemList() {
		List<Parameter> params = Lists.newArrayList();
		String[] strings = {"5001", "5002", "5003"};
		params.add(new Parameter("dict_type_id", "in", Arrays.asList(strings)));
		PageRequest pageRequest = new PageRequest(params, null);
		return dictItemDao.listAll(pageRequest);
	}
	
	private static DictItem getDictItem(List<DictItem> itemList, String typeId, String value) {
		if(StringUtils.isBlank(typeId) || StringUtils.isBlank(value) || itemList == null){
			return null;
		}
		for(DictItem item: itemList) {
			
			if(value.equals(item.getValue()) && typeId.equals(item.getDictType().getId())) {
				return item;
			}
		}
		return null;
	}
}
