import client.dcerpc.msvcctl.BetterServiceControlManagerService;
import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.FileAttributes;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2CreateOptions;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
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
import com.rapid7.helper.smbj.share.NamedPipe;
import javafx.beans.binding.ObjectExpression;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import paexec.FileInfo;
import paexec.Settings;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        SMBClient smbClient = new SMBClient();
        try (Connection smbConnection = smbClient.connect("localhost")) {
            AuthenticationContext smbAuthenticationContext = new AuthenticationContext("user", "pass".toCharArray(), "");
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
            os.write(IOUtils.toByteArray(Main.class.getResourceAsStream("paexec.exe")));
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

            List<Short> processors = new ArrayList<>();
            processors.add((short) 1);
            processors.add((short) 2);

            FileInfo myFileInfo = new FileInfo("paexec.exe", 0, 1, 1, 1);

            List<FileInfo> myFileinfoList = new ArrayList<>();
            myFileinfoList.add(myFileInfo);

            Settings settings = new Settings(null, false, false, false, true, false, 1, true, false, false, "pass", "user", true, "C:/Windows/system32/", false, 0x00000020, "cmd.exe", "-k echo Hello World", false, false, "C:/Temp/", false, "C:/", "C:/", myFileinfoList, myFileinfoList, 20);
            byte[] settingsData = settings.serializeData();

            // 2 represents a 2 byte WORD, each 4 represents a 4 byte DWORD
            int totallenght = settingsData.length + 2 + 4 + 4 + 4;
            byte[] buffer = new byte[totallenght];

            int constructionIndex = 0;

            short msg_id = 1;
            byte[] msg_id_bytes = ByteBuffer.allocate(2).putShort(msg_id).array();

            System.arraycopy(msg_id_bytes, 0, buffer, constructionIndex, msg_id_bytes.length);
            constructionIndex += msg_id_bytes.length;

            Random r = new Random();
            int xorVal = r.nextInt(2147483647);
            byte[] xorVal_bytes = ByteBuffer.allocate(4).putInt(xorVal).array();

            System.arraycopy(xorVal_bytes, 0, buffer, constructionIndex, xorVal_bytes.length);
            constructionIndex += xorVal_bytes.length;
            int XORStart = constructionIndex;

            int unID = 123456789;
            byte[] unID_bytes = ByteBuffer.allocate(4).putInt(unID).array();

            System.arraycopy(unID_bytes, 0, buffer, constructionIndex, unID_bytes.length);
            constructionIndex += unID_bytes.length;

            int len = settingsData.length;
            byte[] len_bytes = ByteBuffer.allocate(4).putInt(len).array();

            System.arraycopy(len_bytes, 0, buffer, constructionIndex, len_bytes.length);
            constructionIndex += len_bytes.length;

            System.arraycopy(settingsData, 0, buffer, constructionIndex, settingsData.length);
            constructionIndex += settingsData.length;

            int dataLen = constructionIndex - XORStart;
            for (int i = 0; i < dataLen - 3; i++)
            {
                buffer[XORStart + i] ^= xorVal;
                xorVal += 3;
            }
            
            pipe.write(buffer);
            //pipe.close();

            while (true)
            {
                System.out.print(Arrays.toString(pipe.read()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
