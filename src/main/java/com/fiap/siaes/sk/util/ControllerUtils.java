package com.fiap.siaes.sk.util;

import com.fiap.siaes.sk.domain.UUIDWrapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.net.URI;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ControllerUtils {

    public static <T> ResponseEntity<T> createdResponse(String endpoint, UUIDWrapper value) {
        return ResponseEntity.created(URI.create(buildUrl(endpoint, value.id().toString()))).build();
    }

    public static String buildUrl(String... paths) {
        if (paths == null || paths.length == 0) {
            return "";
        }

        StringBuilder url = new StringBuilder();
        for (int i = 0; i < paths.length; i++) {
            String path = paths[i];
            if (path == null || path.isEmpty()) {
                continue;
            }

            if (i > 0 && path.startsWith("/")) {
                path = path.substring(1);
            }

            if (i < paths.length - 1 && path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }

            if (!url.isEmpty() && !path.isEmpty()) {
                url.append("/");
            }
            url.append(path);
        }

        return url.toString();
    }
}
