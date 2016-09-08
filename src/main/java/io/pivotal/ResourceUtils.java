package io.pivotal;

import org.cloudfoundry.client.v2.servicebindings.ServiceBindingResource;
import org.cloudfoundry.client.v2.serviceinstances.GetServiceInstanceResponse;

final class ResourceUtils {

    private ResourceUtils() {
    }

    static String getServiceInstanceId(ServiceBindingResource serviceBinding) {
        return org.cloudfoundry.util.ResourceUtils.getEntity(serviceBinding).getServiceInstanceId();
    }

    static String getServiceInstanceName(GetServiceInstanceResponse response) {
        return org.cloudfoundry.util.ResourceUtils.getEntity(response).getName();
    }
}
