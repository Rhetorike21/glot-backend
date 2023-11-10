package rhetorike.glot.domain._2user.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.global.error.exception.UserNotFoundException;
import rhetorike.glot.setup.ServiceTest;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ServiceTest
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("회원의 정보를 조회한다.")
    void getUserProfile(){
        //given
        Long userId = 1L;
        User user = new Personal();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        //when
        userService.getUserInfo(userId);

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
        Assertions.assertThatThrownBy(() -> userService.getUserInfo(userId)).isInstanceOf(UserNotFoundException.class);
    }
}