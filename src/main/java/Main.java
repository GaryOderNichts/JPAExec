import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.rapid7.client.dcerpc.mssrvs.ServerService;
import com.rapid7.client.dcerpc.mssrvs.dto.NetShareInfo0;
import com.rapid7.client.dcerpc.transport.RPCTransport;
import com.rapid7.client.dcerpc.transport.SMBTransportFactories;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        final SMBClient smbClient = new SMBClient();
        try (final Connection smbConnection = smbClient.connect("localhost")) {
            final AuthenticationContext smbAuthenticationContext = new AuthenticationContext("username", "password".toCharArray(), "");
            final Session session = smbConnection.authenticate(smbAuthenticationContext);

            final RPCTransport transport = SMBTransportFactories.SRVSVC.getTransport(session);
            final ServerService serverService = new ServerService(transport);
            final List<NetShareInfo0> shares = serverService.getShares0();
            for (final NetShareInfo0 share : shares) {
                System.out.println(share);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
