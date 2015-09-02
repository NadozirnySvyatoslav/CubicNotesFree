package org.nadozirny_sv.ua.cubic;

import java.io.File;

/**
 * Created by Nadozirny_SV on 26.08.2015.
 */
public  final class DestDir {
    public static String path;
    private static DestDir inst=new DestDir();
    private DestDir(){
    }
    public static  DestDir get(){
        return inst;
    }
}
