package ru.serega6531.pixelwars.creative.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/auth")
    public String authStart(){
        return l(String.format("redirect:https://oauth.vk.com/authorize?client_id=%d&display=%s&" +
                "redirect_uri=%s&scope=%d&response_type=code&v=%s",
                clientId, display, redirectUri, scope, apiVersion));
    }

    @GetMapping(value = "/auth/redirect", params = {"code"})
    public String getCode(@RequestParam String code){
        /*try {
            String url = String.format("https://oauth.vk.com/access_token?client_id=%d&client_secret=%s&" +
                    "redirect_uri=%s&code=%s", clientId, clientSecret, redirectUri, code);

            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        return l(String.format("redirect:https://oauth.vk.com/access_token?client_id=%d&client_secret=%s&" +
                "redirect_uri=%s&code=%s", clientId, clientSecret, redirectUri, code));
    }

    @GetMapping(value = "/auth/redirect", params = {"error", "error_description"})
    public void codeError(@RequestParam String error, @RequestParam("error_description") String errorDescription){
        System.out.println("error = [" + error + "], errorDescription = [" + errorDescription + "]");
    }

    private String l(String s){
        System.out.println(s);
        return s;
    }

}
