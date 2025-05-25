package md.bumbac.api.dto;

import lombok.Data;

@Data
public class NotificationSettingsDTO {
    private boolean notifyByEmail;
    private boolean notifyBySms;
    private boolean notifyByPush;
}
