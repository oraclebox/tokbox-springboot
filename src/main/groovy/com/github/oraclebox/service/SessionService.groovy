package com.github.oraclebox.service

import com.github.oraclebox.conf.OpenTokConfig
import com.github.oraclebox.model.TokToken
import com.opentok.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

interface SessionService {
    String getSessionId(String mediaMode, String archiveMode);

    String createToken(String sessionId, String username, long expireTime);

    TokToken createTokToken(String sessionId, String token);


}

@Service
class SessionServiceImpl implements SessionService {

    @Autowired
    OpenTok openTok;
    @Autowired
    OpenTokConfig openTokConfig;

    @Override
    String getSessionId(String mediaMode, String archiveMode) {
        if (mediaMode == null)
            mediaMode = openTokConfig.mediaMode;
        if (archiveMode == null)
            archiveMode = openTokConfig.archiveMode;

        // A session that is automatically archived (it must used the routed media mode)
        SessionProperties sessionProperties = new SessionProperties.Builder()
                .mediaMode(MediaMode.valueOf(mediaMode.toUpperCase()))
                .archiveMode(ArchiveMode.valueOf(archiveMode.toUpperCase()))
                .build();
        Session session = openTok.createSession(sessionProperties);
        // Store this sessionId in the database for later use:
        return session.getSessionId();
    }

    @Override
    String createToken(String sessionId, String username, long expireTime) {
        if (expireTime <= 0) {
            expireTime = openTokConfig.expireTime;
        }
        // Replace with meaningful metadata for the connection.
        String connectionMetadata = "username=" + username + ",userLevel=4";
        // Generate a token. Use the Role value appropriate for the user.
        TokenOptions tokenOpts = new TokenOptions.Builder()
                .role(Role.PUBLISHER)
                .expireTime((System.currentTimeMillis() / 1000) + expireTime) // in one week
                .data(connectionMetadata)
                .build();
        String token = openTok.generateToken(sessionId, tokenOpts);
        return token;
    }

    @Override
    TokToken createTokToken(String sessionId, String token) {
        return new TokToken(sessionId: sessionId, token: token, apiKey: openTokConfig.apiKey);
    }
}