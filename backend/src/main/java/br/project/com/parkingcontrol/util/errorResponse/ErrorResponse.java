package br.project.com.parkingcontrol.util.errorResponse;

public class ErrorResponse {
    private String messageError;
    private Integer codeError;

    ErrorResponse(String messageError, Integer codeError) {
        this.messageError = messageError;
        this.codeError = codeError;
    }

    public String getMessageError() {
        return messageError;
    }

    public Integer getCodeError() {
        return codeError;
    }

    public static class Builder {
        private String messageError;
        private Integer codeError;

        public Builder() {
            this.messageError = null;
            this.codeError = null;
        }

        public Builder setMessageError(String messageError) {
            this.messageError = messageError;
            return this;
        }

        public Builder setCodeError(Integer codeError) {
            this.codeError = codeError;
            return this;
        }

        public String getMessageError() {
            return messageError;
        }

        public Integer getCodeError() {
            return codeError;
        }

        public ErrorResponse build() {
            return new ErrorResponse(messageError, codeError);
        }
    }
}
