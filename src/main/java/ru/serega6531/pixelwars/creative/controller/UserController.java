package ru.serega6531.pixelwars.creative.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.serega6531.pixelwars.creative.model.User;
import ru.serega6531.pixelwars.creative.model.response.JsonResponse;
import ru.serega6531.pixelwars.creative.model.vk.VKUser;
import ru.serega6531.pixelwars.creative.repository.UserRepository;

import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
public class UserController {

    @Value("${vk.api-version}")
    private String vkApiVersion;

    private final Logger logger;
    private final UserRepository repository;

    @Autowired
    public UserController(Logger logger, UserRepository repository) {
        this.logger = logger;
        this.repository = repository;
    }

    void saveUser(User user) {
        repository.save(user);
    }

    @GetMapping("/user/get/{id}")
    public User getUser(@PathVariable Integer id) {
        if (id == null)
            return null;

        return repository.findOne(id);
    }

    User getUser(HttpSession session) {
        Integer id = (Integer) session.getAttribute("vk_id");
        User user = getUser(id);
        if (user == null) {
            user = new User(id);
            saveUser(user);
        }
        return user;
    }

    @PostMapping("/user/ban/{id}")
    public JsonResponse banUser(@PathVariable int id, HttpSession session) {
        if (session.isNew() || !session.getAttributeNames().hasMoreElements()) {
            return JsonResponse.UNAUTHORIZED;
        }

        User admin = getUser((Integer) session.getAttribute("vk_id"));
        if (!admin.isAdmin()) {
            return JsonResponse.INSUFFICIENT_PRIVILEGES;
        }

        User target = repository.findOne(id);

        if (target == null) {
            return JsonResponse.USER_NOT_EXISTS;
        }

        if (target.isAdmin()) {
            return JsonResponse.CANNOT_BE_BANNED;
        }

        target.setBanned(true);
        repository.save(target);
        return JsonResponse.SUCCESS;
    }

    @PostMapping("/user/unban/{id}")
    public JsonResponse unbanUser(@PathVariable int id, HttpSession session) {
        if (session.isNew() || !session.getAttributeNames().hasMoreElements()) {
            return JsonResponse.UNAUTHORIZED;
        }

        User admin = getUser((Integer) session.getAttribute("vk_id"));
        if (!admin.isAdmin()) {
            return JsonResponse.INSUFFICIENT_PRIVILEGES;
        }

        User target = repository.findOne(id);

        if (target == null) {
            return JsonResponse.USER_NOT_EXISTS;
        }

        target.setBanned(false);
        repository.save(target);
        return JsonResponse.SUCCESS;
    }

    @GetMapping(value = "/user/fullname")
    public VKUser getFullName(HttpSession session) {
        Integer id = (Integer) session.getAttribute("vk_id");
        String token = (String) session.getAttribute("vk_token");

        if (id == null || token == null)
            return VKUser.ANONYMOUS;

        String urlStr = String.format("https://api.vk.com/method/users.get?user_ids=%s&access_token=%s&v=%s",
                id, token, vkApiVersion);

        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            ObjectMapper jsonMapper = new ObjectMapper();
            JsonNode root = jsonMapper.readTree(reader);
            connection.disconnect();

            if(root.has("error")){
                logger.error("Error while getting name for user {}: {}", id, root.toString());
                return VKUser.ANONYMOUS;
            }

            JsonNode userNode = root.get("response").get(0);

            return jsonMapper.treeToValue(userNode, VKUser.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return VKUser.ANONYMOUS;
    }

}
