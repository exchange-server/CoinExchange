package com.bizzan.bitrade.util;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.bizzan.bitrade.service.LocaleMessageSourceService;
import org.apache.poi.ss.usermodel.Workbook;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class FileUtil<E> {
    @Resource
    private LocaleMessageSourceService msService;

    public MessageResult exportExcel(HttpServletRequest request, HttpServletResponse response, List<E> list, String name) throws Exception {
        if (list.isEmpty()) {
            return MessageResult.error(-1, msService.getMessage(msService.getMessage("NO_DATA")));
        }
        String physicalPath = request.getSession().getServletContext().getRealPath("/") + "excel/";
        String fileName = physicalPath + name + ".xlsx";
        File savefile = new File(physicalPath);
        if (!savefile.exists()) {
            savefile.mkdirs();
        }
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), list.get(0).getClass(), list);
        FileOutputStream fos = new FileOutputStream(fileName);
        workbook.write(fos);
        fos.close();
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition", "attachment;filename=" + name + ".xlsx");
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        OutputStream out = response.getOutputStream();
        File file = new File(fileName);
        InputStream in = new FileInputStream(file);
        int data = in.read();
        while (data != -1) {
            out.write(data);
            data = in.read();
        }
        in.close();
        out.close();
        file.delete();
        return MessageResult.success();
    }
}
