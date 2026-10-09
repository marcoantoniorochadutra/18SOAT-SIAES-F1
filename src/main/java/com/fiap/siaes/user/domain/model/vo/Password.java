package com.fiap.siaes.user.domain.model.vo;

import com.fiap.siaes.user.domain.exception.InvalidPasswordException;
import com.fiap.siaes.user.domain.exception.PasswordValueIsNullException;
import com.fiap.siaes.user.domain.security.PasswordHasher;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class Password {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 64;
    private static final Pattern COMPLEXITY_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).+$");

    @EqualsAndHashCode.Include
    private final String value;

    private Password(String value) {
        this.value = value;
    }

    private Password(String rawPassword, PasswordHasher passwordHasher) {
        this.validatePolicy(rawPassword);

        this.value = passwordHasher.hash(rawPassword);
    }

    private void validatePolicy(String rawPassword) {
        if (isBlank(rawPassword)) {
            throw new PasswordValueIsNullException();
        }

        if (!this.hasValidLength(rawPassword) || !COMPLEXITY_PATTERN.matcher(rawPassword).matches()) {
            throw new InvalidPasswordException();
        }
    }

    private boolean hasValidLength(String value) {
        return value.length() >= MIN_LENGTH && value.length() <= MAX_LENGTH;
    }

    public static Password create(String rawPassword, PasswordHasher passwordHasher) {
        return new Password(rawPassword, passwordHasher);
    }

    public static Password recreate(String hashedValue) {
        return new Password(hashedValue);
    }
}
