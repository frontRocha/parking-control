package br.project.com.parkingcontrol.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ResponseData {
    private int statusCode;
    private boolean success;
    private Object data;
    private String message;

    public int getStatusCode() {
        return statusCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public Object getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public static ResponseData generateSuccessfulResponse(Object data) {
        return new ResponseData.Builder()
                .setSuccess(true)
                .setStatusCode(200)
                .setData(data)
                .build();
    }

    public static ResponseData generateUnsuccessfulResponse(String message) {
        return new ResponseData.Builder()
                .setSuccess(false)
                .setStatusCode(404)
                .setMessage(message)
                .build();
    }

    public static class Builder {
        private int statusCode;
        private boolean success;
        private Object data;
        private String message;

        public Builder() {
            this.statusCode = 0;
            this.success = false;
            this.data = null;
            this.message = "";
        }

        public Builder setStatusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder setSuccess(boolean success) {
            this.success = success;
            return this;
        }

        public Builder setData(Object data) {
            this.data = data;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public ResponseData build() {
            return new ResponseData(statusCode, success, data, message);
        }
    }
}
