package rhetorike.glot.domain._1auth.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import rhetorike.glot.domain._1auth.dto.AccountIdFindDto;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.global.util.email.EmailService;
import rhetorike.glot.setup.ServiceTest;

import java.util.List;

import static org.mockito.BDDMockito.given;

@ServiceTest
class AccountIdFindServiceTest {

    @InjectMocks
    AccountIdFindService accountIdFindService;
    @Mock
    EmailService emailService;
    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("이메일로 아이디를 찾는다.")
    void findByEmail(){
        //given
        String name = "홍길동";
        String email = "hong@naver.com";
        String accountId = "hong1234";
        AccountIdFindDto.EmailRequest requestDto = new AccountIdFindDto.EmailRequest(name, email);
        given(userRepository.findByEmail(email)).willReturn(List.of(Personal.builder().accountId(accountId).build()));

        //when
        accountIdFindService.findByEmail(requestDto);

        //then
    }
}