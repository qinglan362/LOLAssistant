package com.ywh.yxlmzs.utils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

@Service
public class GetTokenAndPort {

        public void getPortAndToken() throws IOException {
                    //执行static/run_as_admin.bat
            ClassPathResource resource = new ClassPathResource("static/run_as_admin.bat");
            File batFile = resource.getFile();
            Process process = Runtime.getRuntime().exec("cmd /c start " + batFile.getAbsolutePath());
            try {
                sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            InputStream inputStream2 = getClass().getClassLoader().getResourceAsStream("classpath:yxlmpeizhi.txt");
                    ClassPathResource resource2 = new ClassPathResource("static/run_as_admin.bat");
                    // 提取 token 和 port
                    String token = extractInfo("11", "--remoting-auth-token=([^\"\\s]+)");
                    String port = extractInfo("content", "--app-port=(\\d+)");
                    System.out.println("Token: " + token);
                    // 保存提取的信息到新文件
                    saveInfo( token, port);

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


        private String extractInfo(String content, String regex) {
            Pattern pattern = Pattern.compile(regex);

            Matcher matcher = pattern.matcher(content);
            return matcher.find() ? matcher.group(1) : "Not found";
        }

        private void saveInfo(String token, String port) throws IOException {

               ClassPathResource resource = new ClassPathResource("static/tokenAndPort.json");
               Path tempFile = Files.createTempFile("tokenAndPort", ".json");
                Files.copy(resource.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
                File file = new File(tempFile.toString());
                try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                    writer.println("{\"Token\":\"" + token + "\",\"Port\":\"" + port + "\"}");
                }
//            if (file1.exists()) {
//                if (!file1.delete()) {
//                    System.out.println("Unable to delete existing file: " + file1.getAbsolutePath());
//                    return;
//                }
//            }
//            if (!file1.createNewFile()) {
//                System.out.println("Unable to create new file: " + file1.getAbsolutePath());
//                return;
//            }
//            try (PrintWriter writer = new PrintWriter(new FileWriter(file1))) {
//                writer.println("{\"Token\":\"" + token + "\",\"Port\":\"" + port + "\"}");
//            }
        }

}
