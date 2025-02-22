package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    List<Long> getSetmealIdByDishIds(List<Long> dishIds);

    void saveBatch(List<SetmealDish> setmealDishes);

    void deleteBySetmealIds(List<Long> setmeal_ids);

    List<SetmealDish> getBySetmealId(Long id);

    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void deleteBySetmealId(Long setmeal_id);

    void insertBatch(List<SetmealDish> setmealDishes);
}
