/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2008-2012 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.api.config;

import com.google.common.collect.ImmutableMap;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @since 2.15
 */
public final class Encryption {

  private static final String BASE64_ALGORITHM = "b64";
  private final Base64Cipher base64Encryption;

  private static final String AES_ALGORITHM = "aes";
  private final AesCipher aesEncryption;

  private final Map<String, Cipher> encryptions;
  private static final Pattern ENCRYPTED_PATTERN = Pattern.compile("\\{(.*?)\\}(.*)");

  Encryption(Settings settings) {
    base64Encryption = new Base64Cipher();
    aesEncryption = new AesCipher(settings);
    encryptions = ImmutableMap.of(
        BASE64_ALGORITHM, base64Encryption,
        AES_ALGORITHM, aesEncryption
    );
  }

  public boolean canEncrypt() {
    return aesEncryption.canEncrypt();
  }

  public boolean isEncrypted(String value) {
    return value.startsWith("{") && value.indexOf("}") > 1;
  }

  public String encrypt(String clearText) {
    return encrypt(AES_ALGORITHM, clearText);
  }

  public String scramble(String clearText) {
    return encrypt(BASE64_ALGORITHM, clearText);
  }

  public String generateRandomSecretKey() {
    return aesEncryption.generateRandomSecretKey();
  }

  public String decrypt(String encryptedText) {
    Matcher matcher = ENCRYPTED_PATTERN.matcher(encryptedText);
    if (matcher.matches()) {
      Cipher cipher = encryptions.get(matcher.group(1).toLowerCase(Locale.ENGLISH));
      if (cipher != null) {
        return cipher.decrypt(matcher.group(2));
      }
    }
    return encryptedText;
  }

  private String encrypt(String algorithm, String clearText) {
    Cipher cipher = encryptions.get(algorithm);
    if (cipher == null) {
      throw new IllegalArgumentException("Unknown cipher algorithm: " + algorithm);
    }
    return String.format("{%s}%s", algorithm, cipher.encrypt(clearText));
  }
}