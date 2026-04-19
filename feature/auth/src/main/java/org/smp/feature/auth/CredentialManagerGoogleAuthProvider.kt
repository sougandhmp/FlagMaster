package org.smp.feature.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CredentialManagerGoogleAuthProvider @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : GoogleAuthProvider {

    private val credentialManager = CredentialManager.create(context)

    override suspend fun getIdToken(): String {
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.google_oauth_web_client_id))
                    .setAutoSelectEnabled(false)
                    .build()
            )
            .build()
        val result = credentialManager.getCredential(context, request)
        val credential = result.credential
        check(
            credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) { "Unexpected credential type: ${credential.type}" }
        return GoogleIdTokenCredential.createFrom(credential.data).idToken
    }
}
