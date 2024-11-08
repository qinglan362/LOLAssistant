package com.ywh.yxlmzs.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static java.lang.Thread.sleep;

@Service
public class GetTokenAndPort {

    public static Pattern appPortPattern = Pattern.compile("--app-port=(\\d+)");
    public static Pattern tokenPattern = Pattern.compile("--remoting-auth-token=([\\w-]+)");

    private final GetGlobalTokenAndPort getGlobalTokenAndPort;

    @Autowired
    public GetTokenAndPort(GetGlobalTokenAndPort getGlobalTokenAndPort) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
    }

    public void getPortAndToken() throws IOException {

        StringBuilder output = new StringBuilder();

        ClassPathResource classPathResource = new ClassPathResource("run_as_admin.bat");
        Path tempBatFile = Files.createTempFile("run_as_admin", ".bat");
        Files.copy(classPathResource.getInputStream(), tempBatFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", tempBatFile.toString());
            processBuilder.inheritIO(); // 这将允许看到命令行输出
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                output.append(reader.lines().collect(Collectors.joining("\n")));
            }

            System.out.println("Bat file executed, output: " + output);
            int exitCode = process.waitFor();
            System.out.println("Bat file executed, exit code: " + exitCode);

            try {
                // 等待bat文件执行完毕并生成配置文件
                sleep(3000);

                // 获取生成的配置文件路径
                Path outputFilePath = tempBatFile.getParent().resolve("yxlmpeizhi.txt");

                // 读取文件内容
                String content = readFile(outputFilePath.toString());

                // 提取 token 和 port
                String token = extractInfo(content, "--remoting-auth-token=([^\"\\s]+)");
                String port = extractInfo(content, "--app-port=(\\d+)");
                String regin=extractInfo(content,"--rso_platform_id=([\\w-]+)");

                getGlobalTokenAndPort.setPort(port);
                getGlobalTokenAndPort.setToken(token);
                getGlobalTokenAndPort.setRegin(regin);
                System.out.println(regin);
                System.out.println("port: " + port + ", token: " + token);

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 删除临时文件
            Files.deleteIfExists(tempBatFile);
        }
    }

    private String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    private String extractInfo(String content, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        return matcher.find() ? matcher.group(1) : "Not found";
    }
}
