package wingchi.account.component;

import lombok.AllArgsConstructor;


@AllArgsConstructor
public enum RoleEnum {
    ADMIN(2, "admin"),
    USER(1, "user");
    private int code;
    private String name;
}
