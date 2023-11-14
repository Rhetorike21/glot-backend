package rhetorike.glot.domain._4order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rhetorike.glot.domain._2user.entity.User;

import java.time.LocalDateTime;

public class SubscriptionDto {


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberResponse{
        private String accountId;
        private String name;
        @JsonFormat(pattern = "yyyy-MM-dd-HH:mm:ss")
        private LocalDateTime lastLog;
        private boolean active;

        public MemberResponse(User user){
            this.accountId = user.getAccountId();
            this.name = user.getName();
            this.lastLog = user.getLastLoggedInAt();
            this.active = user.isActive();
        }
    }
}
