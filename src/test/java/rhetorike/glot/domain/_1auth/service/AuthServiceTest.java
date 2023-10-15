package rhetorike.glot.domain._1auth.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import rhetorike.glot.domain._1auth.dto.SignUpRequest;
import rhetorike.glot.domain._1auth.entity.CertCode;
import rhetorike.glot.domain._1auth.repository.CertCodeRepository;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.setup.ServiceTest;

import java.util.Optional;

import static org.mockito.BDDMockito.given;

@ServiceTest
class AuthServiceTest {

    @InjectMocks
    AuthService authService;
    @Mock
    UserRepository userRepository;
    @Mock
    CertCodeRepository certCodeRepository;


    @Test
    @DisplayName("개인 사용자로 서비스에 회원가입한다.")
    void signUpWithPersonal() {
        //given
        given(certCodeRepository.findByPinNumbers("1234")).willReturn(Optional.of(new CertCode("1234", true)));
        SignUpRequest.PersonalDto requestDto = new SignUpRequest.PersonalDto("testpersonal", "abc1234", "김철수", "010-1234-5678", "010-5678-1234", "test@personal.com", true, "1234");

        //when
        authService.signUp(requestDto);

        //then
    }

    @Test
    @DisplayName("기관 사용자로 서비스에 회원가입한다.")
    void signUpWithOrganization() {
        //given
        given(certCodeRepository.findByPinNumbers("1234")).willReturn(Optional.of(new CertCode("1234", true)));
        SignUpRequest.OrganizationDto requestDto = new SignUpRequest.OrganizationDto("asdf1234", "abcd1234", "김철수", "010-1234-5678", "010-5678-1234", "test@personal.com", true, "1234", "한국고등학교");

        //when
        authService.signUp(requestDto);

        //then
    }
}