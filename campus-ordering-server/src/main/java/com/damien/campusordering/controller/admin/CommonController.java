package com.damien.campusordering.controller.admin;

import com.damien.campusordering.constant.MessageConstant;
import com.damien.campusordering.result.Result;
import com.damien.campusordering.utils.AliOssUtil;
import io.jsonwebtoken.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {
    /**
     * 上传文件
     */
    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    public Result<String> upLoad(MultipartFile file) throws IOException {
        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        // 获取文件后缀
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 生成文件名
        String objectName = UUID.randomUUID() + extension;
        try {
            String url = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(url);
        } catch (java.io.IOException e) {
            log.error("文件上传失败,:{}", e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }



}
