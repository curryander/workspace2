package com.app.geocode;

import java.util.Optional;
import com.app.model.Position;

public interface IGeoCoder {
    Optional<Position> geocode(String street, String streetNumber, String zip, String city, String country);
}
