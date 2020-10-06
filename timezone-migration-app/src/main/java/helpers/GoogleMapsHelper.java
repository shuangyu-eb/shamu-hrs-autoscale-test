
package helpers;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.TimeZoneApi;
import com.google.maps.model.ComponentFilter;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import entity.TimezoneAddress;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.TimeZone;

@Slf4j
public class GoogleMapsHelper {
    private String googleGeoCodingApiKey;
    public GoogleMapsHelper(final String googleGeoCodingApiKey){
        this.googleGeoCodingApiKey = googleGeoCodingApiKey;
    }

    public String findTimezone(final TimezoneAddress timezoneAddress) {
        String zipCodeTimezone = findTimezoneByZipCode(timezoneAddress.getPostalCode());
        if (zipCodeTimezone!=null){
            return zipCodeTimezone;
        }
        String cityStateTimezone = findTimezoneByCityState(timezoneAddress.getCity(),
                timezoneAddress.getStateProvinceCode());
        if (cityStateTimezone != null){
            return cityStateTimezone;
        }
        String stateTimezone  = findTimezoneByState(timezoneAddress.getStateProvinceCode());
        return stateTimezone;
    }

    private String findTimezoneByZipCode(final String postalCode) {
        try {
            final Optional<LatLng> location = findLocationByPostalCode(postalCode);
            if (location.isPresent()) {
                final LatLng latLng = location.get();
                return findTimezoneByLocation(latLng);
            } else {
                return null;
            }
        } catch (Exception e) {
            log.warn("Error in Zipcode Timezone, going to city/state " + postalCode,e);
            return null;
        }
    }

    private String findTimezoneByCityState(final String city, final String state){
        try {
            final Optional<LatLng> location = findLocationByCityState(city, state);
            if (location.isPresent()) {
                final LatLng latLng = location.get();
                return findTimezoneByLocation(latLng);
            } else {
                return null;
            }
        } catch (Exception e) {
            log.warn("Error in City/State Timezone, going to state " + city + ", " + state,e);
            return null;
        }
    }

    private String findTimezoneByState(final String state) {
        try {
            final Optional<LatLng> location = findLocationByState(state);
            if (location.isPresent()) {
                final LatLng latLng = location.get();
                return findTimezoneByLocation(latLng);
            } else {
                return null;
            }
        } catch (Exception e) {
            log.warn("Error in State " +  state,e);
            return null;
        }
    }

    private Optional<LatLng> findLocationByState(final String state)
            throws Exception {
        final GeoApiContext geoCodingApiContext =
                new GeoApiContext.Builder().apiKey(googleGeoCodingApiKey).build();
        final GeocodingResult[] results;
        results =
                GeocodingApi.geocode(geoCodingApiContext, "")
                        .components(new ComponentFilter("administrative_area", state))
                        .await();
        geoCodingApiContext.shutdown();
        return results.length == 0 ? Optional.empty() : Optional.of(results[0].geometry.location);
    }

    private Optional<LatLng> findLocationByCityState(final String city, final String state)
            throws Exception {
        final GeoApiContext geoCodingApiContext =
                new GeoApiContext.Builder().apiKey(googleGeoCodingApiKey).build();
        final GeocodingResult[] results;
        results =
                GeocodingApi.geocode(geoCodingApiContext, "")
                        .components(new ComponentFilter("administrative_area", state),
                                new ComponentFilter("locality",city))
                        .await();
        geoCodingApiContext.shutdown();
        return results.length == 0 ? Optional.empty() : Optional.of(results[0].geometry.location);
    }

    private Optional<LatLng> findLocationByPostalCode(final String postalCode)
            throws Exception {
        final GeoApiContext geoCodingApiContext =
                new GeoApiContext.Builder().apiKey(googleGeoCodingApiKey).build();
        final GeocodingResult[] results;
        results =
                GeocodingApi.geocode(geoCodingApiContext, "")
                        .components(new ComponentFilter("postal_code", postalCode))
                        .await();
        geoCodingApiContext.shutdown();
        return results.length == 0 ? Optional.empty() : Optional.of(results[0].geometry.location);
    }

    private String findTimezoneByLocation(final LatLng latLng)
            throws Exception {
        final GeoApiContext mapApiContext = new GeoApiContext.Builder().apiKey(googleGeoCodingApiKey).build();
        final TimeZone tz = TimeZoneApi.getTimeZone(mapApiContext, latLng).await();
        mapApiContext.shutdown();
        return tz.getID();
    }


}