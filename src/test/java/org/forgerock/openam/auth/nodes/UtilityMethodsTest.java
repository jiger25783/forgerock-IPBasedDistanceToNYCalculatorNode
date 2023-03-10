package org.forgerock.openam.auth.nodes;

import static org.junit.jupiter.api.Assertions.*;

class UtilityMethodsTest {

    double lat_ny = 40.7127837;
    double lon_ny = -74.0059413;
    double lat_la = 34.0522342;
    double lon_la = -118.2436849;
    double lat_sj = 37.3382082;
    double lon_sj = -121.8863286;
    //{lat: 41.8781136, lon: -87.6297982, name: 'Chicago, IL'},
    //{lat: 47.6062095, lon: -122.3320708, name: 'Seattle, WA'},
            ];
    @org.junit.jupiter.api.BeforeEach
    void setUp() {
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }


    @org.junit.jupiter.api.Test
    void deg2rad() {
        assertEquals(MainClass.getDistanceFromLatLonInKm(lat_ny, lon_ny, lat_la, lat_la), 3935.7484866833584);
        assertEquals(MainClass.getDistanceFromLatLonInKm(lat_ny, lon_ny, lat_la, lat_la), 4101.863304451695);
    }

    @org.junit.jupiter.api.Test
    void getDistanceFromLatLonInKm() {
    }
}