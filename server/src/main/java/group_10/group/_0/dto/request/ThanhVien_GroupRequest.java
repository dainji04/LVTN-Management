package group_10.group._0.dto.request;

import group_10.group._0.entity.Nhom;
import group_10.group._0.entity.Users;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThanhVien_GroupRequest {

    Integer maNguoiDung;

    Integer maNhom;

    Integer duocMoiBoi;

    Integer chapNhanBoi;
}
