package com.ycusoft.service.impl;

import com.ycusoft.dao.IMenuDao;
import com.ycusoft.dao.impl.MenuDaoImpl;
import com.ycusoft.entity.po.Menu;
import com.ycusoft.service.IMenuService;

import java.util.List;

/**
 * 菜单业务逻辑实现类
 * 实现菜单相关的业务操作
 *
 * @author hq
 * @since 2026-05-25
 */
public class MenuServiceImpl implements IMenuService {

    /**
     * 菜单数据访问对象
     */
    private final IMenuDao menuDao;

    /**
     * 构造函数
     */
    public MenuServiceImpl() {
        this.menuDao = new MenuDaoImpl();
    }

    /**
     * 根据菜单ID查询菜单
     *
     * @param id 菜单ID
     * @return 菜单对象，不存在返回null
     */
    @Override
    public Menu getMenuById(Long id) {
        if (id == null || id <= 0) {
            return null;
        }
        return menuDao.selectById(id);
    }

    /**
     * 根据菜单路径查询菜单
     *
     * @param path 菜单路径
     * @return 菜单对象，不存在返回null
     */
    @Override
    public Menu getMenuByPath(String path) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }
        return menuDao.selectByPath(path);
    }

    /**
     * 查询所有菜单
     *
     * @return 菜单列表
     */
    @Override
    public List<Menu> getAllMenus() {
        return menuDao.selectAll();
    }

    /**
     * 根据父菜单ID查询子菜单
     *
     * @param parentId 父菜单ID
     * @return 子菜单列表
     */
    @Override
    public List<Menu> getMenusByParentId(Long parentId) {
        if (parentId == null) {
            return null;
        }
        return menuDao.selectByParentId(parentId);
    }

    /**
     * 根据角色ID查询菜单列表
     *
     * @param roleId 角色ID
     * @return 菜单列表
     */
    @Override
    public List<Menu> getMenusByRoleId(Long roleId) {
        if (roleId == null || roleId <= 0) {
            return null;
        }
        return menuDao.selectByRoleId(roleId);
    }

    /**
     * 根据用户ID查询菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    @Override
    public List<Menu> getMenusByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            return null;
        }
        return menuDao.selectByUserId(userId);
    }

    /**
     * 创建菜单
     *
     * @param menu 菜单对象
     * @return 是否创建成功
     */
    @Override
    public boolean createMenu(Menu menu) {
        if (menu == null || menu.getMenuName() == null || menu.getMenuName().trim().isEmpty()) {
            return false;
        }

        // 检查菜单路径是否已存在
        if (menu.getPath() != null && !menu.getPath().isEmpty()) {
            if (menuDao.existsByPath(menu.getPath())) {
                return false;
            }
        }

        // 设置默认值
        if (menu.getParentId() == null) {
            menu.setParentId(0L);
        }
        if (menu.getOrderNum() == null) {
            menu.setOrderNum(0);
        }

        int result = menuDao.insert(menu);
        return result > 0;
    }

    /**
     * 更新菜单信息
     *
     * @param menu 菜单对象（必须包含ID）
     * @return 是否更新成功
     */
    @Override
    public boolean updateMenu(Menu menu) {
        if (menu == null || menu.getId() == null || menu.getId() <= 0) {
            return false;
        }

        // 检查菜单是否存在
        Menu existingMenu = menuDao.selectById(menu.getId());
        if (existingMenu == null) {
            return false;
        }

        int result = menuDao.update(menu);
        return result > 0;
    }

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     * @return 是否删除成功
     */
    @Override
    public boolean deleteMenu(Long id) {
        if (id == null || id <= 0) {
            return false;
        }

        // 检查菜单是否存在
        Menu menu = menuDao.selectById(id);
        if (menu == null) {
            return false;
        }

        int result = menuDao.deleteById(id);
        return result > 0;
    }

    /**
     * 检查菜单路径是否可用
     *
     * @param path 菜单路径
     * @return true-可用，false-不可用
     */
    @Override
    public boolean checkPath(String path) {
        if (path == null || path.trim().isEmpty()) {
            return false;
        }
        return !menuDao.existsByPath(path);
    }
}