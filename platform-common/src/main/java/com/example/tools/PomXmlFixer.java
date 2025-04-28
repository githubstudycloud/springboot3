package com.example.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * POM文件XML标签修复工具
 * 用于将所有POM文件中的&lt;n&gt;标签替换为&lt;name&gt;标签
 */
public class PomXmlFixer {

    private static final String[] POM_FILES = {
            "pom.xml",
            "platform-dependencies/pom.xml",
            "platform-common/pom.xml",
            "platform-framework/pom.xml"
    };

    public static void main(String[] args) {
        System.out.println("开始修复POM文件中的XML标签问题...");
        
        for (String pomFile : POM_FILES) {
            try {
                Path path = Paths.get(pomFile);
                if (!Files.exists(path)) {
                    System.out.println("文件不存在: " + pomFile);
                    continue;
                }
                
                System.out.println("处理文件: " + pomFile);
                
                // 读取文件内容
                List<String> lines = Files.readAllLines(path);
                List<String> fixedLines = lines.stream()
                        .map(PomXmlFixer::fixLine)
                        .collect(Collectors.toList());
                
                // 写回文件
                Files.write(path, fixedLines);
                
                System.out.println("已修复文件: " + pomFile);
            } catch (IOException e) {
                System.err.println("处理文件时发生错误: " + pomFile);
                e.printStackTrace();
            }
        }
        
        System.out.println("所有POM文件修复完成！");
    }
    
    /**
     * 修复单行内容中的标签
     * 
     * @param line 原始行内容
     * @return 修复后的行内容
     */
    private static String fixLine(String line) {
        // 将<n>替换为<name>，将</n>替换为</name>
        return line.replaceAll("<n>", "<name>").replaceAll("</n>", "</name>");
    }
    
    /**
     * 递归查找指定目录下的所有POM文件
     *
     * @param directory 要搜索的目录
     * @return 所有POM文件的路径列表
     */
    private static List<Path> findPomFiles(Path directory) throws IOException {
        try (Stream<Path> stream = Files.walk(directory)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().equals("pom.xml"))
                    .collect(Collectors.toList());
        }
    }
}
