package net.letsgta.server.api.email.dto.request;

import lombok.Builder;

@Builder
public record EmailVerificationRequest(
    String email,
    String verificationToken,
    String verificationNumber,
    int attemptCount,
    boolean isDone
) {
    /**
     * 실패 시도 횟수를 증가시킨 새로운 EmailVerificationRequest 생성
     */
    public EmailVerificationRequest incrementAttemptCount() {
        return new EmailVerificationRequest(
                this.email,
                this.verificationToken,
                this.verificationNumber,
                this.attemptCount + 1,
                this.isDone
        );
    }

    /**
     * 인증 완료 상태로 변경된 새로운 EmailVerificationRequest 생성
     */
    public EmailVerificationRequest markAsDone() {
        return new EmailVerificationRequest(
                this.email,
                this.verificationToken,
                this.verificationNumber,
                this.attemptCount,
                true
        );
    }

    /**
     * 인증번호 검증
     */
    public boolean isVerificationNumberValid(String inputVerificationNumber) {
        return this.verificationNumber.equals(inputVerificationNumber);
    }

    /**
     * 최대 시도 횟수 초과 여부 확인
     */
    public boolean isAttemptLimitExceeded(int maxAttempts) {
        return this.attemptCount >= maxAttempts;
    }
}
