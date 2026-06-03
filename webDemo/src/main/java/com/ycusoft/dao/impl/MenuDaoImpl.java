package com.ycusoft.dao.impl;

import com.ycusoft.dao.IMenuDao;
import com.ycusoft.entity.po.Menu;
import com.ycusoft.utils.DBUtil;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.List;

/**
 * 菜单数据访问实现类
 * 实现菜单相关的数据库操作
 *
 * @author hq
 * @since 2026-05-25
 */
public class MenuDaoImpl implements IMenuDao {

    /**
     * 根据菜单ID查询菜单
     *
     * @param id 菜单ID
     * @return 菜单对象，不存在返回null
     */
    @Override
    public Menu selectById(Long id) {
        String sql = "SELECT id, parent_id as parentId, menu_name as menuName, path, component, " +
                     "query, route_name as routeName, icon, order_num as orderNum, remark, " +
                     "create_time as createTime, create_by as createBy, " +
                     "update_time as updateTime, update_by as updateBy " +
                     "FROM menu WHERE id = ?";
        try {
            return DBUtil.getQueryRunner().query(sql, new BeanHandler<>(Menu.class), id);
        } catch (SQLException e) {
            throw new RuntimeException("查询菜单失败", e);
        }
    }

    /**
     * 根据菜单路径查询菜单
     *
     * @param path 菜单路径
     * @return 菜单对象，不存在返回null
     */
    @Override
    public Menu selectByPath(String path) {
        String sql = "SELECT id, parent_id as parentId, menu_name as menuName, path, component, " +
                     "query, route_name as routeName, icon, order_num as orderNum, remark, " +
                     "create_time as createTime, create_by as createBy, " +
                     "update_time as updateTime, update_by as updateBy " +
                     "FROM menu WHERE path = ?";
        try {
            return DBUtil.getQueryRunner().query(sql, new BeanHandler<>(Menu.class), path);
        } catch (SQLException e) {
            throw new RuntimeException("查询菜单失败", e);
        }
    }

    /**
     * 查询所有菜单
     *
     * @return 菜单列表
     */
    @Override
    public List<Menu> selectAll() {
        String sql = "SELECT id, parent_id as parentId, menu_name as menuName, path, component, " +
                     "query, route_name as routeName, icon, order_num as orderNum, remark, " +
                     "create_time as createTime, create_by as createBy, " +
                     "update_time as updateTime, update_by as updateBy " +
                     "FROM menu ORDER BY parent_id ASC, order_num ASC";
        try {
            return DBUtil.getQueryRunner().query(sql, new BeanListHandler<>(Menu.class));
        } catch (SQLException e) {
            throw new RuntimeException("查询菜单列表失败", e);
        }
    }

    /**
     * 根据父菜单ID查询子菜单
     *
     * @param parentId 父菜单ID
     * @return 子菜单列表
     */
    @Override
    public List<Menu> selectByParentId(Long parentId) {
        String sql = "SELECT id, parent_id as parentId, menu_name as menuName, path, component, " +
                     "query, route_name as routeName, icon, order_num as orderNum, remark, " +
                     "create_time as createTime, create_by as createBy, " +
                     "update_time as updateTime, update_by as updateBy " +
                     "FROM menu WHERE parent_id = ? ORDER BY order_num ASC";
        try {
            return DBUtil.getQueryRunner().query(sql, new BeanListHandler<>(Menu.class), parentId);
        } catch (SQLException e) {
            throw new RuntimeException("查询子菜单失败", e);
        }
    }

    /**
     * 根据角色ID查询菜单列表
     *
     * @param roleId 角色ID
     * @return 菜单列表
     */
    @Override
    public List<Menu> selectByRoleId(Long roleId) {
        String sql = "SELECT DISTINCT m.id, m.parent_id as parentId, m.menu_name as menuName, " +
                     "m.path, m.component, m.query, m.route_name as routeName, m.icon, " +
                     "m.order_num as orderNum, m.remark, " +
                     "m.create_time as createTime, m.create_by as createBy, " +
                     "m.update_time as updateTime, m.update_by as updateBy " +
                     "FROM menu m JOIN role_menu rm ON m.id = rm.menu_id " +
                     "WHERE rm.role_id = ? ORDER BY m.parent_id ASC, m.order_num ASC";
        try {
            return DBUtil.getQueryRunner().query(sql, new BeanListHandler<>(Menu.class), roleId);
        } catch (SQLException e) {
            throw new RuntimeException("查询角色菜单失败", e);
        }
    }

