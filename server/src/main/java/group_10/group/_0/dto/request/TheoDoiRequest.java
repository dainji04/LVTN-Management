package group_10.group._0.dto.request;

import group_10.group._0.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TheoDoiRequest {

    private Integer maNguoiTheoDoi;

    private Integer maNguoiDuocTheoDoi;
}
