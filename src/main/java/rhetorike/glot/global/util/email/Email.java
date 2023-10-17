package rhetorike.glot.global.util.email;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import rhetorike.glot.domain._1auth.entity.ResetCode;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Email {
    String destination;
    String subject;
    String content;

    public static Email newAccountIdEmail(String destination, List<String> accountIds) {
        String subject = "GLOT 아이디 찾기 안내";
        String accountIdRegion = accountIds.stream()
                .map(str -> str.substring(0, str.length() - 2) + "**")
                .collect(Collectors.joining("<br>"));
        String content = getAccountIdEmailContent(accountIdRegion);
        return new Email(destination, subject, content);
    }

    public static Email newPasswordResetEmail(String destination, ResetCode resetCode) {
        String subject = "GLOT 비밀번호 변경 안내";
        String domainUrl = "https://www.naver.com";
        String resetLink = domainUrl + "?id=" + resetCode.getAccountId() + "&code=" + resetCode.getCode();
        String content = getPasswordResetEmailContent(resetLink);
        return new Email(destination, subject, content);
    }

    @NotNull
    private static String getPasswordResetEmailContent(String resetLink) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<body>\n" +
                "    <div style=\"font-family: Arial, sans-serif; width: 100%; height: 100vh; display: flex; flex-direction: column; align-items: center; background-color: #f2f3f5;\">\n" +
                "        <div style=\"width: 100%; height: 100%; background-color: white;\">\n" +
                "            <div style=\"background-color: #3290ff; color: white; padding: 40px; display: flex; flex-direction: column; align-items: left;\">\n" +
                "                <img src=\"https://i.postimg.cc/G3GBHJvP/GLOT-logo.png\" alt=\"로고\" style=\"width: 73px; height: 32px;\">\n" +
                "                <div style=\"font-size: 32px; font-weight: 700; text-align: left; margin-top: 50px;\">비밀번호 변경 안내</div>\n" +
                "                <div style=\"font-size: 16px; font-weight: 500; text-align: left; white-space: pre-line; line-height: 1.5\">\n" +
                "                    안녕하세요, 레토리케 입니다.\n" +
                "                    고객님의 비밀번호가 초기화 되었습니다. 아래의 링크로 접속하셔서\n" +
                "                    비밀번호를 재설정해주십시오.\n" +
                "                </div>\n" +
                "            </div>\n" +
                "             <div style=\"padding: 40px; background-color: white; display: flex; flex-direction: column; align-items: center;\">\n" +
                "                <div style=\"width: 90%; background-color: #f2f3f5; padding: 30px; border-radius: 8px; text-align: left;\">\n" +
                resetLink +
                "                </div>\n" +
                "                <div style=\"width: 95%; height: 100px; text-align: left; padding-top: 20px;\">\n" +
                "                  위의 링크를 클릭해도 시작되지 않으면 URL을 복사하여 새 창에 붙여 넣어 주십시오.\n" +
                "                </div>\n" +
                "                <div style=\"border-top: 1px solid black; width: 95%; height: 100px; margin-top: 30px;\">\n" +
                "                  <p style=\"font-size: 14px; font-weight: 400\">Copyright.(c) RHETORIKE All rights reserved.</p>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }

    @NotNull
    private static String getAccountIdEmailContent(String accountIdRegion) {
        String loginLink = "https://naver.com";
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<body>\n" +
                "    <div style=\"font-family: Arial, sans-serif; width: 100%; height: 100vh; display: flex; flex-direction: column; align-items: center; background-color: #f2f3f5;\">\n" +
                "        <div style=\"width: 100%; height: 100%; background-color: white;\">\n" +
                "            <div style=\"background-color: #3290ff; color: white; padding: 40px; display: flex; flex-direction: column; align-items: left;\">\n" +
                "                <img src=\"https://i.postimg.cc/G3GBHJvP/GLOT-logo.png\" alt=\"로고\" style=\"width: 73px; height: 32px;\">\n" +
                "                <div style=\"font-size: 32px; font-weight: 700; text-align: left; margin-top: 50px;\">아이디 찾기 안내</div>\n" +
                "                <div style=\"font-size: 16px; font-weight: 500; text-align: left; margin-top: 15px;\">\n" +
                "                    안녕하세요, 레토리케 입니다. 고객님의 아이디는 아래와 같습니다.\n" +
                "                </div>\n" +
                "            </div>\n" +
                "             <div style=\"padding: 40px; background-color: white; display: flex; flex-direction: column; align-items: center;\">\n" +
                "                <div style=\"width: 90%; background-color: #f2f3f5; padding: 30px; border-radius: 8px; text-align: left;\">\n" +
                accountIdRegion +
                "                </div>\n" +
                "                <a href=\"" + loginLink + "\" style=\"display: flex; align-items: center; justify-content: center; height: 28px; width: 112px; border-radius: 9999px; margin-top: 24px; background-color: #111111; color: #fff; text-decoration: none; font-size: 14px; padding: 5px;\">로그인하러 가기</a>\n" +
                "                <div style=\"border-top: 1px solid black; width: 95%; height: 100px; margin-top: 30px;\">\n" +
                "                  <p style=\"font-size: 14px; font-weight: 400\">Copyright.(c) RHETORIKE All rights reserved.</p>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
}
