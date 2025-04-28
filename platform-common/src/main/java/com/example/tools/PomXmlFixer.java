package com.example.tools;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    public static void main(final String[] args) {
        System.out.println("开始修复POM文件中的XML标签问题...");

        for (String pomFile : POM_FILES) {
            try {
                Path path = Paths.get(pomFile);
                if (!Files.exists(path)) {
                    System.out.println("文件不存在: " + pomFile);
                    continue;
                }

                System.out.println("处理文件: " + pomFile);

                // 读取文件内容，修复默认编码问题，明确指定UTF-8编码
                List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
                List<String> fixedLines = lines.stream()
                        .map(PomXmlFixer::fixLine)
                        .collect(Collectors.toList());

                // 写回文件，明确指定UTF-8编码
                Files.write(path, fixedLines, StandardCharsets.UTF_8);

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
    private static String fixLine(final String line) {
        // 将<n>替换为<n>，将</n>替换为</n>
        return line.replaceAll("<n>", "<n>").replaceAll("</n>", "</n>");
    }

    /**
     * 递归查找指定目录下的所有POM文件
     *
     * @param directory 要搜索的目录
     * @return 所有POM文件的路径列表
     */
    private static List<Path> findPomFiles(final Path directory) throws IOException {
        try (Stream<Path> stream = Files.walk(directory)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().equals("pom.xml"))
                    .collect(Collectors.toList());
        }
    }
}