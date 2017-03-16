package org.hisoka.orm.relative.mybatis;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Hinsteny
 * @Describtion
 * @date 2016/11/3
 * @copyright: 2016 All rights reserved.
 */
public interface BaseDAO<T> {

    /**
     * 新增一条数据
     *
     * @param t
     * @return
     */
    Integer save(T t);

    /**
     * 更新一条数据
     *
     * @param t
     * @return
     */
    int update(T t);

    /**
     * 删除一条数据
     *
     * @param id
     */
    void delete(Integer id);

    /**
     * 根据id查找一条数据
     *
     * @param id
     * @return
     */
    T findById(Integer id);

    /**
     * 查找所有
     *
     * @return
     */
    List<T> findAll(@Param("filterRules") Map<String, Object> filterRules);

    /**
     * 删除所有
     *
     * @return
     */
    void deleteAll(@Param("filterRules") Map<String, Object> filterRules);

    /**
     * 分页查找
     *
     * @param filterRules
     * @param pageQuery
     * @return
     */
    List<T> findByPage(@Param("filterRules") Map<String, Object> filterRules, @Param("pageQuery") PageQuery pageQuery);

    /**
     * 获取个数
     *
     * @param filterRules
     * @return
     */
    Integer getTotalCount(@Param("filterRules") Map<String, Object> filterRules);
}
