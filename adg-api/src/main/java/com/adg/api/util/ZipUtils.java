package com.adg.api.util;

import lombok.SneakyThrows;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.12 02:29
 */
public class ZipUtils {

    @SneakyThrows
    public static void zipFolder(Path sourceFolderPath, Path zipPath) throws Exception {
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()));
        Files.walkFileTree(sourceFolderPath, new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                zos.putNextEntry(new ZipEntry(sourceFolderPath.relativize(file).toString()));
                Files.copy(file, zos);
                zos.closeEntry();
                return FileVisitResult.CONTINUE;
            }
        });
        zos.close();
    }

    public static List<File> unzip(File zipFile, String outputFolder) {

        String fileExtension = getFileExtension(zipFile);
        if (fileExtension.equalsIgnoreCase("gz")) {
            if (zipFile.getAbsoluteFile().getAbsolutePath().contains("tar.gz")) {
                return uncompressTarGzFile(zipFile, outputFolder);
            } else {
                return Arrays.asList(uncompressGZipFile(zipFile, outputFolder));
            }
        } else if (fileExtension.equalsIgnoreCase("zip")) {
            return uncompressZipFile(zipFile, outputFolder);
        } else {
            return Arrays.asList();
        }
    }

    public static String getFileExtension(File file) {
        if (file.isDirectory()) {
            return "";
        }
        String[] parts = file.getName().split("\\.");

        return parts[parts.length - 1];
    }

    /**
     * Get file name without extension
     * @param file
     * @return
     */
    public static String getFileName(File file) {
        String fileExtension = getFileExtension(file);
        return file.getName().replace("." + fileExtension, "");
    }

    @SneakyThrows
    public static File uncompressGZipFile(File zipFile, String outputFolder) {
        String fileName = getFileName(zipFile);

        File outputFile;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            outputFile = createFile(outputFolder, fileName);
            inputStream = new GZIPInputStream(new FileInputStream(zipFile.getAbsolutePath()));
            outputStream = new FileOutputStream(outputFile);
            IOUtils.copy(inputStream, outputStream);
        } finally {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }

        }
        return outputFile;
    }

    @SneakyThrows
    public static List<File> uncompressTarGzFile(File zipFile, String outputFolder){
        List<File> files = new ArrayList<>();
        TarArchiveInputStream tarArchiveInputStream = null;
        try {
            FileInputStream fis = new FileInputStream(zipFile);
            GZIPInputStream gzipInputStream = new GZIPInputStream(new BufferedInputStream(fis));
            tarArchiveInputStream = new TarArchiveInputStream(gzipInputStream);
            TarArchiveEntry tarEntry = null;
            while ((tarEntry = tarArchiveInputStream.getNextTarEntry()) != null) {
                if (tarEntry.isDirectory()) {
                    continue;
                }else {
                    String[] parts = tarEntry.getName().split("/");
                    String fileName = parts[parts.length - 1];
                    File outputFile = createFile(outputFolder, fileName);
                    IOUtils.copy(tarArchiveInputStream, new FileOutputStream(outputFile));
                    files.add(outputFile);
                }
            }
        } finally {
            if(tarArchiveInputStream != null) {
                try {
                    tarArchiveInputStream.close();
                } catch (IOException ignore) {
                }
            }
        }
        return files;
    }
    @SneakyThrows
    public static List<File> uncompressTarGzFile(FileInputStream fis, String outputFolder){
        List<File> files = new ArrayList<>();
        TarArchiveInputStream tarArchiveInputStream = null;
        try {
            GZIPInputStream gzipInputStream = new GZIPInputStream(new BufferedInputStream(fis));
            tarArchiveInputStream = new TarArchiveInputStream(gzipInputStream);
            TarArchiveEntry tarEntry = null;
            while ((tarEntry = tarArchiveInputStream.getNextTarEntry()) != null) {
                if (tarEntry.isDirectory()) {
                    continue;
                }else {
                    String[] parts = tarEntry.getName().split("/");
                    String fileName = parts[parts.length - 1];
                    File outputFile = createFile(outputFolder, fileName);
                    IOUtils.copy(tarArchiveInputStream, new FileOutputStream(outputFile));
                    files.add(outputFile);
                }
            }
        } finally {
            if(tarArchiveInputStream != null) {
                try {
                    tarArchiveInputStream.close();
                } catch (IOException ignore) {
                }
            }
        }
        return files;
    }

    @SneakyThrows
    public static List<File> uncompressZipFile(File zipFile, String outputFolder) {

        List<File> files = new ArrayList<>();
        try(
                ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile.getAbsolutePath()))
        ) {
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                if (zipEntry.isDirectory() ) {
                    zipEntry = zipInputStream.getNextEntry();
                    continue;
                }
                String[] parts = zipEntry.getName().split("/");

                String fileName = parts[parts.length - 1];
                if (fileName.startsWith(".")) {
                    zipEntry = zipInputStream.getNextEntry();
                    continue;
                }

                File file = createFile(outputFolder, fileName);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                IOUtils.copy(zipInputStream, fileOutputStream);
                fileOutputStream.close();
                files.add(file);
                zipEntry = zipInputStream.getNextEntry();
            }
        }

        return files;
    }

    @SneakyThrows
    public static List<File> uncompressZipFile(InputStream is, String outputFolder) {

        List<File> files = new ArrayList<>();
        try(
                ZipInputStream zipInputStream = new ZipInputStream(is)
        ) {
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                if (zipEntry.isDirectory() ) {
                    zipEntry = zipInputStream.getNextEntry();
                    continue;
                }
                String[] parts = zipEntry.getName().split("/");

                String fileName = parts[parts.length - 1];
                if (fileName.startsWith(".")) {
                    zipEntry = zipInputStream.getNextEntry();
                    continue;
                }

                File file = createFile(outputFolder, fileName);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                IOUtils.copy(zipInputStream, fileOutputStream);
                fileOutputStream.close();
                files.add(file);
                zipEntry = zipInputStream.getNextEntry();
            }
        }

        return files;
    }

    @SneakyThrows
    public static File createFile(String folder, String fileName) {
        String folderPath = folder.endsWith("/") ? folder : folder + "/";

        File targetFile = new File(folderPath + fileName);
        File parent = targetFile.getParentFile();

        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("Couldn't create dir: " + parent);
        }

        targetFile.createNewFile();

        return targetFile;
    }


}
