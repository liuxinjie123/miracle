package com.dream.common.dto.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class LocalStorage implements Storage {

    @Value("${filePath}")
    private String localPath;

    /**
     * 举例:
     * localpath =  /data
     * bucket = meeting
     * key = d03d34502975b15619fdb29bb40397c7.jpg
     * 文件将会被存储在/data/meeting/d03d34502975b15619fdb29bb40397c7.jpg
     *
     * @param bucket
     * @param key
     * @param input
     * @param length
     * @param contentType
     * @throws StorageException
     */
    @Override
    public void save(String bucket, String key, InputStream input, int length, String contentType) throws StorageException {
        Path path = Paths.get(localPath, bucket, key);
        File file = path.toFile();
        FileOutputStream fos = null;

        File fpath = new File(file.getParent());
        if(!fpath.exists()){
            fpath.mkdirs();
        }

        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new StorageException(e);
        }
        byte[] buffer = new byte[8096];
        while (true) {
            try {
                int cur = input.read(buffer, 0, 8096);
                if (cur > 0) {
                    fos.write(buffer, 0, cur);
                } else {
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public InputStream download(String bucket, String key) throws StorageException {
        Path path = Paths.get(localPath, bucket, key);
        File file = path.toFile();
        makeDirectory(file);
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new StorageException(e);
        }
    }

    private void makeDirectory(File file) {
        if (!file.getParentFile().exists()) {
            makeDirectory(file.getParentFile());
        }
        file.mkdir();
    }

    public String getServerFileRootPath() {
        return localPath;
    }

}

