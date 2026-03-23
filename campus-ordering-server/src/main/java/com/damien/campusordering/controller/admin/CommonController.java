package com.damien.campusordering.controller.admin;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.damien.campusordering.result.Result;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

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
    @PostMapping("/upload")
    public Result<String> upLoad(MultipartFile file) {
        log.info("上传文件: {}", file.getOriginalFilename());
        return Result.success("test");
    }
}
