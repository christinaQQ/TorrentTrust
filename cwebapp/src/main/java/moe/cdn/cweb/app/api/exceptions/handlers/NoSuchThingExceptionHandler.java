package moe.cdn.cweb.app.api.exceptions.handlers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import moe.cdn.cweb.app.api.exceptions.NoSuchThingException;

/**
 * @author davix
 */
@Provider
public class NoSuchThingExceptionHandler implements ExceptionMapper<NoSuchThingException> {
    @Override
    public Response toResponse(NoSuchThingException exception) {
        return Response.status(Response.Status.NOT_FOUND).entity(exception.getMessage()).build();
    }
}
