package com.khpt.projectkim.service;

import javax.servlet.http.HttpSession;
import com.khpt.projectkim.entity.User;
import com.khpt.projectkim.oauth2.CustomOAuth2User;
import com.khpt.projectkim.oauth2.OAuthAttributes;
import com.khpt.projectkim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        System.out.println("load user");
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        System.out.println("get regis id");
        String registrationId = userRequest
                .getClientRegistration()
                .getRegistrationId();
        System.out.println("get username attr name");
        String userNameAttributeName =
                userRequest
                .getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        System.out.println("load attrs");
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        System.out.println("save or update");
        User user = saveOrUpdate(attributes);

        System.out.println("save to session");
        httpSession.setAttribute("user", user);
        System.out.println("session save done");

        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey(),
                attributes
        );
    }


    private User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                .orElse(attributes.toEntity());

        System.out.println(user + " SAVE!!");
        return userRepository.save(user);
    }
}