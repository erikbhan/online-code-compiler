package compiler.backend;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class CompilerController {

    @PostMapping("/")
    public String processRequest(@RequestBody Code code) throws InterruptedException, IOException {
        try (FileWriter fw = new FileWriter("script.py")) {
            fw.write(code.getCode());
        } catch (IOException e) {
            e.printStackTrace();
            return "Error on backend";
        }

        String buildCommand = "docker build . -t py-compiler";
        String runCommand = "docker run py-compiler";

        Process build = Runtime.getRuntime().exec(buildCommand.split(" "));
        build.waitFor();

        Process run = Runtime.getRuntime().exec(runCommand.split(" "));
        String result = readProcess(run.getInputStream());

        run.waitFor();
        return result;
    }

    private String readProcess(InputStream stream) {
        //build buffered stream to string
        Scanner scanner = new Scanner(stream);
        StringBuilder stringBuilder = new StringBuilder();
        while (scanner.hasNext()) {
            stringBuilder.append(scanner.next());
        }
        scanner.close();
        return stringBuilder.toString();
    }
}


