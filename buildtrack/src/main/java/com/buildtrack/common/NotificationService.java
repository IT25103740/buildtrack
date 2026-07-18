package com.buildtrack.common;

import com.buildtrack.user.entity.User;
import org.springframework.stereotype.Service;
import org.slf4j.*;

/**
 * Observer-pattern SEED: for now just logs.
 * Sprint 5 upgrades this to real in-app + email notifications with multiple subscribers.
 */
@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public void notify(User user, String message, String link) {
        if (user == null) return;
        log.info("[NOTIFY] to={} ({}): {} → {}",
            user.getEmail(), user.getRole().getName(), message, link);
    }
}
