package moe.cdn.cweb.app.api.exceptions.handlers;

import moe.cdn.cweb.app.api.exceptions.ConflictException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ConflictExceptionHandler implements ExceptionMapper<ConflictException> {
    @Override
    public Response toResponse(ConflictException exception) {
        return Response.status(Response.Status.CONFLICT).entity(exception.getMessage()).build();
    }
}

