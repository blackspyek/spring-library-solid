package org.pollub.library.branch.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LibraryBranchDto {
    private Long id;
    private String branchNumber;
    private String name;
    private String city;
    private String address;
    private Double latitude;
    private Double longitude;
    private String phone;
    private String email;
}
