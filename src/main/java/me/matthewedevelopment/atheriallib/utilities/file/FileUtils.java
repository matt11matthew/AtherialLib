package me.matthewedevelopment.atheriallib.utilities.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileUtils {
    public FileUtils() {
    }

    public static String readFileToString(File file) {
        try {
            return org.apache.commons.io.FileUtils.readFileToString(file, Charset.forName("UTF-8"));
        } catch (IOException var2) {
            var2.printStackTrace();
            return null;
        }
    }
    public static void deleteDirectoryRecursively(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    deleteDirectoryRecursively(entry);
                }
            }
        }
        Files.delete(path);
    }

    public static void unzip(String zipFilePath, String destDir) {
        File dir = new File(destDir);
        // create output directory if it doesn't exist
        if (!dir.exists()) dir.mkdirs();

        // buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try (FileInputStream fis = new FileInputStream(zipFilePath);
             ZipInputStream zis = new ZipInputStream(fis)) {
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(destDir, fileName);
                // create directories for sub directories in zip
                new File(newFile.getParent()).mkdirs();
                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //    public  static void unzip(String zipFilePath, String destDir) {
//        File dir = new File(destDir);
//        // create output directory if it doesn't exist
//        if(!dir.exists()) dir.mkdirs();
//        FileInputStream fis;
//        //buffer for read and write data to file
//        byte[] buffer = new byte[1024];
//        try {
//            fis = new FileInputStream(zipFilePath);
//            ZipInputStream zis = new ZipInputStream(fis);
//            ZipEntry ze = zis.getNextEntry();
//            while(ze != null){
//                String fileName = ze.getName();
//                File newFile = new File(destDir + File.separator + fileName);
//                //create directories for sub directories in zip
//                new File(newFile.getParent()).mkdirs();
//                FileOutputStream fos = new FileOutputStream(newFile);
//                int len;
//                while ((len = zis.read(buffer)) > 0) {
//                    fos.write(buffer, 0, len);
//                }
//                fos.close();
//                //close this ZipEntry
//                zis.closeEntry();
//                ze = zis.getNextEntry();
//            }
//            //close last ZipEntry
//            zis.closeEntry();
//            zis.close();
//            fis.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    public  static void zipFiles(String[] srcFiles, String zipFilePath) {
        try {
            // create byte buffer
            byte[] buffer = new byte[1024];

            FileOutputStream fos = new FileOutputStream(zipFilePath);
            ZipOutputStream zos = new ZipOutputStream(fos);

            for (int i=0; i < srcFiles.length; i++) {
                File srcFile = new File(srcFiles[i]);
                FileInputStream fis = new FileInputStream(srcFile);
                // begin writing a new ZIP entry, positions the stream to the start of the entry data
                zos.putNextEntry(new ZipEntry(srcFile.getName()));

                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
                // close the InputStream
                fis.close();
            }
            // close the ZipOutputStream
            zos.close();
        } catch (IOException ioe) {
            System.out.println("Error creating zip file: " + ioe);
        }
    }
    public static boolean createFile(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
                return true;
            } catch (IOException var2) {
                var2.printStackTrace();
            }
        }

        return false;
    }

    public static boolean writeStringToFile(File file, String string) {
        try {
            org.apache.commons.io.FileUtils.writeStringToFile(file, string, Charset.forName("UTF-8"), false);
            return true;
        } catch (IOException var3) {
            var3.printStackTrace();
            return false;
        }
    }
}
