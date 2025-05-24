package com.campus.ordering.controller;

import com.campus.ordering.common.R;
import com.campus.ordering.config.FileUploadConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@Slf4j
@Api(tags = "文件上传管理")
@RestController
@RequestMapping("/file")
public class FileUploadController {

    @ApiOperation("图片上传")
    @PostMapping("/upload")
    public R<String> uploadImage(@RequestParam("file") MultipartFile file) {
        log.info("图片上传：{}", file.getOriginalFilename());

        if (file.isEmpty()) {
            return R.error("上传文件不能为空");
        }

        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!isImageFile(extension)) {
            return R.error("只支持jpg、jpeg、png、gif格式的图片");
        }

        // 生成唯一文件名
        String fileName = UUID.randomUUID().toString() + extension;
        
        try {
            // 保存文件
            String uploadPath = FileUploadConfig.getUploadPath();
            File destFile = new File(uploadPath + fileName);
            file.transferTo(destFile);
            
            // 返回相对访问路径
            String relativePath = "/uploads/images/" + fileName;
            log.info("图片上传成功：{}", relativePath);
            
            return R.success(relativePath);
        } catch (IOException e) {
            log.error("图片上传失败", e);
            return R.error("图片上传失败");
        }
    }

    /**
     * 检查是否为图片文件
     */
    private boolean isImageFile(String extension) {
        String[] allowedExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp"};
        extension = extension.toLowerCase();
        for (String allowed : allowedExtensions) {
            if (allowed.equals(extension)) {
                return true;
            }
        }
        return false;
    }
} 