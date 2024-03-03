package com.slothhell.api.member.domain

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.client.RestClient

enum class OAuth2Provider {

	GOOGLE {
		override fun getOAuth2Attribute(oauth2User: OAuth2User): OAuth2Attribute {
			return OAuth2Attribute(oauth2User.name)
		}

		override fun validateProviderAccessToken(accessToken: String): Boolean {
			return validateProviderToken("https://www.googleapis.com/oauth2/v3/tokeninfo", accessToken)
		}
	},
	NAVER {
		override fun getOAuth2Attribute(oauth2User: OAuth2User): OAuth2Attribute {
			val oauth2Attributes = oauth2User.attributes["response"] as Map<*, *>
			return OAuth2Attribute(oauth2Attributes["id"] as String)
		}

		override fun validateProviderAccessToken(accessToken: String): Boolean {
			return validateProviderToken("https://openapi.naver.com/v1/nid/verify", accessToken)
		}
	},
	KAKAO {
		override fun getOAuth2Attribute(oauth2User: OAuth2User): OAuth2Attribute {
			return OAuth2Attribute(oauth2User.name)
		}

		override fun validateProviderAccessToken(accessToken: String): Boolean {
			return validateProviderToken("https://kapi.kakao.com/v1/user/access_token_info", accessToken)
		}
	};
//	APPLE
//	TODO: APPLE 로그인 적용 필요

	abstract fun getOAuth2Attribute(oauth2User: OAuth2User): OAuth2Attribute
	abstract fun validateProviderAccessToken(accessToken: String): Boolean
	fun validateProviderToken(tokenCheckUrl: String, accessToken: String): Boolean {
		val response = RestClient.builder()
			.baseUrl(tokenCheckUrl)
			.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
			.build()
			.get()
			.retrieve()
			.toBodilessEntity()
		return response.statusCode == HttpStatus.OK
	}

}
