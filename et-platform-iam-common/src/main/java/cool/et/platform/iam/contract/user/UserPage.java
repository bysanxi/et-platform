package cool.et.platform.iam.contract.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPage {
    private List<UserInfoResp> records;
    private long total;
    private long size;
    private long current;
    private long pages;
}
