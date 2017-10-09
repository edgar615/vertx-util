package com.github.edgar615.util.vertx.deployment;

public class CodecRegistrationException extends RuntimeException {

        private static final long serialVersionUID = 7375493553868519673L;

        public CodecRegistrationException(final String message) {
            super(message);
        }

        public CodecRegistrationException(final String message, final Throwable cause) {
            super(message, cause);
        }
}