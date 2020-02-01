import client.dcerpc.msvcctl.BetterServiceControlManagerService;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.rapid7.client.dcerpc.msvcctl.dto.ServiceHandle;
import com.rapid7.client.dcerpc.msvcctl.dto.ServiceManagerHandle;
import com.rapid7.client.dcerpc.msvcctl.dto.enums.ServiceError;
import com.rapid7.client.dcerpc.msvcctl.dto.enums.ServiceStartType;
import com.rapid7.client.dcerpc.msvcctl.dto.enums.ServiceType;
import com.rapid7.client.dcerpc.transport.RPCTransport;
import com.rapid7.client.dcerpc.transport.SMBTransportFactories;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        SMBClient smbClient = new SMBClient();
        try (Connection smbConnection = smbClient.connect("localhost")) {
            AuthenticationContext smbAuthenticationContext = new AuthenticationContext("user", "pass".toCharArray(), "");
            Session session = smbConnection.authenticate(smbAuthenticationContext);

            RPCTransport t = SMBTransportFactories.SVCCTL.getTransport(session);
            BetterServiceControlManagerService s = new BetterServiceControlManagerService(t);
            ServiceManagerHandle handle = s.openServiceManagerHandle();

            ServiceHandle testservice = s.createService(handle,
                                        "testtest",
                                        "testtest",
                                        ServiceType.WIN32_OWN_PROCESS,
                                        ServiceStartType.DEMAND_START,
                                        ServiceError.NORMAL,
                                        "C:/test.exe",
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
