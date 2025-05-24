package com.campus.ordering.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.MultipartConfigElement;
import java.io.File;

/**
 * 文件上传配置
 */
@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

    /**
     * 上传文件保存路径（相对路径）
     */
    public static final String UPLOAD_PATH = "uploads/images/";

    /**
     * 获取上传文件的完整路径
     */
    public static String getUploadPath() {
        // 获取当前项目根目录，然后加上back路径
        String userDir = System.getProperty("user.dir");
        
        // 如果当前目录已经是back目录，直接使用
        // 如果是项目根目录，则加上back路径
        String backDir;
        if (userDir.endsWith("back")) {
            backDir = userDir;
        } else {
            backDir = userDir + File.separator + "back";
        }
        
        String fullPath = backDir + File.separator + UPLOAD_PATH;
        
        // 创建目录（如果不存在）
        File directory = new File(fullPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        return fullPath;
    }

    /**
     * 配置静态资源访问
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取back目录下的uploads目录路径
        String userDir = System.getProperty("user.dir");
        String backDir;
        if (userDir.endsWith("back")) {
            backDir = userDir;
        } else {
            backDir = userDir + File.separator + "back";
        }
        
        String uploadsPath = backDir + File.separator + "uploads" + File.separator;
        
        // 确保目录存在
        File directory = new File(uploadsPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        System.out.println("配置静态资源路径: " + uploadsPath);
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadsPath);
    }

    /**
     * 配置上传文件大小限制
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 单个文件最大5MB
        factory.setMaxFileSize(DataSize.ofMegabytes(5));
        // 总上传最大10MB
        factory.setMaxRequestSize(DataSize.ofMegabytes(10));
        return factory.createMultipartConfig();
    }
} 