package moe.cdn.cweb.app;

import moe.cdn.cweb.CwebApi;
import moe.cdn.cweb.GlobalEnvironment;
import moe.cdn.cweb.trust.CwebTrustNetworkApi;
import moe.cdn.cweb.vote.CwebVoteApi;

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
        return (CwebTrustNetworkApi) servletContext
                .getAttribute(CwebTrustNetworkApi.class.getName());
    }

    protected CwebVoteApi getCwebVoteApi() {
        return (CwebVoteApi) servletContext.getAttribute(CwebVoteApi.class.getName());
    }

    protected GlobalEnvironment getCwebEnvironment() {
        return (GlobalEnvironment) servletContext.getAttribute(GlobalEnvironment.class.getName());
    }
}
