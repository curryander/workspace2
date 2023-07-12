package com.app.geocode;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@RegisterRestClient(configKey = "nominatimClient", baseUri = "https://nominatim.openstreetmap.org")
@Produces(MediaType.APPLICATION_JSON)

public interface INominatimClient {
    @GET
    @Path("search")
    List<NominatimGeoCodeResponse> search(
            @QueryParam("street") String street,
            @QueryParam("city") String city,
            @QueryParam("country") String country,
            @QueryParam("state") String state,
            @QueryParam("postalcode") String postalCode,
            @QueryParam("format") String format,
            @QueryParam("addressdetails") String addressDetails);
}
