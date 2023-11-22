package rhetorike.glot.domain._2user.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserEntityTest {

    @Test
    @DisplayName("Personal인 경우, getType()이 '개인'을 반환한다.")
    void typeOfPersonal(){
        //given
        User user = new Personal();

        //when
        String type = user.getUserType();

        //then
        assertThat(type).isEqualTo("개인");
    }

    @Test
    @DisplayName("Organization인 경우, getType()이 '기관'을 반환한다.")
    void typeOfOrganization(){
        //given
        User user = new Organization();

        //when
        String type = user.getUserType();

        //then
        assertThat(type).isEqualTo("기관");
    }
}