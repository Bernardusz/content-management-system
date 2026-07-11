package io.github.bernardusz.cms.auth.service;

import io.github.bernardusz.cms.user.UserSecurity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {
  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.access_token.expiration}")
  private long accessTokenExpiration;

  @Value("${jwt.refresh_token.expiration}")
  private long refreshTokenExpiration;

  public boolean validateToken(String token, UserSecurity user) {
    String extractedUsername = extractSubjectId(token);

    return (
      extractedUsername.equals(String.valueOf(user.getId())) &&
        !isTokenExpired(token)
    );
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  public String generateToken(UserSecurity user) {
    return buildToken(new HashMap<>(), user);
  }

  public String buildToken(
    Map<String, Object> extraClaims,
    UserSecurity userDetails
  ){
    return Jwts.builder()
        .subject(String.valueOf(userDetails.getId()))
        .claims(extraClaims)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String extractSubjectId(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(
    String token,
    Function<Claims, T> claimsResolver
  ){
    Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public Claims extractAllClaims(String token){
    return Jwts.parser()
      .verifyWith(getSigningKey())
      .build()
      .parseSignedClaims(token)
      .getPayload();
  }

  public SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
  }

  public long getAccessTokenExpiration() {
    return accessTokenExpiration;
  }

  public long getRefreshTokenExpiration() {
    return refreshTokenExpiration;
  }

  public String generateEncryptedRefreshToken() {
    try {
      UUID uuid = UUID.randomUUID();
      String uuidString = uuid.toString();
      
      byte[] keyBytes = Decoders.BASE64.decode(secret);
      SecretKey aesKey = new SecretKeySpec(keyBytes, "AES");
      
      SecureRandom random = new SecureRandom();
      byte[] iv = new byte[12];
      random.nextBytes(iv);
      
      Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
      cipher.init(Cipher.ENCRYPT_MODE, aesKey, new javax.crypto.spec.GCMParameterSpec(128, iv));
      
      byte[] encryptedBytes = cipher.doFinal(uuidString.getBytes());
      
      byte[] combined = new byte[iv.length + encryptedBytes.length];
      System.arraycopy(iv, 0, combined, 0, iv.length);
      System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);
      
      return bytesToHex(combined);
    } catch (Exception e) {
      throw new RuntimeException("Failed to generate encrypted refresh token", e);
    }
  }

  public String generateSalt() {
    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[32];
    random.nextBytes(salt);
    return bytesToHex(salt);
  }

  public String hashToken(String token, String salt) {
    try {
      byte[] keyBytes = Decoders.BASE64.decode(secret);
      byte[] saltBytes = hexToBytes(salt);
      
      javax.crypto.spec.PBEKeySpec spec = new javax.crypto.spec.PBEKeySpec(
        token.toCharArray(),
        saltBytes,
        10000,
        256
      );
      
      javax.crypto.SecretKeyFactory factory = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
      byte[] hash = factory.generateSecret(spec).getEncoded();
      
      return bytesToHex(hash);
    } catch (Exception e) {
      throw new RuntimeException("Failed to hash token", e);
    }
  }

  public String decryptRefreshToken(String encryptedToken) {
    try {
      byte[] combined = hexToBytes(encryptedToken);
      
      byte[] keyBytes = Decoders.BASE64.decode(secret);
      SecretKey aesKey = new SecretKeySpec(keyBytes, "AES");
      
      byte[] iv = new byte[12];
      byte[] encryptedBytes = new byte[combined.length - 12];
      System.arraycopy(combined, 0, iv, 0, iv.length);
      System.arraycopy(combined, iv.length, encryptedBytes, 0, encryptedBytes.length);
      
      Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
      cipher.init(Cipher.DECRYPT_MODE, aesKey, new javax.crypto.spec.GCMParameterSpec(128, iv));
      
      byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
      return new String(decryptedBytes);
    } catch (Exception e) {
      throw new RuntimeException("Failed to decrypt refresh token", e);
    }
  }

  private String bytesToHex(byte[] bytes) {
    StringBuilder hexString = new StringBuilder();
    for (byte b : bytes) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    return hexString.toString();
  }

  private byte[] hexToBytes(String hex) {
    int len = hex.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
        + Character.digit(hex.charAt(i + 1), 16));
    }
    return data;
  }
}
