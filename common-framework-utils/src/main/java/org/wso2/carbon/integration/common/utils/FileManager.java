/*
*Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.carbon.integration.common.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class FileManager {
    private static final Log log = LogFactory.getLog(FileManager.class);

    public static String readFile(String filePath) throws IOException {
        BufferedReader reader;
        StringBuilder stringBuilder;
        String line;
        String ls;
        log.debug("Path to file : " + filePath);
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8));
        stringBuilder = new StringBuilder();
        ls = System.getProperty("line.separator");
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        reader.close();
        return stringBuilder.toString();
    }

    public static String readFile(File file) throws IOException {
        BufferedReader reader;
        StringBuilder stringBuilder;
        String line;
        String ls;
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        stringBuilder = new StringBuilder();
        ls = System.getProperty("line.separator");
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        reader.close();
        return stringBuilder.toString();
    }

    public static void writeToFile(String filePath, String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8));
        try {
            writer.write(content);
            writer.newLine();
            writer.flush();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                //ignore
            }
        }
    }

    public static void copyFile(File sourceFile, String destinationPath) throws IOException {
        File destinationFile = new File(destinationPath);
        InputStreamReader in = null;
        OutputStreamWriter out = null;
        int c;
        try {
            in = new InputStreamReader(new FileInputStream(sourceFile), StandardCharsets.UTF_8);
            out = new OutputStreamWriter(new FileOutputStream(destinationFile), StandardCharsets.UTF_8);
            while ((c = in.read()) != -1) {
                out.write(c);
            }
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                //ignore
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                //ignore
            }
        }
    }

    public static File copyResourceToFileSystem(String sourcePath, String targetPath,
                                                String fileName)
            throws IOException {
        File file = new File(targetPath + File.separator + fileName);
        if (file.exists()) {
            FileUtils.deleteQuietly(file);
        }
        OutputStream os = null;
        InputStream is = null;

        try {
            FileUtils.touch(file);
            os = FileUtils.openOutputStream(file);
            is = new FileInputStream(sourcePath);

            if (is != null) {
                byte[] data = new byte[1024];
                int len;
                while ((len = is.read(data)) != -1) {
                    os.write(data, 0, len);
                }
            }
        } finally {
            if (os != null) {
                try {
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    log.warn("Unable to close steam");
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.warn("Unable to close steam");
                }
            }
        }
        return file;
    }

    public void copyJarFile(String sourceFileLocationWithFileName, String destinationDirectory)
            throws URISyntaxException, IOException  {
        File sourceFile = new File(getClass().getResource(sourceFileLocationWithFileName).toURI());
        File destinationFileDirectory = new File(destinationDirectory);
        JarOutputStream jarOutputStream = null;
        InputStream inputStream = null;

        try {
            JarFile jarFile = new JarFile(sourceFile);
            String fileName = jarFile.getName();
            String fileNameLastPart = fileName.substring(fileName.lastIndexOf(File.separator));
            File destinationFile = new File(destinationFileDirectory, fileNameLastPart);
            jarOutputStream = new JarOutputStream(new FileOutputStream(destinationFile));
            Enumeration<JarEntry> entries = jarFile.entries();


            while (entries.hasMoreElements()) {
                try {
                    JarEntry jarEntry = entries.nextElement();
                    inputStream = jarFile.getInputStream(jarEntry);
                    //jarOutputStream.putNextEntry(jarEntry);
                    //create a new jarEntry to avoid ZipException: invalid jarEntry compressed size
                    jarOutputStream.putNextEntry(new JarEntry(jarEntry.getName()));
                    byte[] buffer = new byte[4096];
                    int bytesRead = 0;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        jarOutputStream.write(buffer, 0, bytesRead);
                    }
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            log.warn("Fail to close jarOutStream");
                        }
                    }
                    if (jarOutputStream != null) {
                        try {
                            jarOutputStream.flush();
                            jarOutputStream.closeEntry();
                        } catch (IOException e) {
                            log.warn("Error while closing jar out stream");
                        }
                    }
                    if (jarFile != null) {
                        try {
                            jarFile.close();
                        } catch (IOException e) {
                            log.warn("Error while closing jar file");
                        }
                    }
                }
            }
        } finally {
            if (jarOutputStream != null) {
                try {
                    jarOutputStream.close();
                } catch (IOException e) {
                    log.warn("Fail to close jarOutStream");
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.warn("Error while closing input stream");
                }
            }
        }
    }

    public static void copyJarFile(File sourceFile, String destinationDirectory)
            throws IOException {
        File destinationFileDirectory = new File(destinationDirectory);
        JarFile jarFile = new JarFile(sourceFile);
        String fileName = jarFile.getName();
        String fileNameLastPart = fileName.substring(fileName.lastIndexOf(File.separator));
        File destinationFile = new File(destinationFileDirectory, fileNameLastPart);
        JarOutputStream jarOutputStream = null;
        try {
            jarOutputStream = new JarOutputStream(new FileOutputStream(destinationFile));
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                InputStream inputStream = jarFile.getInputStream(jarEntry);
                //jarOutputStream.putNextEntry(jarEntry);
                //create a new jarEntry to avoid ZipException: invalid jarEntry compressed size
                jarOutputStream.putNextEntry(new JarEntry(jarEntry.getName()));
                byte[] buffer = new byte[4096];
                int bytesRead = 0;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    jarOutputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
                jarOutputStream.flush();
                jarOutputStream.closeEntry();
            }
        } finally {
            if (jarOutputStream != null) {
                try {
                    jarOutputStream.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }
    }


    public static void copyJarFiles(List<File> jarListSelected, File destinationDirectory)
    {
        for(File jarFile : jarListSelected){
            try {
                if (!destinationDirectory.exists())
                    destinationDirectory.mkdir();
                FileUtils.copyFileToDirectory(jarFile,destinationDirectory);

            } catch (IOException ex) {
                log.warn("Error while copying jar files");

            }
        }
    }

    public static boolean deleteFile(String filePathWithFileName) {
        File jarFile = new File(filePathWithFileName);
        return !jarFile.isDirectory() && jarFile.delete();
    }
}

