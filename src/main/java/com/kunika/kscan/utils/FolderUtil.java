package com.kunika.kscan.utils;

import java.io.File;

public class FolderUtil {
    private FolderUtil()
    {

    }
    public static void createDefaultFolder(String dirpath)
    {
        File directory=new File(dirpath);
        if(!directory.exists())
        {
            directory.mkdir();
        }
    }
    public boolean checkIfFileExist(String filePath)
    {
        File file=new File(filePath);
        return file.exists();
    }
}
