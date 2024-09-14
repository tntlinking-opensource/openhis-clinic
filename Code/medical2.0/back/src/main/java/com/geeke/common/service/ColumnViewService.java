package com.geeke.common.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.geeke.gen.dao.GenSchemeDao;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geeke.common.dao.ColumnViewDao;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.entity.ColumnView;
import com.geeke.gen.dao.GenTableColumnDao;
import com.geeke.gen.entity.GenScheme;
import com.geeke.gen.entity.GenTableColumn;
import com.geeke.gen.entity.GenTableColumnEx;
import com.geeke.sys.utils.SessionUtils;
import com.google.common.collect.Lists;

/**
 * 表列头管理Service
 * 
 * @author lys
 * @version 2019/2/2
 */
@Service
@Transactional(readOnly = true)
public class ColumnViewService {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	final private static String OPERATE_COLUMN_NAME = "operate_column";	// 固定的列，用于存放操作列的用户自定义列宽
	
	@Autowired
	private ColumnViewDao dao;

	@Autowired
	private GenTableColumnDao genTableColumnDao;

	@Autowired
	private GenSchemeDao genSchemeDao;

	/**
	 * 获取显示列表
	 * @param routerId
	 * @param tableId
	 * @return
	 */
	@Transactional(readOnly = false)
	public List<ColumnView> listColumn(String routerId, String tableId ) {
		return listColumn(routerId, tableId, false);
	}
	
	/**
	 * 获取显示列表
	 * @param routerId
	 * @param tableId
	 * @param isAll	显示全部列	
	 * @return
	 */
	@Transactional(readOnly = false)
	public List<ColumnView> listColumn(String routerId, String tableId, boolean isAll) {
		String userId = SessionUtils.getUserJson().getString("id");
		List<ColumnView> list = Lists.newArrayList();

		List<Parameter> Params = Lists.newArrayList();
			
		Params.add(new Parameter("gen_table_id", "=", tableId));
		
		// 排除不在列表中显示的字段
		String[] strings = {"del_flag", "ids"};
		Params.add(new Parameter("name", "not in", Arrays.asList(strings)));
		Params.add(new Parameter("show_type", "<>", "'PassWordInput'"));
		Params.add(new Parameter("show_type", "<>", "'MultiFileUpload'"));
		
		PageRequest pageRequest = new PageRequest(Params);
		List<GenTableColumn> columns = genTableColumnDao.listAll(pageRequest);

		for (GenTableColumn cc : columns) {
			GenTableColumnEx col = (GenTableColumnEx)cc;
			col.setSort(col.getSort() * 1000);// 业务表设置时，顺序放大10倍；表顺序时需要放大10000倍。因此需要补充1000倍。
			if ("Select".equals(col.getShowType().getValue())
					|| "SelectTree".equals(col.getShowType().getValue()) 
					|| "StringMultiSelect".equals(col.getShowType().getValue())) {
				if ("Custom".equals(col.getJavaType().getValue())) {
					GenScheme scheme = genSchemeDao.get(col.getCustomType().getId());
					String tId = scheme.getGenTable().getId(); // 自定义类型的表ID
					String[][] fieldAttrs = col.getJavaFieldAttrs();
					for (int index = 0; index < fieldAttrs.length; index++) {
						String[] attr = fieldAttrs[index];
						GenTableColumnEx c = this.getCustomerColumn(tId, attr[1]);
						c.setSort(col.getSort() + index); // 显示顺序按照主表字段的显示顺序 +
															// 1，2，3
						ColumnView column = this.mapForm(c);
						column.setProp(col.getSimpleJavaField() + "." + attr[0]);
						// 只有一个属性值的时候，直接使用主表上字段名称作为列头；否则格式为主表字段名.子表的字段名
						if(fieldAttrs.length == 1) {
							column.setMiniWidth(cc.getWidth());   // 只有一个属性时，列宽使用当前业务表的配置列宽；否则使用原列宽
							column.setLabel(col.getComments());
						} else {
							column.setLabel(col.getComments() + "."
									+ column.getLabel());
						}
						if (!"1".equals(col.getIsList())) { // 对象设置不显示时，所有属性不显示
							column.setDisplay(false);
						}
						column.setTableId(tableId);
						column.setRouterId(routerId);
						column.setUserId(userId);
						if("name".equals(c.getName())) {
							column.setMiniWidth(cc.getWidth());   // 属性表字段为name时，使用当前表配置的列宽
							// 字典项的名称列，单元格居中
							if("6006".equals(scheme.getId())) {
								column.setAlign("center");
							}
						}
						list.add(column);
					}
				} else if ("This".equals(col.getJavaType().getValue())) {
					String[][] fieldAttrs = col.getJavaFieldAttrs();
					for (int index = 0; index < fieldAttrs.length; index++) {
						String[] attr = fieldAttrs[index];
						GenTableColumnEx c = this.getCustomerColumn(columns,	attr[0]);
						ColumnView column = this.mapForm(c);
						column.setProp(col.getSimpleJavaField() + "." + attr[0]);
						// 只有一个属性值的时候，直接使用主表上字段名称作为列头；否则格式为主表字段名.子表的字段名
						if(fieldAttrs.length == 1) {
							column.setMiniWidth(cc.getWidth());   // 只有一个属性时，列宽使用当前业务表的配置列宽；否则使用原列宽
							column.setLabel(col.getComments());
						} else {
							column.setLabel(col.getComments() + "."
									+ column.getLabel());
						}
						if (!"1".equals(col.getIsList())) { // 对象设置不显示时，所有属性不显示
							column.setDisplay(false);
						}
						column.setTableId(tableId);
						column.setRouterId(routerId);
						column.setUserId(userId);
						column.setSort(col.getSort() + index + 1 ); // 显示顺序按照主表字段的显示顺序 +1，2，3	
						if("name".equals(c.getName())) {
							column.setMiniWidth(cc.getWidth());   // 属性表字段为name时，使用当前表配置的列宽
						}
						list.add(column);
					}
				} else {
					ColumnView column = this.mapForm(col);
					column.setProp(col.getSimpleJavaField());
					column.setTableId(tableId);
					column.setRouterId(routerId);
					column.setUserId(userId);
					list.add(column);
				}
			} else {
				ColumnView column = this.mapForm(col);
				column.setTableId(tableId);
				column.setRouterId(routerId);
				column.setUserId(userId);
				list.add(column);
			}
		}
				
		List<ColumnView> lv = dao.list(userId, routerId, tableId); // 用户个性化配置信息
		for (ColumnView cv : lv) {
			if(OPERATE_COLUMN_NAME.equals(cv.getProp())) {  // 操作列的属性
				ColumnView operateColumn = new ColumnView();
				operateColumn.setId(cv.getId());
				operateColumn.setSort(list.size() * 100000);
				operateColumn.setProp(OPERATE_COLUMN_NAME);
				operateColumn.setDisplay(cv.getDisplay());
				operateColumn.setWidth(cv.getWidth());
				operateColumn.setTableId(tableId);
				operateColumn.setRouterId(routerId);
				operateColumn.setUserId(userId);
				list.add(operateColumn);
				continue;
			}
			
			boolean found = false;
			for (ColumnView column : list) {
				if (cv.getProp().equals(column.getProp())) {
					column.setId(cv.getId());
					column.setDisplay(cv.getDisplay());
					column.setSort(cv.getSort());
					column.setWidth(cv.getWidth());
					found = true;
					break;
				}
			}
			if (!found) { // 字段已经不存在
				this.delete(cv.getId());
			}
		}
		Collections.sort(list);
		List<ColumnView> adds = Lists.newArrayList();			// 新增列
		List<ColumnView> updates = Lists.newArrayList();		// 更新列
		for (int i = 0; i < list.size(); i++) {
			ColumnView cv = list.get(i);
			// 重置显示顺序
			cv.setSort((i + 1) * 10000);

			if(isAll) {
				cv.setDisplay(true);
			}
			
			if (StringUtils.isBlank(cv.getId())) {
				cv.preInsert();
				adds.add(cv);
			} else {
				updates.add(cv);
			}
		}
		if(updates.size() > 0) {
			dao.bulkUpdate(updates);
		}
		if(adds.size() > 0) {
			dao.bulkInsert(adds);
		}
		
		return list;
	}

