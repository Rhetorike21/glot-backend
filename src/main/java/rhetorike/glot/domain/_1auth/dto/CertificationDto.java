package rhetorike.glot.domain._1auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CertificationDto {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CodeRequest{
        String mobile;
    }

    @Getter
    @AllArgsConstructor
    public static class VerifyResponse{
        boolean success;
    }


}
