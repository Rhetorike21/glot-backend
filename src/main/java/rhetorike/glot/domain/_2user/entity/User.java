package rhetorike.glot.domain._2user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import rhetorike.glot.global.config.jpa.BaseTimeEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type")
@NoArgsConstructor
@Entity
public abstract class User extends BaseTimeEntity implements UserDetails {

    public final static String DEFAULT_ROLE = "ROLE_USER";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(length = 50)
    protected String username;

    @Column(length = 50)
    protected String password;

    @Column(length = 50)
    protected String name;

    @Column(length = 50)
    protected String phone;

    @Column(length = 50)
    protected String mobile;

    @Column(length = 50)
    protected String email;

    protected boolean marketingAgreement;

    @ElementCollection(fetch = FetchType.LAZY)
    protected List<String> roles;

    public User(Long id, String username, String password, String name, String phone, String mobile, String email, boolean marketingAgreement, List<String> roles){
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.mobile = mobile;
        this.email = email;
        this.marketingAgreement = marketingAgreement;
        this.roles = new ArrayList<>();
        this.roles.addAll(roles);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles
                .stream().map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