	/**
	 * 保存数据（插入或更新）
	 * 
	 * @param cv
	 */
	@Transactional(readOnly = false)
	public int save(ColumnView cv) {
		String userId = SessionUtils.getUserJson().getString("id");
		cv.setUserId(userId);
		int rows = 0;
		if (StringUtils.isBlank(cv.getId())) {
			cv.preInsert();
			rows = dao.insert(cv);
		} else {

			rows = dao.update(cv);
		}
		return rows;
	}

	/**
	 * 还原显示默认列
	 * 
	 * @param routerId
	 * @param tableId
	 */
	@Transactional(readOnly = false)
	public void reset(String routerId, String tableId) {
		String userId = SessionUtils.getUserJson().getString("id");
		dao.deleteAll(userId, routerId, tableId);
	}

	/**
	 * 删除已经不存在的列
	 * 
	 * @param id
	 */
	@Transactional(readOnly = false)
	public void delete(String id) {
		dao.delete(id);
	}

	private ColumnView mapForm(GenTableColumnEx col) {
		ColumnView column = new ColumnView();
		column.setProp(col.getJavaFieldId());
		column.setLabel(col.getComments());
		if ("Switch".equals(col.getShowType().getValue())
				|| "Checkbox".equals(col.getShowType().getValue())
				|| "Radio".equals(col.getShowType().getValue())) {
			column.setAlign("center");
		} else if ("Date".equals(col.getSimpleJavaType())
				|| "Long".equals(col.getSimpleJavaType())
				|| "Integer".equals(col.getSimpleJavaType())
				|| "Float".equals(col.getSimpleJavaType())
				|| "Double".equals(col.getSimpleJavaType())
				|| "BigDecimal".equals(col.getSimpleJavaType())) {
			column.setAlign("right");
		} else {
			column.setAlign("left");
		}
		column.setMiniWidth(col.getWidth());
		column.setDisplay("1".equals(col.getIsList()));
		column.setSort(col.getSort());
		column.setShowType(col.getShowType().getValue());
		column.setJavaType(col.getJavaType().getValue());
		return column;
	}

	private GenTableColumnEx getCustomerColumn(String tableId, String name) {
		List<Parameter> params = Lists.newArrayList();
		params.add(new Parameter("gen_table_id", "=", tableId));
		params.add(new Parameter("name", "=", name));

		PageRequest pageRequest = new PageRequest(params);
		List<GenTableColumn> cs = genTableColumnDao.listAll(pageRequest);
		if (cs != null && cs.size() == 1) {
			return (GenTableColumnEx)cs.get(0);
		}
		return null;
	}

	private GenTableColumnEx getCustomerColumn(List<GenTableColumn> genTableColumns, String javaField) {
		for (GenTableColumn cc : genTableColumns) {
			GenTableColumnEx column = (GenTableColumnEx)cc;
			if (javaField.equals(column.getJavaField())) {
				return column;
			}
		}
		return null;
	}
}
