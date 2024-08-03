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
//        String cmd = "WMIC PROCESS WHERE \"name='LeagueClientUx.exe'\" GET commandline";
//        BufferedReader reader1 = null;
//        Process process1 = null;
//       String token1="";
//       String port1="";
//        try {
//            process1 = Runtime.getRuntime().exec(cmd);
//            // windows 命令必须gbk编码
//            reader1 = new BufferedReader(new InputStreamReader(process1.getInputStream(), "gb2312"));
//            String line1;
//
//            while ((line1 = reader1.readLine()) != null) {
//                System.out.println(line1);
//                Matcher appPortMatcher = appPortPattern.matcher(line1);
//                Matcher tokenPatternMatcher = tokenPattern.matcher(line1);
//                if (tokenPatternMatcher.find()) {
//                    token1=(tokenPatternMatcher.group(1));
//                }
//                if (appPortMatcher.find()) {
//                    port1=(appPortMatcher.group(1));
//                }
//
//            }
//        }finally {
//            if (reader1 != null) {
//                try {
//                    reader1.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            if (process1 != null) {
//                process1.getErrorStream().close();
//                process1.getOutputStream().close();
//            }
//        }
//        System.out.println("port:"+port1+"token:"+token1);
        //

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

                getGlobalTokenAndPort.setPort(port);
                getGlobalTokenAndPort.setToken(token);

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
