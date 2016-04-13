package moe.cdn.cweb.app.api;

import moe.cdn.cweb.CwebApi;
import moe.cdn.cweb.trust.CwebTrustNetworkApi;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

/**
 * @author davix
 */
public class CwebApiEndPoint {
    @Context
    ServletContext servletContext;

    protected CwebApi getCwebApi() {
        return (CwebApi) servletContext.getAttribute(CwebApi.class.getName());
    }

    protected CwebTrustNetworkApi getCwebTrustNetworkApi() {
        return (CwebTrustNetworkApi) servletContext.getAttribute(
                CwebTrustNetworkApi.class.getName());
    }
}
