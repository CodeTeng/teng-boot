package com.lt.boot.mapper;

import com.lt.boot.model.entity.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author teng
 * @description 针对表【menu(菜单权限表)】的数据库操作Mapper
 * @createDate 2024-02-18 20:45:00
 * @Entity com.lt.boot.model.entity.Menu
 */
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 根据角色ID列表查询菜单列表
     */
    List<Menu> selectMenusByRoleIds(@Param("roleIds") List<Long> roleIds);
}
