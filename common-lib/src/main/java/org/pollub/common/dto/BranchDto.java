package org.pollub.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for library branch data shared across services.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchDto {
    private Long id;
    private String branchNumber;
    private String name;
    private String city;
    private String address;
    private Double latitude;
    private Double longitude;
    private String phone;
    private String email;
    private String openingHours;
}
