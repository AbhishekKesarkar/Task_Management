package com.task_manager.util;

import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.util.Base64;

public class JwtKeyGenerator {

    public static void main(String[] args) {
    	
        // New 0.12.x API — no more deprecated SignatureAlgorithm enum
        SecretKey key = Jwts.SIG.HS256.key().build();
        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());

        System.out.println("Copy this into application.properties as jwt.secret:");
        System.out.println(base64Key);
    }
}
