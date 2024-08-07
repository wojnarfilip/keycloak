package org.keycloak.test.examples;

import com.nimbusds.oauth2.sdk.TokenIntrospectionResponse;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.test.framework.annotations.InjectOAuthClient;
import org.keycloak.test.framework.annotations.InjectRealm;
import org.keycloak.test.framework.annotations.KeycloakIntegrationTest;
import org.keycloak.test.framework.oauth.OAuthClient;
import org.keycloak.test.framework.realm.ManagedRealm;
import jakarta.ws.rs.core.Response.Status;

@KeycloakIntegrationTest()
public class OAuthRevocationTest {

    @InjectRealm
    ManagedRealm realm;

    @InjectOAuthClient
    OAuthClient oAuthClient;

    @Test
    public void testAccessTokenRevocation() throws Exception {
        TokenResponse tokenResponse = oAuthClient.clientCredentialGrant();
        Assertions.assertTrue(tokenResponse.indicatesSuccess());
        Assertions.assertNotNull(tokenResponse.toSuccessResponse().getTokens().getAccessToken());

        AccessToken accessToken = tokenResponse.toSuccessResponse().getTokens().getAccessToken();
        TokenIntrospectionResponse introspectionResponse = oAuthClient.introspection(accessToken);
        Assertions.assertTrue(introspectionResponse.indicatesSuccess());
        Assertions.assertNotNull(introspectionResponse.toSuccessResponse().getScope());

        Assertions.assertEquals(Status.OK.getStatusCode(), oAuthClient.revokeAccessToken(accessToken).getStatusCode());

        introspectionResponse = oAuthClient.introspection(accessToken);
        Assertions.assertTrue(introspectionResponse.indicatesSuccess());
        Assertions.assertNull(introspectionResponse.toSuccessResponse().getScope());
    }
}
