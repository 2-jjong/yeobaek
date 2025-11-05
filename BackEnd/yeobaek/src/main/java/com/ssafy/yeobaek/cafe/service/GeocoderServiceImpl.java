package com.ssafy.yeobaek.cafe.service;

import com.ssafy.yeobaek.cafe.dto.response.Coordinates;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.ssafy.yeobaek.common.code.ResponseCode;
import com.ssafy.yeobaek.common.exception.CustomException;

@Service
public class GeocoderServiceImpl implements GeocoderService {

    @Value("${vworld.api.key}")
    private String apiKey;

    @Value("${vworld.api.search-type}")
    private String searchType;

    @Value("${vworld.api.epsg}")
    private String epsg;

    @Value("${vworld.api.url}")
    private String url;

    private static final Logger logger = LoggerFactory.getLogger(GeocoderServiceImpl.class);

    @Override
    public Coordinates getCoordinates(String address) {

        StringBuilder sb = new StringBuilder(url);
        sb.append("?service=address");
        sb.append("&request=getCoord");
        sb.append("&format=json");
        sb.append("&crs=").append(epsg);
        sb.append("&key=").append(apiKey);
        sb.append("&type=").append(searchType);
        sb.append("&address=").append(URLEncoder.encode(address, StandardCharsets.UTF_8));

        try {
            URL url = new URL(sb.toString());
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));

            JSONParser jspa = new JSONParser();
            JSONObject jsob = (JSONObject) jspa.parse(reader);
            JSONObject jsrs = (JSONObject) jsob.get("response");

            // API 응답 상태 확인
            String status = (String) jsrs.get("status");
            if (!"OK".equals(status)) {
                if ("NOT_FOUND".equals(status)) {
                    logger.warn("Geocoding failed for address '{}': Status NOT_FOUND", address);
                    throw new CustomException(ResponseCode.CAFE_GEOCODING_FAILED);
                } else if ("ERROR".equals(status)) {
                    JSONObject jsError = (JSONObject) jsrs.get("error");
                    String errorCode = (String) jsError.get("code");
                    String errorText = (String) jsError.get("text");
                    logger.error("VWorld Geocoding API returned ERROR. Code: {}, Text: {}", errorCode, errorText);
                    throw new CustomException(ResponseCode.GEOCODING_API_ERROR);
                }
                logger.error("VWorld Geocoding API returned an unexpected status: {}", status);
                throw new CustomException(ResponseCode.GEOCODING_API_ERROR);
            }

            JSONObject jsResult = (JSONObject) jsrs.get("result");
            JSONObject jsPoint = (JSONObject) jsResult.get("point");

            double longitude = Double.parseDouble((String) jsPoint.get("x"));
            double latitude = Double.parseDouble((String) jsPoint.get("y"));

            return new Coordinates(longitude, latitude);

        } catch (IOException | ParseException | NullPointerException e) {
            logger.error("Exception during geocoding for address '{}'", address, e);
            throw new CustomException(ResponseCode.GEOCODING_API_ERROR);
        }
    }
}
