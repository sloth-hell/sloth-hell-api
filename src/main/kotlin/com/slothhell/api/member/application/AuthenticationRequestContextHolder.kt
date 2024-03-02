package com.slothhell.api.member.application

class AuthenticationRequestContextHolder {
	companion object {
		private var context = ThreadLocal<AuthenticationRequestContext?>()
		fun getContext(): AuthenticationRequestContext? = context.get()
		fun setContext(context: AuthenticationRequestContext) = this.context.set(context)
	}
}

data class AuthenticationRequestContext(
	val memberId: Long,
	val clientRedirectUri: String,
)
