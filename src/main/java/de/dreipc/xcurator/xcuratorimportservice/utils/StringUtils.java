package de.dreipc.xcurator.xcuratorimportservice.utils;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
public class StringUtils {


    public static Integer parseNodeToInt(JsonNode node) {
        try {
            return Integer.parseInt(parseNodeToString(node));
        } catch (NullPointerException e) {
            log.error("cant parse to int");
            throw e;
        }


    }

    public static String parseNodeToString(JsonNode node) {
        try {
            return node.asText();
        } catch (NullPointerException e) {
            log.debug("cant parse", e);
            return null;
        }
    }

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isBlank();
    }

    public static URL parseNodeToUrl(JsonNode node) {
        try {
            return new URL(node.asText());
        } catch (MalformedURLException e) {
            log.error("Can‘t parse url");
            throw new RuntimeException(e);
        }
    }
}
