package tr.otunctan.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileHelper {


    public static List<File> findFiles(File inFile) {

        if (!inFile.exists()) {
            throw new RuntimeException();
        }


        try {
            List<File> result = new ArrayList<>();


            if (!inFile.isDirectory()) {
                return result;
            }

            File[] files = inFile.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        List<File> foundFilesInDirectory = findFiles(file);
                        result.addAll(foundFilesInDirectory);
                    } else {
                        result.add(file);
                    }
                }
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException();
        }

    }

    public static List<File> findFiles(File inFile, String contain) {
        List<File> files = findFiles(inFile);
        return files.stream()//
                .filter(file -> file.getName().toLowerCase().contains(contain.toLowerCase()))//
                .collect(Collectors.toList());
    }


    public static List<File> findFiles(File inFile, List<String> fileExtensions) {

        List<File> files = findFiles(inFile);

        return files.stream()
                .filter(file -> fileExtensions.stream().anyMatch(fileExtension -> file.getName().endsWith("." + fileExtension)))
                .collect(Collectors.toList());

    }

    public static String getExtension(File file) {
        if (file == null) {
            throw new RuntimeException();
        }
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return name.substring(lastIndexOf + 1);
    }

    public static String getFileNameWithoutExtension(File file) {
        if (file == null) {
            throw new RuntimeException();
        }
        String extension = getExtension(file);

        String fileName = file.getName();
        int lastIndexOf = fileName.lastIndexOf(extension);

        if (lastIndexOf == -1) {
            return fileName;
        }

        return fileName.substring(0, lastIndexOf - 1);

    }


}