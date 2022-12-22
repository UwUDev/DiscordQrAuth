package me.uwu.dqa.exception;

public class AuthentificationTimedOutException extends RuntimeException {
    public AuthentificationTimedOutException() {
        super("Authentification timed out");
    }
}
