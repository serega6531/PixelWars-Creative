package ru.serega6531.pixelwars.creative.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.serega6531.pixelwars.creative.model.User;
import ru.serega6531.pixelwars.creative.model.vk.VKError;
import ru.serega6531.pixelwars.creative.model.vk.VKTokenInfo;
import ru.serega6531.pixelwars.creative.model.vk.VKUser;

import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Controller
public class AuthController {

    @Value("${vk.client-id}")
    private int clientId;

    @Value("${vk.api-version}")
    private String apiVersion;

    @Value("${vk.client-secret}")
    private String clientSecret;

    @Value("${vk.display}")
    private String display;

    @Value("${vk.redirect-uri}")
    private String redirectUri;

    @Value("${vk.scope}")
    private int scope;

    private final Logger logger;
    private final UserController userController;

    @Autowired
    public AuthController(Logger logger, UserController userController) {
        this.logger = logger;
        this.userController = userController;
    }

    @GetMapping("/auth")
    public String authStart() {
        return String.format("redirect:https://oauth.vk.com/authorize?client_id=%d&display=%s&" +
                        "redirect_uri=%s&scope=%d&response_type=code&v=%s",
                clientId, display, redirectUri, scope, apiVersion);
    }

    @GetMapping(value = "/auth/redirect", params = {"code"})
    public String getCode(@RequestParam String code, HttpSession session) {
        try {
            String urlStr = String.format("https://oauth.vk.com/access_token?client_id=%d&client_secret=%s&" +
                    "redirect_uri=%s&code=%s", clientId, clientSecret, redirectUri, code);

            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            ObjectMapper jsonMapper = new ObjectMapper();
            String tokenJson = reader.readLine();
            connection.disconnect();

            if (tokenJson.contains("token")) {
                try {
                    VKTokenInfo tokenInfo = jsonMapper.readValue(tokenJson, VKTokenInfo.class);
                    int userId = tokenInfo.getUserId();
                    String token = tokenInfo.getToken();

                    User user = userController.getUser(userId);
                    if (user != null) {
                        if (user.isBanned()) {
                            return "redirect:/?error=Ошибка входа&error_description=Вы были забанены администратором";
                        }
                    } else {
                        user = new User(userId);
                        userController.createUser(user);
                    }

                    session.setAttribute("vk_id", userId);
                    session.setAttribute("vk_token", token);
                    //session.setAttribute("expires_at", System.currentTimeMillis() / 1000L + tokenInfo.getExpires());
                    session.setMaxInactiveInterval(60 * 60 * 24 * 7); // 7 дней

                    new Thread(() -> getUserFullName(userId, token, session)).start();

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
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping(value = "/auth/redirect", params = {"error", "error_description"})
    public String codeError(@RequestParam String error, @RequestParam("error_description") String errorDescription) {
        return String.format("redirect:/auth/redirect?error=%s&error_description=%s", error, errorDescription);
    }

    private void getUserFullName(int id, String token, HttpSession session){
        String urlStr = String.format("https://api.vk.com/method/users.get?user_ids=%s&access_token=%s&v=%s",
                id, token, apiVersion);

        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            ObjectMapper jsonMapper = new ObjectMapper();
            JsonNode root = jsonMapper.readTree(reader);
            JsonNode userNode = root.get("response").get(0);
            VKUser user = jsonMapper.treeToValue(userNode, VKUser.class);

            session.setAttribute("first_name", user.getFirstName());
            session.setAttribute("last_name", user.getLastName());
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
