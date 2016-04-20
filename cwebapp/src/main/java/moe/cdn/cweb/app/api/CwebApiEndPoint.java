package moe.cdn.cweb.app.api;

import moe.cdn.cweb.CwebApi;
import moe.cdn.cweb.GlobalEnvironment;
import moe.cdn.cweb.app.services.CwebApiService;
import moe.cdn.cweb.trust.CwebTrustNetworkApi;
import moe.cdn.cweb.vote.CwebVoteApi;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import java.nio.file.Path;

/**
 * @author davix
 */
public abstract class CwebApiEndPoint {
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

    protected Path getStateFilePath() {
        return (Path) servletContext.getAttribute(CwebApiService.STATE_FILE_PATH_ATTRIBUTE);
    }
}
