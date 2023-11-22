package rhetorike.glot.domain._1auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._1auth.entity.EmailCertCode;
import rhetorike.glot.domain._1auth.entity.MobileCertCode;
import rhetorike.glot.domain._1auth.repository.certcode.CertCodeRepository;
import rhetorike.glot.domain._1auth.service.codesender.emailsender.EmailCodeSender;
import rhetorike.glot.domain._1auth.service.codesender.smssender.MobileCodeSender;
import rhetorike.glot.global.error.exception.CertificationFailedException;
import rhetorike.glot.global.util.RandomTextGenerator;

@Service
@RequiredArgsConstructor
public class CertificationService {
    private final MobileCodeSender mobileCodeSender;
    private final EmailCodeSender emailCodeSender;
    private final CertCodeRepository certCodeRepository;
    private final RandomTextGenerator randomTextGenerator;

    /**
     * 주어진 전화번호로 인증 번호를 전송합니다.
     *
     * @param mobile 전화번호
     */
    public void sendMobileCode(String mobile) {
        String number = getUniquePinNumbers();
        MobileCertCode certCode = new MobileCertCode(number, mobile);
        certCodeRepository.save(certCode);
        mobileCodeSender.send(certCode);
    }

    /**
     * 주어진 이메일로 인증 번호를 전송합니다.
     *
     * @param email 이메일
     */
    public void sendEmailCode(String email) {
        String number = getUniquePinNumbers();
        EmailCertCode certCode = new EmailCertCode(number, email);
        certCodeRepository.save(certCode);
        emailCodeSender.send(certCode);
    }

    private String getUniquePinNumbers() {
        String pinNumbers = randomTextGenerator.generateSixNumbers();
        while (certCodeRepository.doesExists(pinNumbers)) {
            pinNumbers = randomTextGenerator.generateSixNumbers();
        }
        return pinNumbers;
    }

    /**
     * 모바일로 전달된 코드가 유효한지 검증합니다.
     *
     * @param number 인증 번호
     * @return 검증 여부
     */
    public boolean isValidNumber(String number) {
        return certCodeRepository.doesExists(number);
    }

    public void deleteCodeIfValid(String codeNumber) {
        if (certCodeRepository.doesExists(codeNumber)) {
            certCodeRepository.delete(codeNumber);
            return;
        }
        throw new CertificationFailedException();
    }
}
