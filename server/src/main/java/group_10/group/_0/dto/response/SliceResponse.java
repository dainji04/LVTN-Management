package group_10.group._0.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SliceResponse<T> {
    private List<T> content;
    private boolean hasNext;
    private int page;
    private int size;
}