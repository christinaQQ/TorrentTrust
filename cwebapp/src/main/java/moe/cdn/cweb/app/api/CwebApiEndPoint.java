package moe.cdn.cweb.app.api;

import java.nio.file.Path;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import moe.cdn.cweb.CwebApi;
import moe.cdn.cweb.GlobalEnvironment;
import moe.cdn.cweb.IdentityEnvironment;
import moe.cdn.cweb.TrustApi;
import moe.cdn.cweb.TrustGenerator;
import moe.cdn.cweb.app.services.CwebApiService;
import moe.cdn.cweb.dht.ManagedPeer;
import moe.cdn.cweb.trust.CwebIdentityApi;
import moe.cdn.cweb.trust.CwebTrustNetworkApi;
import moe.cdn.cweb.vote.CwebVoteApi;

/**
 * @author davix
 */
public abstract class CwebApiEndPoint {
    @Context
    ServletContext servletContext;

    protected Path getStateFilePath() {
        return (Path) servletContext.getAttribute(CwebApiService.STATE_FILE_PATH_ATTRIBUTE);
    }

    protected ManagedPeer getDhtPeer() {
        return (ManagedPeer) servletContext.getAttribute(ManagedPeer.class.getName());
    }

    protected GlobalEnvironment getCwebEnvironment() {
        return (GlobalEnvironment) servletContext.getAttribute(GlobalEnvironment.class.getName());
    }

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

    protected IdentityEnvironment getCwebIdentities() {
        return (IdentityEnvironment) servletContext
                .getAttribute(IdentityEnvironment.class.getName());
    }

    protected CwebIdentityApi getCwebIdentityApi() {
        return (CwebIdentityApi) servletContext.getAttribute(CwebIdentityApi.class.getName());
    }

    protected TrustGenerator getCwebTrustGenerator() {
        return (TrustGenerator) servletContext.getAttribute(TrustGenerator.class.getName());
    }

    protected TrustApi getCwebTrustApi() {
        return (TrustApi) servletContext.getAttribute(TrustApi.class.getName());
    }
}
