package group_10.group._0.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersResponse {

    Integer maNguoiDung;
    String ho;
    String ten;
    String email;
    Instant ngayTao;
}