package com.wbq.ragsystem.ticket;

import java.util.Arrays;

public enum TicketCategory {
    RECHARGE_REFUND("充值与退款"),
    ACCOUNT_SECURITY("账户与安全"),
    VOICE_ROOM("语音房与直播"),
    GIFT_TIP("礼物与打赏"),
    MEMBER("会员与活动"),
    REPORT("举报与违规"),
    SOUND_NETWORK("声音与网络"),
    OTHER("其他");

    private final String label;

    TicketCategory(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public static boolean isValid(String code) {
        if (code == null || code.isBlank()) {
            return false;
        }
        return Arrays.stream(values()).anyMatch(c -> c.name().equals(code));
    }

    public static String labelOf(String code) {
        if (code == null) {
            return "";
        }
        return Arrays.stream(values())
                .filter(c -> c.name().equals(code))
                .map(TicketCategory::label)
                .findFirst()
                .orElse(code);
    }
}
