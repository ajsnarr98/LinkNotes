package com.github.ajsnarr98.linknotes.network.auth

import com.github.ajsnarr98.linknotes.network.result.ResultStatus
import com.github.ajsnarr98.linknotes.network.domain.User
import com.github.ajsnarr98.linknotes.network.result.ErrorType
import com.github.ajsnarr98.linknotes.network.result.map
import com.github.ajsnarr98.linknotes.network.storage.local.LocalStorage

class DefaultAuthRepository(
    private val authProvider: AuthRepository.AuthProvider,
    private val accountStore: LocalStorage<User>,
) : AuthRepository {

    override suspend fun attemptSignIn(): ResultStatus<User?, ErrorType> {
        TODO()
//        val savedUser: User = accountStore.get(AuthRepository.USER_FILE)
//            ?: return ResultStatus.Error(null, ErrorType.LocalWarning.NoSavedToken)
//
//        val googleIdToken = savedUser.googleIDToken
//            ?: return ResultStatus.Error(null, ErrorType.LocalWarning.NoSavedToken)
//
//        val googleId = savedUser.googleID
//            ?: return ResultStatus.Error(null, ErrorType.LocalWarning.NoSavedServiceId)
//
//        return if (authProvider.isValidGoogleToken(googleIdToken)) {
//            TODO()
////            signInWithGoogle(googleId, googleIdToken)
//        } else {
//            ResultStatus.Error(null, ErrorType.LocalWarning.InvalidToken)
//        }
    }

    override suspend fun signInWithGoogle(): ResultStatus<User?, ErrorType> {
        return authProvider.signInWithGoogle().map { authResult ->
            User(
                id = authResult
                    ?: throw IllegalStateException("Success result did not have a user id"),
//                    googleID = googleId,
//                    googleIDToken = googleIdToken,
            ).also { user ->
                accountStore.save(AuthRepository.USER_FILE, user)
            }
        }
    }

    override suspend fun signOut(): Boolean {
        authProvider.signOut()
        accountStore.save(AuthRepository.USER_FILE, null)
        return true
    }
}