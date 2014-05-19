package com.lpedrosa.resources;

import com.lpedrosa.core.SimpleObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Date;

// TODO delete me, I exist only for testing
@Path("/hello-world")
@Produces(MediaType.APPLICATION_JSON)
public class SimpleResource {

    @GET
    public String helloWorld() {
        return "Hello World";
    }

    @GET
    @Path("/object")
    public SimpleObject simpleObject() {
        final SimpleObject obj = new SimpleObject("date-time", new Date().getTime());
        return obj;
    }
}
