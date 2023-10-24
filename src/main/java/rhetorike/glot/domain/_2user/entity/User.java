package rhetorike.glot.domain._2user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import rhetorike.glot.domain._3writing.entity.WritingBoard;
import rhetorike.glot.global.config.jpa.BaseTimeEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type")
@NoArgsConstructor
@Entity
public abstract class User extends BaseTimeEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(length = 50)
    protected String accountId;

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

    @OneToMany
    protected List<WritingBoard> writingBoards;

    public User(Long id, String accountId, String password, String name, String phone, String mobile, String email, boolean marketingAgreement, List<String> roles, List<WritingBoard> writingBoards) {
        this.id = id;
        this.accountId = accountId;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.mobile = mobile;
        this.email = email;
        this.marketingAgreement = marketingAgreement;
        this.roles = new ArrayList<>();
        this.writingBoards = new ArrayList<>();
        if (writingBoards != null){
            this.writingBoards.addAll(writingBoards);
        }
        if (roles != null){
            this.roles.addAll(roles);
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles
                .stream().map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return String.valueOf(this.id);
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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        User user = (User) object;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getType() {
        if (this instanceof Personal){
            return "개인";

        }
        if (this instanceof Organization) {
            return "기관";
        }
        return "없음";
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public int countBoard() {
        return writingBoards.size();
    }
}
