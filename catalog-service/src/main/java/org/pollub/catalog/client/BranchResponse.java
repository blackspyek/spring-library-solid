package org.pollub.catalog.client;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BranchResponse {
    private Long id;
    private String name;
    private String address;
    private String city;
}
