package paexec;

import com.sun.istack.internal.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Settings {
    int SERIALIZE_VER = 1;
    List<Short> allowedProcessors;
    boolean bCopyFiles;
    boolean bForceCopy;
    boolean bCopyIfNewerOrHigherVer;
    boolean bDontWaitForTerminate;
    boolean bDontLoadProfile;
    int sessionToInteractWith;
    boolean bInteractive;
    boolean bRunElevated;
    boolean bRunLimited;
    String password;
    String user;
    boolean bUseSystemAccount;
    String workingDir;
    boolean bShowUIOnWinLogon;
    int priority;
    String app;
    String appArgs;
    boolean bDisableFileRedirection;
    boolean bODS;
    String remoteLogPath;
    boolean bNoDelete;
    String srcDir;
    String destDir;
    List<FileInfo> srcFileInfos;
    List<FileInfo> destFileInfos;
    int timeoutSeconds;

    public Settings(@Nullable List<Short> allowedProcessors,
                    boolean bCopyFiles,
                    boolean bForceCopy,
                    boolean bCopyIfNewerOrHigherVer,
                    boolean bDontWaitForTerminate,
                    boolean bDontLoadProfile,
                    int sessionToInteractWith,
                    boolean bInteractive,
                    boolean bRunElevated,
                    boolean bRunLimited,
                    String password,
                    String user,
                    boolean bUseSystemAccount,
                    String workingDir,
                    boolean bShowUIOnWinLogon,
                    int priority,
                    String app,
                    String appArgs,
                    boolean bDisableFileRedirection,
                    boolean bODS,
                    String remoteLogPath,
                    boolean bNoDelete,
                    String srcDir,
                    String destDir,
                    List<FileInfo> srcFileInfos,
                    List<FileInfo> destFileInfos,
                    int timeoutSeconds) {
        this.allowedProcessors = allowedProcessors;
        this.bCopyFiles = bCopyFiles;
        this.bForceCopy = bForceCopy;
        this.bCopyIfNewerOrHigherVer = bCopyIfNewerOrHigherVer;
        this.bDontWaitForTerminate = bDontWaitForTerminate;
        this.bDontLoadProfile = bDontLoadProfile;
        this.sessionToInteractWith = sessionToInteractWith;
        this.bInteractive = bInteractive;
        this.bRunElevated = bRunElevated;
        this.bRunLimited = bRunLimited;
        this.password = password;
        this.user = user;
        this.bUseSystemAccount = bUseSystemAccount;
        this.workingDir = workingDir;
        this.bShowUIOnWinLogon = bShowUIOnWinLogon;
        this.priority = priority;
        this.app = app;
        this.appArgs = appArgs;
        this.bDisableFileRedirection = bDisableFileRedirection;
        this.bODS = bODS;
        this.remoteLogPath = remoteLogPath;
        this.bNoDelete = bNoDelete;
        this.srcDir = srcDir;
        this.destDir = destDir;
        this.srcFileInfos = srcFileInfos;
        this.destFileInfos = destFileInfos;
        this.timeoutSeconds = timeoutSeconds;
    }

    public byte[] serializeData() throws IOException
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);

        out.writeInt(SERIALIZE_VER);
        boolean allowedProcessorsNull = allowedProcessors == null;
        out.writeInt(allowedProcessorsNull ? 0 : allowedProcessors.size());
        if (!allowedProcessorsNull) {
            for (Short item : allowedProcessors) {
                out.writeInt((int) item);
            }
        }
        out.writeBoolean(bCopyFiles);
        out.writeBoolean(bForceCopy);
        out.writeBoolean(bCopyIfNewerOrHigherVer);
        out.writeBoolean(bDontWaitForTerminate);
        out.writeBoolean(bDontLoadProfile);
        out.writeInt(sessionToInteractWith);
        out.writeBoolean(bInteractive);
        out.writeBoolean(bRunElevated);
        out.writeBoolean(bRunLimited);
        out.writeInt(password.length());
        out.write(password.getBytes(StandardCharsets.UTF_16LE));
        out.writeInt(user.length());
        out.write(user.getBytes(StandardCharsets.UTF_16LE));
        out.writeBoolean(bUseSystemAccount);
        out.writeInt(workingDir.length());
        out.write(workingDir.getBytes(StandardCharsets.UTF_16LE));
        out.writeBoolean(bShowUIOnWinLogon);
        out.writeInt(priority);
        out.writeInt(app.length());
        out.write(app.getBytes(StandardCharsets.UTF_16LE));
        out.writeInt(appArgs.length());
        out.write(appArgs.getBytes(StandardCharsets.UTF_16LE));
        out.writeBoolean(bDisableFileRedirection);
        out.writeBoolean(bODS);
        out.writeInt(remoteLogPath.length());
        out.write(remoteLogPath.getBytes(StandardCharsets.UTF_16LE));
        out.writeBoolean(bNoDelete);
        out.writeInt(srcDir.length());
        out.write(srcDir.getBytes(StandardCharsets.UTF_16LE));
        out.writeInt(destDir.length());
        out.write(destDir.getBytes(StandardCharsets.UTF_16LE));
        out.writeInt(srcFileInfos.size());
        for(FileInfo fileInfo : srcFileInfos)
        {
            out.write(fileInfo.filenameOnly.getBytes(StandardCharsets.UTF_16LE));
            out.writeInt(fileInfo.fileLastWrite_low);
            out.writeInt(fileInfo.fileLastWrite_high);
            out.writeInt(fileInfo.fileVersionLS);
            out.writeInt(fileInfo.fileVersionMS);
        }
        out.writeInt(destFileInfos.size());
        for(FileInfo fileInfo : destFileInfos)
        {
            out.write(fileInfo.filenameOnly.getBytes(StandardCharsets.UTF_16LE));
            out.writeInt(fileInfo.fileLastWrite_low);
            out.writeInt(fileInfo.fileLastWrite_high);
            out.writeInt(fileInfo.fileVersionLS);
            out.writeInt(fileInfo.fileVersionMS);
        }
        out.writeInt(timeoutSeconds);

        byte[] data = stream.toByteArray();
        stream.close();

        return data;
    }
}
