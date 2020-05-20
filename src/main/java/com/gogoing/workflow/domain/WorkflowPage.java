package com.gogoing.workflow.domain;


import javax.validation.constraints.Min;

/**
 * 分页类
 * @author lhj
 */
public class WorkflowPage extends AbstractParam {

    /**
     * 页码,传-1代表不分页查询
     */
    @Min(value = -1, message = "page不能少于-1")
    private Integer page = 1;

    /**
     * 每页数量
     */
    @Min(value = 0, message = "size不能少于0")
    private Integer size = 10;

    public Integer getOffset() {
        if(page <= 1){
            return 0;
        }
        return (page-1) * size;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
