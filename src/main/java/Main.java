import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.rapid7.client.dcerpc.mssrvs.ServerService;
import com.rapid7.client.dcerpc.mssrvs.dto.*;
import com.rapid7.client.dcerpc.msvcctl.ServiceControlManagerService;
import com.rapid7.client.dcerpc.msvcctl.dto.ServiceHandle;
import com.rapid7.client.dcerpc.msvcctl.dto.ServiceManagerHandle;
import com.rapid7.client.dcerpc.transport.RPCTransport;
import com.rapid7.client.dcerpc.transport.SMBTransportFactories;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        SMBClient smbClient = new SMBClient();
        try (Connection smbConnection = smbClient.connect("localhost")) {
            AuthenticationContext smbAuthenticationContext = new AuthenticationContext("user", "pass".toCharArray(), "");
            Session session = smbConnection.authenticate(smbAuthenticationContext);

            RPCTransport t = SMBTransportFactories.SVCCTL.getTransport(session);
            ServiceControlManagerService s = new ServiceControlManagerService(t);
            ServiceManagerHandle handle = s.openServiceManagerHandle();
            System.out.println(handle);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
