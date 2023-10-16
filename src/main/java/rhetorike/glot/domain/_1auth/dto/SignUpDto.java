package rhetorike.glot.domain._1auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rhetorike.glot.domain._2user.entity.Organization;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.global.constant.Role;

import java.util.Collections;


public class SignUpDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static abstract class BasicDto {
        @Pattern(regexp = "^[a-z0-9]{5,20}$")
        protected String accountId;
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).{8,}$")
        protected String password;
        protected String name;
        protected String phone;
        protected String mobile;
        @Email
        protected String email;
        protected boolean marketingAgreement;
        protected String code;

        public abstract User toUser(String encodedPassword);
    }

    @NoArgsConstructor
    @Getter
    public static class PersonalRequest extends BasicDto {
        public PersonalRequest(String accountId, String password, String name, String phone, String mobile, @Email String email, boolean marketingAgreement, String code) {
            super(accountId, password, name, phone, mobile, email, marketingAgreement, code);
        }

        @Override
        public User toUser(String encodedPassword) {
            return Personal.builder()
                    .username(this.accountId)
                    .password(encodedPassword)
                    .name(this.name)
                    .phone(this.phone)
                    .mobile(this.mobile)
                    .email(this.email)
                    .marketingAgreement(this.marketingAgreement)
                    .roles(Collections.singletonList(Role.USER.value()))
                    .build();
        }
    }

    @NoArgsConstructor
    @Getter
    public static class OrgRequest extends BasicDto {
        private String organizationName;

        public OrgRequest(String accountId, String password, String name, String phone, String mobile, @Email String email, boolean marketingAgreement, String code, String organizationName) {
            super(accountId, password, name, phone, mobile, email, marketingAgreement, code);
            this.organizationName = organizationName;
        }

        @Override
        public User toUser(String encodedPassword) {
            return Organization.builder()
                    .organizationName(this.organizationName)
                    .username(this.accountId)
                    .password(encodedPassword)
                    .name(this.name)
                    .phone(this.phone)
                    .mobile(this.mobile)
                    .email(this.email)
                    .marketingAgreement(this.marketingAgreement)
                    .roles(Collections.singletonList(Role.USER.value()))
                    .build();
        }
    }
}
