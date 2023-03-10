/*
 * Copyright © 2017 ForgeRock, AS.
 *
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 *
 * otpSelectorNode: Created by Charan Mann on 12/7/17 , 11:48 AM.
 */


package org.forgerock.openam.auth.nodes;

import com.google.inject.assistedinject.Assisted;
import com.sun.identity.shared.debug.Debug;
import org.forgerock.json.JsonValue;
import org.forgerock.json.JsonValueException;
import org.forgerock.openam.annotations.sm.Attribute;
import org.forgerock.openam.auth.node.api.*;
import org.forgerock.util.i18n.PreferredLocales;

import javax.inject.Inject;
import javax.security.auth.callback.ChoiceCallback;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;



/**
 * An authentication decision node which identify user(client)'s ip address and identify its distance from NY (
 */
@Node.Metadata((outcomeProvider = SingleOutcomeNode.OutcomeProvider.class,
        configClass = IPBasedDistanceToNYCalculatorNode.Config.class)
public class IPBasedDistanceToNYCalculatorNode  extends SingleOutcomeNode {

    private final static String DEBUG_FILE = "IPBasedDistanceToNYCalculatorNode";
    private final static String DISTENCE_FROM_NY_KEY = "DistanceFromNYInKilloMeter";
    public static final String IPAPI_URL = "ipapi.url";
    public static final String NY_COORDINATES_LAT = "ny.coordinates.lat";
    public static final String NY_COORDINATES_LON = "ny.coordinates.lon";
    private final Config config;
    protected Debug debug = Debug.getInstance(DEBUG_FILE);
    private static final String BUNDLE = IPBasedDistanceToNYCalculatorNode.class.getName().replace(".", "/");

    /**
     * Guice constructor.
     *
     * @param config The service config for the node.
     * @throws NodeProcessException If there is an error reading the configuration.
     */
    @Inject
    public IPBasedDistanceToNYCalculatorNode(@Assisted Config config)
            throws NodeProcessException {
        this.config = config;
    }

    @Override
    public Action process(TreeContext context) throws NodeProcessException {

        String clientIP = context.request.clientIp;
        double distanceFromNY = calculateDistanceFromNY(clientIP);
        context.sharedState.put(DISTENCE_FROM_NY_KEY, String.valueOf(distanceFromNY));
        return goToNext().build();

    }

    /**
     * Method to make api call and calculate distance of client ip from newyork
     * @param clientIP
     * @return
     */
    private double calculateDistanceFromNY(String clientIP) {
        double dFromNY;
        ResourceBundle bundle = locales.getBundleInPreferredLocale(BUNDLE, OutcomeProvider.class.getClassLoader());
        String ipApiUrl = bundle.getString(IPAPI_URL) == null ? "https://ip-api.com/json/" : bundle.getString(IPAPI_URL);
        String nyLat = bundle.getString(NY_COORDINATES_LAT);
        String nyLon = bundle.getString(NY_COORDINATES_LON);
        URI targetURI = new URI(ipApiUrl+clientIP);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(targetURI)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        ObjectMapper objectMapper = new ObjectMapper();
        IPResponse[] todos = objectMapper.readValue(response.body(), IPResponse[].class);
        dFromNY = UtilityMethods.getDistanceFromLatLonInKm(Double.valueOf(nyLat), Double.valueOf(nyLon), Double.valueOf(todos[0].lat), Double.valueOf(todos[0].lon));
        return dFromNY;
    }



    private Action.ActionBuilder goTo(String outcome) {
        return Action.goTo(outcome);
    }

    /**
     * Configuration for the node.
     */
    public interface Config {
        @Attribute(order = 100)
        List<String> choices();
    }

    /**
     * Provides the outcomes for the choice collector node.
     * Not necessary for single outcome node
    public static class ChoiceCollectorOutcomeProvider implements OutcomeProvider {


        @Override
        public List<Outcome> getOutcomes(PreferredLocales locales, JsonValue nodeAttributes) {
            ResourceBundle bundle = locales.getBundleInPreferredLocale(BUNDLE, OutcomeProvider.class.getClassLoader());
            try {
                return Arrays.asList(new Outcome("success","success"));
                return nodeAttributes.get("choices").required()
                        .asList(String.class)
                        .stream()
                        .map(choice -> new Outcome(choice, (bundle.containsKey(choice)) ? bundle.getString(choice) : choice))
                        .collect(Collectors.toList());
            } catch (Exception e) {
                return emptyList();
            }
        }
    }
    */

}

