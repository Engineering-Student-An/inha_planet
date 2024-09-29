package jongseol.inha_helper.exception;

public class OpenAIServiceException extends RuntimeException {
    public OpenAIServiceException(String message) {
        super(message);
    }
}
