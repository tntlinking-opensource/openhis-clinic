package com.geeke.common.persistence;

import java.util.List;

/**
 * DAO支持类实现
 * @author lys
 * @version 2014-05-16
 * @param <T>
 */
public interface TreeDao<T extends TreeEntity<T>> extends CrudDao<T> {

	
}