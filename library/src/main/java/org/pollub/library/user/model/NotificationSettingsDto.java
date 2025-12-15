package org.pollub.library.user.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettingsDto {
    
    @NotNull(message = "reservationReady is required")
    private Boolean reservationReady;
    
    @NotNull(message = "returnReminder is required")
    private Boolean returnReminder;
    
    @NotNull(message = "newArrivals is required")
    private Boolean newArrivals;

    public NotificationSettings toEntity() {
        return new NotificationSettings(reservationReady, returnReminder, newArrivals);
    }

    public static NotificationSettingsDto fromEntity(NotificationSettings entity) {
        if (entity == null) {
            return new NotificationSettingsDto(false, false, false);
        }
        return new NotificationSettingsDto(
                entity.isReservationReady(),
                entity.isReturnReminder(),
                entity.isNewArrivals()
        );
    }
}
