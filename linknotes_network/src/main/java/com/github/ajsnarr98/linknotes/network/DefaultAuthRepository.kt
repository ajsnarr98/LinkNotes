package com.github.ajsnarr98.linknotes.network

import com.github.ajsnarr98.linknotes.network.domain.User
import com.github.ajsnarr98.linknotes.network.storage.local.LocalStorage

class DefaultAuthRepository(
    private val authProvider: AuthRepository.AuthProvider,
    private val accountStore: LocalStorage<User>,
) : AuthRepository {

    override suspend fun attemptSignIn(): ResultStatus<User?> {
        val savedUser: User = accountStore.get(AuthRepository.USER_FILE)
            ?: return ResultStatus.Error(null, ResultStatus.ErrorType.LocalWarning.NoSavedToken)

        val googleIdToken = savedUser.googleIDToken
            ?: return ResultStatus.Error(null, ResultStatus.ErrorType.LocalWarning.NoSavedToken)

        val googleId = savedUser.googleID
            ?: return ResultStatus.Error(null, ResultStatus.ErrorType.LocalWarning.NoSavedServiceId)

        return if (authProvider.isValidGoogleToken(googleIdToken)) {
            signInWithGoogle(googleId, googleIdToken)
        } else {
            ResultStatus.Error(null, ResultStatus.ErrorType.LocalWarning.InvalidToken)
        }
    }

    override suspend fun signInWithGoogle(googleId: String, googleIdToken: String): ResultStatus<User?> {
        return when (val authResult = authProvider.signInWithGoogle(googleIdToken)) {
            is ResultStatus.Success -> {
                val user = User(
                    id = authResult.result
                        ?: throw IllegalStateException("Success result did not have a user id"),
                    googleID = googleId,
                    googleIDToken = googleIdToken,
                )

                accountStore.save(AuthRepository.USER_FILE, user)

                ResultStatus.Success(
                    user
                )
            }
            is ResultStatus.Error -> ResultStatus.Error(
                null,
                authResult.error,
            )
        }
    }

    override suspend fun signOut(): Boolean {
        authProvider.signOut()
        accountStore.save(AuthRepository.USER_FILE, null)
        return true
    }
}