package com.gogoing.workflow.utils;


import com.gogoing.workflow.domain.PageBean;
import com.gogoing.workflow.domain.WorkflowPage;

import java.util.List;

/**
 * 分页工具类
 * @author lhj
 */
public class PageUtil<T,E extends WorkflowPage> {

    public PageBean<T> buildPage(List<T> list, E e, long count){
        PageBean<T> pageBean = new PageBean<>();
        if(list != null && list.size() != 0){
            pageBean.setNumberOfElements(list.size());
            pageBean.setTotalPages((int)count/e.getSize() +1);
        }
        pageBean.setNumber(e.getPage());
        pageBean.setSize(e.getSize());
        pageBean.setTotalElements(count);
        pageBean.setContent(list);
        return pageBean;
    }

}