    /**
     * 根据用户ID查询菜单列表（通过角色关联）
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    @Override
    public List<Menu> selectByUserId(Long userId) {
        String sql = "SELECT DISTINCT m.id, m.parent_id as parentId, m.menu_name as menuName, " +
                     "m.path, m.component, m.query, m.route_name as routeName, m.icon, " +
                     "m.order_num as orderNum, m.remark, " +
                     "m.create_time as createTime, m.create_by as createBy, " +
                     "m.update_time as updateTime, m.update_by as updateBy " +
                     "FROM menu m JOIN role_menu rm ON m.id = rm.menu_id " +
                     "JOIN user_role ur ON rm.role_id = ur.role_id " +
                     "WHERE ur.user_id = ? ORDER BY m.parent_id ASC, m.order_num ASC";
        try {
            return DBUtil.getQueryRunner().query(sql, new BeanListHandler<>(Menu.class), userId);
        } catch (SQLException e) {
            throw new RuntimeException("查询用户菜单失败", e);
        }
    }

    /**
     * 插入菜单记录
     *
     * @param menu 菜单对象
     * @return 影响的行数
     */
    @Override
    public int insert(Menu menu) {
        String sql = "INSERT INTO menu (parent_id, menu_name, path, component, query, route_name, " +
                     "icon, order_num, remark, create_time, create_by, update_time, update_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), ?, NOW(), ?)";
        try {
            return DBUtil.getQueryRunner().update(sql,
                    menu.getParentId(),
                    menu.getMenuName(),
                    menu.getPath(),
                    menu.getComponent(),
                    menu.getQuery(),
                    menu.getRouteName(),
                    menu.getIcon(),
                    menu.getOrderNum(),
                    menu.getRemark(),
                    menu.getCreateBy(),
                    menu.getUpdateBy());
        } catch (SQLException e) {
            throw new RuntimeException("插入菜单失败", e);
        }
    }

    /**
     * 更新菜单记录
     *
     * @param menu 菜单对象（必须包含ID）
     * @return 影响的行数
     */
    @Override
    public int update(Menu menu) {
        String sql = "UPDATE menu SET parent_id = ?, menu_name = ?, path = ?, component = ?, " +
                     "query = ?, route_name = ?, icon = ?, order_num = ?, remark = ?, " +
                     "update_time = NOW(), update_by = ? WHERE id = ?";
        try {
            return DBUtil.getQueryRunner().update(sql,
                    menu.getParentId(),
                    menu.getMenuName(),
                    menu.getPath(),
                    menu.getComponent(),
                    menu.getQuery(),
                    menu.getRouteName(),
                    menu.getIcon(),
                    menu.getOrderNum(),
                    menu.getRemark(),
                    menu.getUpdateBy(),
                    menu.getId());
        } catch (SQLException e) {
            throw new RuntimeException("更新菜单失败", e);
        }
    }

    /**
     * 删除菜单记录
     *
     * @param id 菜单ID
     * @return 影响的行数
     */
    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM menu WHERE id = ?";
        try {
            return DBUtil.getQueryRunner().update(sql, id);
        } catch (SQLException e) {
            throw new RuntimeException("删除菜单失败", e);
        }
    }

    /**
     * 检查菜单路径是否存在
     *
     * @param path 菜单路径
     * @return true-存在，false-不存在
     */
    @Override
    public boolean existsByPath(String path) {
        String sql = "SELECT COUNT(*) FROM menu WHERE path = ?";
        try {
            Long count = DBUtil.getQueryRunner().query(sql, new ScalarHandler<>(), path);
            return count != null && count > 0;
        } catch (SQLException e) {
            throw new RuntimeException("检查菜单路径失败", e);
        }
    }
}