package com.dudu.oauth;

import org.json.JSONObject;
import org.springframework.security.jwt.crypto.sign.RsaSigner;

import java.util.Base64;
import java.util.List;
import java.util.Random;

import static java.nio.charset.StandardCharsets.US_ASCII;

public class TokenIssuer {
    private static final String TOKEN_TYPE = "JWT";
    private static final String TOKEN_ALGO = "RS256";
    private static final String TOKEN_ISSUER = "dudu";

    private RsaSigner signer;

    public TokenIssuer(String privKey) {
        signer = new RsaSigner(privKey);
    }

    public String issue(long userId, List<String> scopes) {
        var headerJson = new JSONObject();
        headerJson.put("typ", TOKEN_TYPE);
        headerJson.put("alg", TOKEN_ALGO);

        var claimsJson = new JSONObject();
        claimsJson.put("iss", TOKEN_ISSUER);
        claimsJson.put("exp", System.currentTimeMillis()/1000 + 60*60);         // expired after one hour
        claimsJson.put("iat", System.currentTimeMillis()/1000);
        claimsJson.put("jti", new Random().nextLong());

        claimsJson.put("UserId", userId);
        claimsJson.put("Scopes", String.join(",", scopes));

        var signatureBytes = signer.sign(claimsJson.toString().getBytes(US_ASCII));

        StringBuilder token = new StringBuilder();
        var encoder = Base64.getEncoder();
        token.append(new String(encoder.encode(headerJson.toString().getBytes(US_ASCII)), US_ASCII));
        token.append(".");

        token.append(new String(encoder.encode(claimsJson.toString().getBytes(US_ASCII)), US_ASCII));
        token.append(".");

        token.append(new String(encoder.encode(signatureBytes), US_ASCII));

        return token.toString();
    }
}
