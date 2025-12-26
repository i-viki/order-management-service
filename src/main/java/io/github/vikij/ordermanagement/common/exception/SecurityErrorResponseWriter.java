package io.github.vikij.ordermanagement.common.exception;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public final class SecurityErrorResponseWriter {

    private SecurityErrorResponseWriter() {

    }

    public static void writeUnauthorized(HttpServletResponse response, String message)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        response.getWriter().write("""
        {
          "status": 401,
          "error": "UNAUTHORIZED",
          "message": "%s"
        }
        """.formatted(message));
    }
}
