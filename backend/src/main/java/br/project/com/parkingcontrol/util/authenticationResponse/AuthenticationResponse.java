package br.project.com.parkingcontrol.util.authenticationResponse;

import java.util.Map;

public class AuthenticationResponse {
    private String token;
    private Map<String, Object> data;

    AuthenticationResponse(String token, Map<String, Object> data) {
        this.token = token;
        this.data = data;
    }

    public String getToken() {
        return token;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public static class Builder {
        private String token;
        private Map<String, Object> data;

        public Builder() {
            this.token = null;
            this.data = null;
        }

        public Builder setToken(String token) {
            this.token = token;
            return this;
        }

        public Builder setData(Map<String, Object> data) {
            this.data = data;
            return this;
        }

        public String getToken() {
            return token;
        }

        public Map<String, Object> getData() {
            return data;
        }

        public AuthenticationResponse build() {
            return new AuthenticationResponse(token, data);
        }
    }
}
