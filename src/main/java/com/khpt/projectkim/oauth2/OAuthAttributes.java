package com.khpt.projectkim.oauth2;
import com.khpt.projectkim.entity.Role;
import com.khpt.projectkim.entity.User;

import java.io.Serializable;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthAttributes implements Serializable {

    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String login;
    private String picture;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String login, String picture) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.login = login;
        this.picture = picture;
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        return ofGithub(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGithub(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .login((String) attributes.get("login"))
                .picture((String) attributes.get("avatar_url"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public User toEntity() {
        return User.builder()
                .login(login)
                .picture(picture)
                .role(Role.GUEST)
                .build();
    }
}