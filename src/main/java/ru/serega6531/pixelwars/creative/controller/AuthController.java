package ru.serega6531.pixelwars.creative.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.serega6531.pixelwars.creative.model.VKError;
import ru.serega6531.pixelwars.creative.model.VKTokenInfo;

import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Controller
public class AuthController {

    @Value("${vk.client-id}")
    int clientId;

    @Value("${vk.api-version}")
    String apiVersion;

    @Value("${vk.client-secret}")
    String clientSecret;

    @Value("${vk.display}")
    String display;

    @Value("${vk.redirect-uri}")
    String redirectUri;

    @Value("${vk.scope}")
    int scope;

    @Autowired
    private Logger logger;

    @GetMapping("/auth")
    public String authStart(){
        return String.format("redirect:https://oauth.vk.com/authorize?client_id=%d&display=%s&" +
                "redirect_uri=%s&scope=%d&response_type=code&v=%s",
                clientId, display, redirectUri, scope, apiVersion);
    }

    @GetMapping(value = "/auth/redirect", params = {"code"})
    public String getCode(@RequestParam String code, HttpSession session){
        try {
            String urlStr = String.format("https://oauth.vk.com/access_token?client_id=%d&client_secret=%s&" +
                "redirect_uri=%s&code=%s", clientId, clientSecret, redirectUri, code);

            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            ObjectMapper jsonMapper = new ObjectMapper();
            String tokenJson = reader.readLine();

            if(tokenJson.contains("token")) {
                try {
                    VKTokenInfo tokenInfo = jsonMapper.readValue(tokenJson, VKTokenInfo.class);
                    session.setAttribute("vk_id", tokenInfo.getUserId());
                    session.setAttribute("vk_token", tokenInfo.getToken());
                    session.setAttribute("expires_at", System.currentTimeMillis() / 1000L + tokenInfo.getExpires());
                    session.setMaxInactiveInterval(tokenInfo.getExpires());
                    return "redirect:/";
                } catch (JsonProcessingException e) {
                    logger.error("Error parsing token json: " + tokenJson);
                    e.printStackTrace();
                    return "redirect:/auth/redirect?error=JSON Error&error_description=Error parsing token json: " + tokenJson;
                }
            } else {
                try {
                    VKError error = jsonMapper.readValue(tokenJson, VKError.class);
                    logger.error("VK returned error on /access_token: %s, %s", error.getError(), error.getErrorDescription());
                    return String.format("redirect:/?error=%s&error_description=%s", error.getError(), error.getErrorDescription());
                } catch (JsonProcessingException e) {
                    logger.error("Error parsing token json: " + tokenJson);
                    e.printStackTrace();
                    return "redirect:/?error=JSON Error&error_description=Error parsing token json: " + tokenJson;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return String.format("redirect:/?error=%s&error_description=%s", e.getClass().toString(), e.getMessage());
        }
    }

    @GetMapping("/auth/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/";
    }

    @ResponseBody
    @GetMapping(value = "/auth/redirect", params = {"error", "error_description"})
    public String codeError(@RequestParam String error, @RequestParam("error_description") String errorDescription){
        return String.format("redirect:/auth/redirect?error=%s&error_description=%s", error, errorDescription);
    }

}
