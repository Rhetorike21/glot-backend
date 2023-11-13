package rhetorike.glot.domain._2user.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import rhetorike.glot.domain._2user.dto.UserProfileDto;
import rhetorike.glot.domain._2user.entity.OrganizationMember;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.global.error.exception.UserNotFoundException;
import rhetorike.glot.setup.ServiceTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ServiceTest
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원의 정보를 조회한다.")
    void getUserProfile(){
        //given
        Long userId = 1L;
        User user = new Personal();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        //when
        userService.getUserProfile(userId);

        //then
        verify(userRepository).findById(userId);
    }


    @Test
    @DisplayName("회원이 존재하지 않는 경우, 예외를 던진다.")
    void getUserProfileFailed(){
        //given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        //when

        //then
        Assertions.assertThatThrownBy(() -> userService.getUserProfile(userId)).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("[프로필 수정] ")
    void updateUserProfile(){
        //given
        Long userId = 1L;
        User user = Personal.builder().name("가나다").email("test@naver.com").build();
        UserProfileDto.UpdateRequest requestBody = new UserProfileDto.UpdateRequest("홍길동", "01012345678", null, "abcd1234");
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.encode("abcd1234")).willReturn("encodedPassword");

        //when
        userService.updateUserProfile(requestBody, userId);

        //then
        verify(userRepository).findById(userId);
        verify(passwordEncoder).encode("abcd1234");
    }

    @Test
    @DisplayName("[프로필 수정] - OrganizationMember의 경우, 이름과 비밀번호만 수정할 수 있다.")
    void updateOrganizationMemberProfile(){
        //given
        Long userId = 1L;
        User user = OrganizationMember.builder()
                .name("가나다")
                .email(null)
                .mobile(null)
                .password("password")
                .build();
        UserProfileDto.UpdateRequest requestBody = new UserProfileDto.UpdateRequest("홍길동", "01098765432", "update@naver.com", "updatePassword");
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.encode("updatePassword")).willReturn("encodedPassword");

        //when
        userService.updateUserProfile(requestBody, userId);

        //then
        verify(userRepository).findById(userId);
        verify(passwordEncoder).encode("updatePassword");
        assertThat(user.getName()).isEqualTo("홍길동");
        assertThat(user.getMobile()).isNull();
        assertThat(user.getEmail()).isNull();
        assertThat(user.getPassword()).isEqualTo("encodedPassword");
    }
}