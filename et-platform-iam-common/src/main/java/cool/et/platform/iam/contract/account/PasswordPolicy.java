package cool.et.platform.iam.contract.account;

public final class PasswordPolicy {
    public static final String PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,64}$";

    private PasswordPolicy() {
    }
}
