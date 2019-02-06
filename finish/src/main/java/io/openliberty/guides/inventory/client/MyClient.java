package io.openliberty.guides.inventory.client;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;

@Path("stock")
public interface MyClient {

    @GET
    @Path("{stockName}/chart/date/{date}")
    List<ChartEntry> getChart(@PathParam("stockName") final String stockName, @PathParam("date") final String date);
}
