package com.gogoing.workflow.domain;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * 任务附件信息
 *
 * @author huangzh
 * @since 2019/12/27 10:16
 */
@Data
@ApiModel("任务附件信息")
public class TaskAttachmentParam implements Serializable {
    /**
     * 附件名称
     */
    private String name;
    /**
     * 附件描述
     */
    private String description;
    /**
     * 附件类型
     */
    private String type;
    /**
     * 附件字节数据
     */
    private byte[] fileBytes;
}
