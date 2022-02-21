package it.unimore.dipi.iot.metering.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unimore.dipi.iot.metering.server.resources.model.ResourceURIDescriptor;

import java.util.*;

public class Utils {
    public static ArrayList<ResourceURIDescriptor> parseLinkFormatText (String payload) {
        List<String> rows = Arrays.asList(payload.split(","));
        ArrayList<ResourceURIDescriptor> resources = new ArrayList<>();
        rows.forEach(row -> {
            List<String> tokens = Arrays.asList(row.split(";"));
            Map<String, Object> tokenMap = new HashMap<>();
            tokens.forEach(token -> {
                if (token.charAt(0) == '<') {
                    String cleanToken = token.replace("<", "").replace(">", "");
                    tokenMap.put("uri", cleanToken);
                } else if (token.equals("obs")) {
                    tokenMap.put(token, true);
                } else {
                    String[] split = token.split("=");
                    split[1] = split[1].replace("\"", "");
                    tokenMap.put(split[0], split[1]);
                }
            });
            resources.add(new ResourceURIDescriptor(
                    (String)tokenMap.get("uri"),
                    (String)tokenMap.get("rt"),
                    (String)tokenMap.get("if"),
                    (String)tokenMap.get("title")
            ));
        });
        return resources;
    }

    public static Optional<SenMLPack> parseSenmlString (String payload, ObjectMapper mapper) {
        try {
            if (payload == null)
                throw new Exception("NULL payload string");

            return Optional.ofNullable(mapper.readValue(payload, new TypeReference<SenMLPack>() {}));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static void main (String[] args) {
        String payload = "</device-info>;ct=\"110 0\";if=\"core.rp\";rt=\"iot:config:device-info\";title=\"MeterInfo\",</consumption>;ct=\"110 0\";if=\"core.s\";obs;rt=\"iot:sensor:energy-consumption\";title=\"EnergyConsumptionSensor\",</switch>;ct=\"110 0\";if=\"core.a\";obs;rt=\"iot:actuator:switch\";title=\"SwitchActuator\"";
        List<ResourceURIDescriptor> resList = parseLinkFormatText(payload);
        resList.forEach(System.out::println);
    }
}
