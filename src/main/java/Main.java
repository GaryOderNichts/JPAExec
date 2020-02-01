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
import com.rapid7.client.dcerpc.msvcctl.dto.ServiceHandle;
import com.rapid7.client.dcerpc.msvcctl.dto.ServiceManagerHandle;
import com.rapid7.client.dcerpc.msvcctl.dto.enums.ServiceError;
import com.rapid7.client.dcerpc.msvcctl.dto.enums.ServiceStartType;
import com.rapid7.client.dcerpc.msvcctl.dto.enums.ServiceType;
import com.rapid7.client.dcerpc.transport.RPCTransport;
import com.rapid7.client.dcerpc.transport.SMBTransportFactories;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;

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

            RPCTransport t = SMBTransportFactories.SVCCTL.getTransport(session);
            BetterServiceControlManagerService s = new BetterServiceControlManagerService(t);
            ServiceManagerHandle handle = s.openServiceManagerHandle();

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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
