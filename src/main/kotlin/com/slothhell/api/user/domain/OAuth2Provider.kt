package com.slothhell.api.user.domain

import org.springframework.security.oauth2.core.user.OAuth2User

enum class OAuth2Provider {

	GOOGLE {
		override fun getOAuth2Attribute(oauth2User: OAuth2User): OAuth2Attribute {
			return OAuth2Attribute(
				id = oauth2User.name,
				email = oauth2User.attributes["email"] as String,
				profileUrl = oauth2User.attributes["picture"] as String,
			)
		}
	},
	NAVER {
		override fun getOAuth2Attribute(oauth2User: OAuth2User): OAuth2Attribute {
			val oauth2Attributes = oauth2User.attributes["response"] as Map<*, *>
			return OAuth2Attribute(
				id = oauth2Attributes["id"] as String,
				email = oauth2Attributes["email"] as String,
				profileUrl = oauth2Attributes["profile_image"] as String,
			)
		}
	},
	KAKAO {
		override fun getOAuth2Attribute(oauth2User: OAuth2User): OAuth2Attribute {
			val oauth2Attributes = oauth2User.attributes["properties"] as Map<*, *>
			return OAuth2Attribute(
				id = oauth2User.name,
//                email = oauth2User.attributes["email"] as String,
				email = "",
				profileUrl = oauth2Attributes["profile_image"] as String,
			)
		}
	};
//	APPLE

	abstract fun getOAuth2Attribute(oauth2User: OAuth2User): OAuth2Attribute

}
