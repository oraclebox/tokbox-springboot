package com.github.oraclebox.controller

import com.github.oraclebox.model.ResultModel
import com.github.oraclebox.service.SessionService
import com.opentok.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.Assert
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = "/session/v1")
class SessionController {

    @Autowired
    OpenTok openTok;
    @Autowired
    SessionService service;


    @CrossOrigin
    @RequestMapping(value = "/session", method = RequestMethod.POST)
    public ResponseEntity session(@RequestBody Map json) {
        Assert.isTrue(!(json == null), "Missing payload content.");
        String sessionId = service.getSessionId(
                json.get("mediaMode").toString(),
                json.get("archiveMode").toString());


        String username = "Default"; //TODO from JWT session key;
        long expireTime = 0; //TODO determine the expireTime;
        String token = service.createToken(sessionId, username, expireTime);

        return new ResponseEntity<>(ResultModel.ok(service.createTokToken(sessionId, token)), HttpStatus.OK);
    }

    /*
     * Create session by your APIKEY (You can consider as chat room)
     */

    @CrossOrigin
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity create() {
        // A session that is automatically archived (it must used the routed media mode)
        SessionProperties sessionProperties = new SessionProperties.Builder()
                .mediaMode(MediaMode.ROUTED)
                .archiveMode(ArchiveMode.ALWAYS)
                .build();

        Session session = openTok.createSession(sessionProperties);

        // Store this sessionId in the database for later use:
        String sessionId = session.getSessionId();

        return new ResponseEntity<>(ResultModel.ok(sessionId), HttpStatus.OK);

    }

    /*
    * Once a Session is created, you can start generating Tokens for clients to use when connecting to it.
    * Suggest use JWT token from HttpRequest header to identify the user instead of using userId.
    */

    @CrossOrigin
    @RequestMapping(value = "/token/{sessionId}/{userId}", method = RequestMethod.POST)
    public ResponseEntity token(@PathVariable String sessionId, @PathVariable String userId) {
        // Replace with meaningful metadata for the connection.
        String connectionMetadata = "username=" + userId + ",userLevel=4";

        // Generate a token. Use the Role value appropriate for the user.
        TokenOptions tokenOpts = new TokenOptions.Builder()
                .role(Role.PUBLISHER)
                .expireTime((System.currentTimeMillis() / 1000) + (7 * 24 * 60 * 60)) // in one week
                .data(connectionMetadata)
                .build();
        String token = openTok.generateToken(sessionId, tokenOpts);
        return new ResponseEntity<>(ResultModel.ok(token), HttpStatus.OK);
    }
}
