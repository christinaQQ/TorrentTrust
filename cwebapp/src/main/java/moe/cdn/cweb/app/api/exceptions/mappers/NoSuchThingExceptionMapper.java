package moe.cdn.cweb.app.api.exceptions.mappers;

import moe.cdn.cweb.app.api.exceptions.NoSuchThingException;

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
