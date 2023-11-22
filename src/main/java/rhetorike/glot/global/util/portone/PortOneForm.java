package rhetorike.glot.global.util.portone;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
public abstract class PortOneForm {
    private int code;
    private String message;
    private PortOneResponse response;


    @ToString
    @Getter
    public static class Token extends PortOneForm {
        private PortOneResponse.Token response;
    }

    @ToString
    @Getter
    public static class OneTimePay extends PortOneForm {
        private PortOneResponse.OneTimePay response;
    }

    @ToString
    @Getter
    public static class History extends PortOneForm {
        private PortOneResponse.PayHistory response;
    }

    @ToString
    @Getter
    public static class Again extends PortOneForm {
        private PortOneResponse.AgainPay response;
    }

    @ToString
    @Getter
    public static class Cancel extends PortOneForm {
        private PortOneResponse.Cancel response;
    }
    @ToString
    @Getter
    public static class DeleteBillingKey extends PortOneForm {
        private PortOneResponse.DeleteBillingKey response;
    }

    @ToString
    @Getter
    public static class IssueBillingKey extends PortOneForm {
        private PortOneResponse.IssueBillingKey response;
    }

    @ToString
    @Getter
    public static class PayMethod extends PortOneForm{
        PortOneResponse.PayMethod response;
    }
}
