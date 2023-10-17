package rhetorike.glot.global.util.email;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
        String content = "<h1 style='background-color: lightpink'>RHETORIKE</h1>" +
                "<h3 style='background-color: lightblue'>아이디 찾기 안내</h3>" +
                "<div style='background-color: lightgrey; border-radius: 10px; padding: 10px;'>" +
                accountIdRegion +
                "</div>";
        return new Email(destination, subject, content);
    }
}
