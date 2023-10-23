package com.example.green.mapper;

import com.example.green.entity.Good;
import com.example.green.entity.GoodStandard;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.green.entity.dto.GoodDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

public interface GoodMapper extends BaseMapper<Good> {

    @Select("select * from good_standard where good_id = #{id}")
    List<GoodStandard> getStandardById(int id);

    List<GoodDTO> findFrontGoods();

    @Update("update good set is_delete = 1 where id = #{id}")
    void fakeDelete(Long id);

    void insertGood(@Param("good") Good good);

    @Select("SELECT discount * MIN(price) FROM good_standard gs, good WHERE good.id = gs.good_id AND good.id = #{id} ")
    BigDecimal getMinPrice(Long id);

    boolean saleGood(@Param("id")Long goodId,@Param("count") int count,@Param("money") BigDecimal totalPrice);


    @Select("SELECT * FROM `good` WHERE is_delete = 0 ORDER BY sale_money DESC LIMIT 0,#{num}")
    List<Good> getSaleRank(int num);
}
