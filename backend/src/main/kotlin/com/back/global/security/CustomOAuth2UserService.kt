package com.back.global.security

import com.back.domain.member.member.service.MemberService
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private enum class OAuth2Provider {
    KAKAO, GOOGLE, NAVER;

    companion object {
        fun from(registrationId: String): OAuth2Provider =
            entries.firstOrNull { it.name.equals(registrationId, ignoreCase = true) }
                ?: error("Unsupported provider: $registrationId")
    }
}

@Service
class CustomOAuth2UserService(
    private val memberService: MemberService
) : DefaultOAuth2UserService() {

    @Transactional
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)
        val provider = OAuth2Provider.from(userRequest.clientRegistration.registrationId)

        val (oauthUserId, nickname, profileImgUrl) = when (provider) {
            OAuth2Provider.KAKAO -> {
                val props = (oAuth2User.attributes.getValue("properties") as Map<String, Any>)
                Triple(
                    oAuth2User.name,
                    props.getValue("nickname") as String,
                    props.getValue("profile_image") as String
                )
            }

            OAuth2Provider.GOOGLE -> {
                val attrs = oAuth2User.attributes
                Triple(
                    oAuth2User.name,
                    attrs.getValue("name") as String,
                    attrs.getValue("picture") as String
                )
            }

            OAuth2Provider.NAVER -> {
                val resp = (oAuth2User.attributes.getValue("response") as Map<String, Any>)
                Triple(
                    resp.getValue("id") as String,
                    resp.getValue("nickname") as String,
                    resp.getValue("profile_image") as String
                )
            }
        }

        val username = "${provider.name}__$oauthUserId"
        val password = ""

        val member = memberService.modifyOrJoin(username, password, nickname, profileImgUrl).data

        return SecurityUser(
            member.id,
            member.username,
            member.password ?: "",
            member.name,
            member.authorities
        )
    }
}
