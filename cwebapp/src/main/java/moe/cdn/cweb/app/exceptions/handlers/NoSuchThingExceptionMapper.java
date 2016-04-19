package moe.cdn.cweb.app.exceptions.handlers;

import moe.cdn.cweb.app.exceptions.NoSuchThingException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author davix
 */
@Provider
public class NoSuchThingExceptionMapper implements ExceptionMapper<NoSuchThingException> {
    @Override
    public Response toResponse(NoSuchThingException exception) {
        return Response.status(Response.Status.NOT_FOUND).entity(exception.getMessage()).build();
    }
}
