package org.example.server.api.email.application;

public interface EmailSendService {

    void sendEmail(String email, String verificationNumber);
}
