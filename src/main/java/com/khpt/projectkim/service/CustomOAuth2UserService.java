package com.khpt.projectkim.service;

import javax.servlet.http.HttpSession;
import com.khpt.projectkim.entity.User;
import com.khpt.projectkim.oauth2.CustomOAuth2User;
import com.khpt.projectkim.oauth2.OAuthAttributes;
import com.khpt.projectkim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final HttpSession httpSession;

    private final DiscordWebhookService discordWebhookService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest
                .getClientRegistration()
                .getRegistrationId();
        String userNameAttributeName =
                userRequest
                .getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        User user = saveOrUpdate(attributes);

        httpSession.setAttribute("user", user.getId().toString());

        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey(),
                attributes
        );
    }


    private User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findByLogin(attributes.getLogin())
                .map(entity -> entity.update(attributes.getLogin(), attributes.getPicture()))
                .orElse(attributes.toEntity());

        log.debug("Oauth: before webhook service");
        discordWebhookService.queueLoginLog(user.getId().toString(), user.getLogin());
        log.debug("Oauth: after webhook service");

        return userRepository.save(user);
    }
}