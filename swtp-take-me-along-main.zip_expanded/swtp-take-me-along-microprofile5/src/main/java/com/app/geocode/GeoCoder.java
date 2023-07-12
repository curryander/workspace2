package com.app.geocode;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.app.model.Position;

import java.util.Optional;
import jakarta.inject.Inject;

public class GeoCoder implements IGeoCoder {

    @Inject
    @RestClient
    INominatimClient nominatimClient;

    @Override
    public Optional<Position> geocode(String street, String streetNumber, String zip, String city, String country) {
        final var results = nominatimClient.search(street + " " + streetNumber, city, null, null, zip, "json", "1");

        if (results.size() > 0) {
            final var firstResult = results.get(0);
            Position position = new Position(Double.parseDouble(firstResult.getLon()),
                    Double.parseDouble(firstResult.getLat()));
            return Optional.of(position);
        } else {
            return Optional.empty();
        }
    }
}
