package group_10.group._0.service;

import group_10.group._0.repository.InvalidatedTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupService {

    private final InvalidatedTokenRepository invalidatedTokenRepository;

    /**
     * Tác vụ này sẽ tự động chạy.
     * Cấu hình cron dưới đây là chạy vào lúc 2h sáng mỗi ngày.
     * (cron = "Giây Phút Giờ Ngày Tháng Thứ")
     */
    @Scheduled(cron = "0 0 2 * * ?")
//    @Scheduled(cron = "0/10 * * * * ?") // Chạy mỗi 10 giây để kiểm tra
    @Transactional // Bắt buộc phải có Transactional khi thực hiện query dạng DELETE/UPDATE
    public void clearExpiredTokens() {
        log.info("Bắt đầu tiến trình dọn dẹp các token đã hết hạn trong Blacklist...");
        try {
            invalidatedTokenRepository.deleteByExpirationtimeBefore(new Date());
            log.info("Dọn dẹp token hoàn tất!");
        } catch (Exception e) {
            log.error("Có lỗi xảy ra khi dọn dẹp token: ", e);
        }
    }
}