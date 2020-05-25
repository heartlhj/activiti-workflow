package com.gogoing.workflow.controller;

import com.gogoing.workflow.exception.ProcessException;
import com.gogoing.workflow.service.ProcessDefineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;


/**
 * 流程任务管理
 *
 * @author lhj
 * @since 2020/04/27 10:42
 */
@RestController
@RequestMapping("/workflow/processDefine")
@Api(value = "流程模型管理", tags = "流程模型管理")
public class ProcessDefineController {

    private static final Logger log = LoggerFactory.getLogger(ProcessDefineController.class);

    @Autowired
    private ProcessDefineService processDefineService;


    /**
     * bpmn格式文件上传生成model流程图
     * @param file 上传的文件
     * @return 模型结果ProcessModelResult
     */
    @ApiOperation("bpmn格式文件上传生成model流程图")
    @PostMapping("/importModel")
    public Boolean importModel(@RequestParam("file") MultipartFile file) {
        return processDefineService.importModel(file);
    }

    /**
     *
     * @Description: 模型导出
     * @date 2019/7/11 9:06
     */
    @ApiOperation("模型导出")
    @GetMapping("/export")
    public void export(@RequestParam(required = false) String processDefinitionId,
                       HttpServletRequest request, HttpServletResponse response) {
        try {
            BpmnModel bpmnModel = processDefineService.export(processDefinitionId);
            BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
            String name = bpmnModel.getMainProcess().getId();
            byte[] xmlBytes = xmlConverter.convertToXML(bpmnModel);

            response.setHeader("Content-Disposition", "attachment; filename=" + name + ".bpmn20.xml");
            ServletOutputStream servletOutputStream = response.getOutputStream();
            response.setContentType("application/octet-stream");
            BufferedInputStream in = new BufferedInputStream(new ByteArrayInputStream(xmlBytes));

            byte[] buffer = new byte[8096];
            while (true) {
                int count = in.read(buffer);
                if (count == -1) {
                    break;
                }
                servletOutputStream.write(buffer, 0, count);
            }
            servletOutputStream.flush();
            servletOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ProcessException("模型导出失败！");
        }
    }

}
