package org.unina;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App {
    static final String[] args = new String[5];

    public static void main(String[] unused){
        args[0] = ".html";
        args[1] = "angularjs";
        args[2] = "/home/simon/Documents/Projects/angular-spotify";

        System.out.println("Start");
        Path start = Paths.get(args[2]);
        String batchFileName;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            batchFileName = "hookInjection.bat";
        } else {
            batchFileName = "hookInjection.sh";
        }

        String currentWorkingDir = System.getProperty("user.dir");
        Path batchFilePath = Paths.get(currentWorkingDir, batchFileName);

        try {
            Stream<Path> streamPath = Files.walk(start, Integer.MAX_VALUE);
            List<String> filePathList = streamPath
                    .map(String::valueOf)
                    .sorted()
                    .toList();
            List<String> feFilePathList = filePathList
                    .stream()
                    .filter(fileName -> fileName.contains(args[0]))
                    .toList();

            System.out.println("Files to edit found:");
            System.out.println(feFilePathList);
            List<String> commmandList = createHookInjectionContent(feFilePathList, args[1]);
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                commmandList.add(0,"cd .\\test-hooks\\test-guard");
            } else {
                commmandList.add(0,"#!/bin/bash");
                commmandList.add(1,"cd ./test-hooks/test-guard");
            }
            System.out.println("Commands to inject into hookInjection script file:");
            System.out.println(commmandList);

            FileWriter myWriter = new FileWriter(batchFilePath.toFile());
            commmandList.forEach(feFile -> {
                try {
                    myWriter.write(feFile+"\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            System.out.println("Successfully wrote to the file.");
            myWriter.close();

            try{
                ProcessBuilder pb = new ProcessBuilder();
                pb.command("bash", batchFilePath.toString());
                pb.redirectErrorStream(true);
                Process p = pb.start();
                int exitCode = p.waitFor();
                System.out.println("Script executed with exit code: " + exitCode);
            }catch(Exception e){
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> createHookInjectionContent(List<String> fileFeList, String grammarType){
        List<String> commandList = new ArrayList<>();
        fileFeList.forEach(
            content -> {
                content = "node main.js inject-hooks "
                        + "\"" + content + "\""
                        + " --grammar "
                        + grammarType;
                commandList.add(content);
            }
        );
        return commandList;
    }

}
