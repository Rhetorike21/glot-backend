package rhetorike.glot.domain._1auth.service.smscert;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._1auth.entity.CertCode;
import rhetorike.glot.domain._1auth.repository.certcode.CertCodeRepository;
import rhetorike.glot.domain._1auth.service.smscert.smssender.SmsSender;
import rhetorike.glot.global.util.RandomTextGenerator;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SmsCertificationService {
    private final SmsSender smsSender;
    private final CertCodeRepository certCodeRepository;
    private final RandomTextGenerator randomTextGenerator;

    /**
     * 주어진 전화번호로 인증 번호를 전송합니다.
     *
     * @param mobile 전화번호
     */
    public void sendCertCode(String mobile) {
        CertCode certCode = certCodeRepository.save(new CertCode(getUniquePinNumbers(), false));
        smsSender.sendCertCode(mobile, certCode);
    }

    private String getUniquePinNumbers() {
        String pinNumbers = randomTextGenerator.generateSixNumbers();
        while (certCodeRepository.findByPinNumbers(pinNumbers).isPresent()) {
            pinNumbers = randomTextGenerator.generateSixNumbers();
        }
        return pinNumbers;
    }

    /**
     * 인증 번호를 확인합니다.
     *
     * @param pinNumbers 인증 번호
     * @return 검증 여부
     */
    public boolean verifyCode(String pinNumbers) {
        Optional<CertCode> certCodeOptional = certCodeRepository.findByPinNumbers(pinNumbers);
        certCodeOptional.ifPresent(this::checkCertCode);
        return certCodeOptional.isPresent();
    }

    private void checkCertCode(CertCode certCode) {
        certCode.setChecked();
        certCodeRepository.update(certCode);
    }

    /**
     * 핀번호 6자리가 유효한지 검증합니다.
     * 발급된 핀번호인지 + checked=true 를 확인합니다.
     *
     * @param pinNumbers 핀번호 6자리
     * @return true/false
     */
    public boolean doesValidPinNumbers(String pinNumbers) {
        Optional<CertCode> certCodeOptional = certCodeRepository.findByPinNumbers(pinNumbers);
        return certCodeOptional.isPresent() && certCodeOptional.get().isChecked();
    }
}
