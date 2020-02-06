package com.garyodernichts.jpaexec.paexec;

public class FileInfo {
    String filenameOnly;
    int fileLastWrite_low;
    int fileLastWrite_high;
    int fileVersionMS;
    int fileVersionLS;

    public FileInfo(String filenameOnly,
                    int fileLastWrite_low,
                    int fileLastWrite_high,
                    int fileVersionMS,
                    int fileVersionLS) {
        this.filenameOnly = filenameOnly;
        this.fileLastWrite_low = fileLastWrite_low;
        this.fileLastWrite_high = fileLastWrite_high;
        this.fileVersionMS = fileVersionMS;
        this.fileVersionLS = fileVersionLS;
    }
}
