package com.lisz.arica.mapper;

import com.lisz.arica.entity.Item;
import com.lisz.arica.entity.ItemExample;
import org.springframework.stereotype.Repository;

/**
 * ItemDAO继承基类
 */
@Repository
public interface ItemDAO extends MyBatisBaseDao<Item, Integer, ItemExample> {
}