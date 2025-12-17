package org.pollub.library.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettings {
    
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean reservationReady = false;
    
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean returnReminder = false;
    
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean newArrivals = false;
}
