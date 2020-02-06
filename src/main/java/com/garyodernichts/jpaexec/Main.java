package com.garyodernichts.jpaexec;

import com.garyodernichts.jpaexec.client.dcerp.msvcctl.BetterServiceControlManagerService;
import com.garyodernichts.jpaexec.paexec.FileInfo;
import com.garyodernichts.jpaexec.paexec.Message;
import com.garyodernichts.jpaexec.paexec.MessageId;
import com.garyodernichts.jpaexec.paexec.Settings;
import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.mserref.NtStatus;
import com.hierynomus.msfscc.FileAttributes;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2CreateOptions;
import com.hierynomus.mssmb2.SMB2Header;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.mssmb2.messages.SMB2ReadRequest;
import com.hierynomus.mssmb2.messages.SMB2ReadResponse;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.common.SMBRuntimeException;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;
import com.hierynomus.smbj.share.PipeShare;
import com.rapid7.client.dcerpc.msvcctl.dto.ServiceHandle;
import com.rapid7.client.dcerpc.msvcctl.dto.ServiceManagerHandle;
import com.rapid7.client.dcerpc.msvcctl.dto.enums.ServiceError;
import com.rapid7.client.dcerpc.msvcctl.dto.enums.ServiceStartType;
import com.rapid7.client.dcerpc.msvcctl.dto.enums.ServiceType;
import com.rapid7.client.dcerpc.transport.RPCTransport;
import com.rapid7.client.dcerpc.transport.SMBTransportFactories;
import com.rapid7.helper.smbj.io.SMB2Exception;
import com.rapid7.helper.smbj.share.NamedPipe;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class Main {

    final static String USER = "user";
    final static String PASS = "pass";

    public static void main(String[] args) {
        SMBClient smbClient = new SMBClient();
        try (Connection smbConnection = smbClient.connect("localhost")) {
            AuthenticationContext smbAuthenticationContext = new AuthenticationContext(USER, PASS.toCharArray(), "");
            Session session = smbConnection.authenticate(smbAuthenticationContext);

            DiskShare share = (DiskShare) session.connectShare("ADMIN$");
            File f = share.openFile("paexec.exe",
                    new HashSet<>(Arrays.asList(AccessMask.GENERIC_ALL)),
                    new HashSet<>(Arrays.asList(FileAttributes.FILE_ATTRIBUTE_NORMAL)),
                    SMB2ShareAccess.ALL,
                    SMB2CreateDisposition.FILE_OVERWRITE_IF,
                    new HashSet<>(Arrays.asList(SMB2CreateOptions.FILE_DIRECTORY_FILE))
            );
            OutputStream os = f.getOutputStream();
            os.write(IOUtils.toByteArray(Main.class.getResourceAsStream("/paexec.exe")));
            os.close();
            f.close();

            RPCTransport t = SMBTransportFactories.SVCCTL.getTransport(session);
            BetterServiceControlManagerService s = new BetterServiceControlManagerService(t);
            ServiceManagerHandle handle = s.openServiceManagerHandle();

            //ServiceHandle testservice = s.openServiceHandle(handle, "paexectest");
            //if (testservice != null) {
            //    s.deleteService(testservice);
            //}

            ServiceHandle testservice = s.createService(handle,
                                        "paexectest",
                                        "paexectest",
                                        ServiceType.WIN32_OWN_PROCESS,
                                        ServiceStartType.DEMAND_START,
                                        ServiceError.NORMAL,
                                        "C:/Windows/paexec.exe -service",
                                        null,
                                        0,
                                        null,
                                        null,
                                        null);

            s.startService(testservice);

            PipeShare pipeShare = (PipeShare) session.connectShare("IPC$");
            NamedPipe pipe = new NamedPipe(session, pipeShare, "paexec.exe");

            FileInfo myFileInfo = new FileInfo("paexec.exe", 0, 1, 1, 1);

            List<FileInfo> myFileinfoList = new ArrayList<>();
            myFileinfoList.add(myFileInfo);

            Settings settings = new Settings(null, false, false, false, true, false, 1, true, false, false, PASS, USER, true, "C:/Windows/system32", false, 0x00000020, "cmd.exe", "-k echo Hello World", false, false, "C:/Temp", false, "C:/", "C:/", myFileinfoList, myFileinfoList, 20);
            byte[] settingsData = settings.serializeData();

            Message paexecmessage = new Message(MessageId.Settings, settingsData, 123456789);

            //System.out.print("Buffer: " + Arrays.toString(buffer) + "\n");

            pipe.write(paexecmessage.getDataToSend());

            System.out.println(Arrays.toString(pipe.read()));

            //System.out.print("Bitstring: " + ByteHelper.toBitString(buffer) + "\n");

            //System.out.print(Arrays.toString(pipe.read()));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
