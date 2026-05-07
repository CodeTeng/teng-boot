package com.lt.boot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lt.boot.mapper.MenuMapper;
import com.lt.boot.model.entity.Menu;
import com.lt.boot.model.vo.menu.MenuVO;
import com.lt.boot.service.MenuService;
import com.lt.boot.utils.CollUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu>
        implements MenuService {

    @Resource
    private MenuMapper menuMapper;

    @Override
    public List<MenuVO> listMenuTree() {
        // 查询启用的菜单，按顺序排序
        List<Menu> menuList = lambdaQuery()
                .eq(Menu::getStatus, 1)
                .orderByAsc(Menu::getOrderNum)
                .list();
        return buildMenuTree(menuList);
    }

    @Override
    public List<MenuVO> listMenuTreeByRoleIds(List<Long> roleIds) {
        if (CollUtils.isEmpty(roleIds)) {
            return new ArrayList<>();
        }
        List<Menu> menuList = menuMapper.selectMenusByRoleIds(roleIds);
        return buildMenuTree(menuList);
    }

    @Override
    public List<MenuVO> listAllMenuTree() {
        // 查询所有菜单（包括禁用的）
        List<Menu> menuList = lambdaQuery()
                .orderByAsc(Menu::getOrderNum)
                .list();
        return buildMenuTree(menuList);
    }

    /**
     * 构建菜单树
     */
    private List<MenuVO> buildMenuTree(List<Menu> menuList) {
        if (CollUtils.isEmpty(menuList)) {
            return new ArrayList<>();
        }
        // 转换为 VO 列表
        List<MenuVO> menuVOList = menuList.stream().map(this::convertToMenuVO).collect(Collectors.toList());
        // 过滤出顶级节点 (parentId == 0)
        return menuVOList.stream()
                .filter(menu -> menu.getParentId() == null || menu.getParentId() == 0)
                .peek(menu -> menu.setChildren(getChildren(menu.getId(), menuVOList)))
                .sorted(Comparator.comparingInt(MenuVO::getOrderNum))
                .collect(Collectors.toList());
    }

    /**
     * 递归查找子节点
     */
    private List<MenuVO> getChildren(Long parentId, List<MenuVO> menuVOList) {
        List<MenuVO> childrenList = menuVOList.stream()
                .filter(menu -> parentId.equals(menu.getParentId()))
                .peek(menu -> menu.setChildren(getChildren(menu.getId(), menuVOList)))
                .sorted(Comparator.comparingInt(MenuVO::getOrderNum))
                .collect(Collectors.toList());
        return CollUtils.isEmpty(childrenList) ? null : childrenList;
    }

    /**
     * Menu -> MenuVO 转换
     */
    private MenuVO convertToMenuVO(Menu menu) {
        MenuVO menuVO = new MenuVO();
        BeanUtils.copyProperties(menu, menuVO);
        return menuVO;
    }
}
