package com.lisz.arica.mapper;

import com.lisz.arica.entity.Item;
import com.lisz.arica.entity.ItemExample;
import com.lisz.arica.entity.ItemHtml;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ItemDAO继承基类
 */
@Repository
public interface ItemDAO extends MyBatisBaseDao<Item, Integer, ItemExample> {
	@Select("select * from item") //这里会自动填充父类的属性，留空子类的属性
	List<ItemHtml> selectAll();

	@Update("update item set title=#{title}, content=#{content} where id=#{id}")
	void update(Item item);
}