package com.swidy.miaosha.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.swidy.miaosha.domain.MiaoshaGoods;
import com.swidy.miaosha.vo.GoodsVo;

@Mapper
public interface GoodsDao {
	
	@Select("select g.*,mg.miaosha_price, mg.stock_count, mg.start_date, mg.end_date from miaosha_goods mg left join goods g on mg.goods_id = g.id") 
	public List<GoodsVo> listGoodsVo();

	@Select("select g.*,mg.miaosha_price, mg.stock_count, mg.start_date, mg.end_date from miaosha_goods mg left join goods g on mg.goods_id = g.id where g.id = #{goodsId}") 
	public GoodsVo getGoodsVoByGoodsId(Long goodsId);

	@Select("update miaosha_goods set stock_count = stock_count -1 where goods_id = #{goodsId} and stock_count > 0") 
	public Object reduceStock(MiaoshaGoods g);

}