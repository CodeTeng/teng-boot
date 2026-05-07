package com.lt.boot.service;

import com.lt.boot.model.entity.Menu;
import com.lt.boot.model.vo.menu.MenuVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author teng
 * @description 针对表【menu(菜单权限表)】的数据库操作Service
 * @createDate 2024-02-18 20:45:00
 */
public interface MenuService extends IService<Menu> {

    /**
     * 获取当前用户的菜单树
     */
    List<MenuVO> listMenuTree();

    /**
     * 根据角色ID列表获取菜单树
     */
    List<MenuVO> listMenuTreeByRoleIds(List<Long> roleIds);

    /**
     * 获取完整的菜单树（管理端编辑用）
     */
    List<MenuVO> listAllMenuTree();
}
