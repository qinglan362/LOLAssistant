package com.ywh.yxlmzs.utils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

@Service
public class GetTokenAndPort {
    public void getPortAndToken() {
        String batFilePath = Paths.get("run_as_admin.bat").toAbsolutePath().toString();

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", batFilePath);
            processBuilder.inheritIO(); // 这将允许看到命令行输出
            Process process = processBuilder.start();

            int exitCode = process.waitFor();
            System.out.println("Bat file executed, exit code: " + exitCode);

            try{
                // 获取项目根路径
                sleep(3000);
                String rootPath = System.getProperty("user.dir");
                System.out.println(rootPath);
                String inputFilePath = Paths.get(rootPath, "yxlmpeizhi.txt").toString();
                String outputFilePath = Paths.get(rootPath, "tokenAndPort.txt").toString();

                // 读取文件内容
                String content = readFile(inputFilePath);
                System.out.println("File content: " + content);
                // 提取 token 和 port
                String token = extractInfo(content, "--remoting-auth-token=([^\"\\s]+)");
                String port = extractInfo(content, "--app-port=(\\d+)");
                System.out.println("Token: " + token);
                // 保存提取的信息到新文件
                saveInfo(outputFilePath, token, port);

                System.out.println("信息已提取并保存到 " + outputFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void saveInfo(String outputFilePath, String token, String port) throws IOException {
        File file = new File(outputFilePath);

        // 如果文件存在，删除它
        if (file.exists()) {
            if (!file.delete()) {
                System.out.println("Unable to delete existing file: " + outputFilePath);
                return;
            }
        }

        // 创建新文件
        if (!file.createNewFile()) {
            System.out.println("Unable to create new file: " + outputFilePath);
            return;
        }

        // 写入新内容
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("Token: " + token);
            writer.println("Port: " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("File created and information saved to: " + outputFilePath);
    }

    private String extractInfo(String content, String s) {
        Pattern pattern = Pattern.compile(s);

        Matcher matcher = pattern.matcher(content);
        return matcher.find() ? matcher.group(1) : "Not found";
    }

    private String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "GBK"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }
        return content.toString();
    }

//        public void getPortAndToken() {
//            String batFilePath = Paths.get("run_as_admin.bat").toAbsolutePath().toString();
//
//            try {
//                ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", batFilePath);
//                processBuilder.inheritIO(); // 这将允许看到命令行输出
//                Process process = processBuilder.start();
//
//                int exitCode = process.waitFor();
//                System.out.println("Bat file executed, exit code: " + exitCode);
//
//                try{
//                    // 获取项目根路径
//                    sleep(3000);
//                    String rootPath = System.getProperty("user.dir");
//                    System.out.println(rootPath);
//                    String inputFilePath = Paths.get(rootPath, "yxlmpeizhi.txt").toString();
//                    String outputFilePath = Paths.get(rootPath, "tokenAndPort.txt").toString();
//
//                    // 读取文件内容
//                    String content = readFile(inputFilePath);
//                    System.out.println("File content: " + content);
//                    // 提取 token 和 port
//                    String token = extractInfo(content, "--remoting-auth-token=([^\"\\s]+)");
//                    String port = extractInfo(content, "--app-port=(\\d+)");
//                    System.out.println("Token: " + token);
//                    // 保存提取的信息到新文件
//                    saveInfo(outputFilePath, token, port);
//
//                    System.out.println("信息已提取并保存到 " + outputFilePath);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            } catch (IOException | InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        private String readFile(String filePath) throws IOException {
//            StringBuilder content = new StringBuilder();
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "GBK"))) {
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    content.append(line);
//                }
//            }
//            return content.toString();
//        }
//
//
//        private String extractInfo(String content, String regex) {
//            Pattern pattern = Pattern.compile(regex);
//
//            Matcher matcher = pattern.matcher(content);
//            return matcher.find() ? matcher.group(1) : "Not found";
//        }
//
//        private void saveInfo(String filePath, String token, String port) throws IOException {
//            File file = new File(filePath);
//
//            // 如果文件存在，删除它
//            if (file.exists()) {
//                if (!file.delete()) {
//                    System.out.println("Unable to delete existing file: " + filePath);
//                    return;
//                }
//            }
//
//            // 创建新文件
//            if (!file.createNewFile()) {
//                System.out.println("Unable to create new file: " + filePath);
//                return;
//            }
//
//            // 写入新内容
//            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
//                writer.println("Token: " + token);
//                writer.println("Port: " + port);
//            }
//
//            System.out.println("File created and information saved to: " + filePath);
//        }

}
