package client.dcerpc.msvcctl;

import client.dcerpc.msvcctl.messages.RCreateServiceWRequest;
import client.dcerpc.msvcctl.messages.RDeleteServiceWRequest;
import com.rapid7.client.dcerpc.msvcctl.ServiceControlManagerService;
import com.rapid7.client.dcerpc.msvcctl.dto.ServiceHandle;
import com.rapid7.client.dcerpc.msvcctl.dto.ServiceManagerHandle;
import com.rapid7.client.dcerpc.msvcctl.dto.enums.ServiceError;
import com.rapid7.client.dcerpc.msvcctl.dto.enums.ServiceStartType;
import com.rapid7.client.dcerpc.msvcctl.dto.enums.ServiceType;
import com.rapid7.client.dcerpc.transport.RPCTransport;

import java.io.IOException;

public class BetterServiceControlManagerService extends ServiceControlManagerService {
    public BetterServiceControlManagerService(RPCTransport transport) {
        super(transport);
    }

    public ServiceHandle createService(final ServiceManagerHandle serviceManagerHandle,
                                       final String serviceName,
                                       final String displayName,
                                       final ServiceType serviceType,
                                       final ServiceStartType serviceStartType,
                                       final ServiceError errorControl,
                                       final String binaryPathName,
                                       final String loadOrderGroup,
                                       final int tagId,
                                       final String[] dependencies,
                                       final String serviceStartName,
                                       final String password) throws IOException {
        final RCreateServiceWRequest request = new RCreateServiceWRequest(serviceManagerHandle.getBytes(),
                parseWCharNT(serviceName),
                parseWCharNT(displayName),
                FULL_ACCESS,
                serviceType.getValue(),
                serviceStartType.getValue(),
                errorControl.getValue(),
                parseWCharNT(binaryPathName),
                parseWCharNT(loadOrderGroup),
                tagId,
                dependencies,
                parseWCharNT(serviceStartName),
                password);
        return new ServiceHandle(callExpectSuccess(request, "RCreateServiceW").getHandle());
    }

    public void deleteService(final ServiceHandle serviceHandle) throws IOException {
        final RDeleteServiceWRequest request = new RDeleteServiceWRequest(serviceHandle.getBytes());
        callExpectSuccess(request, "RDeleteServiceW");
    }
}
