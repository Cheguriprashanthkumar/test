package org.nishgrid.clienterp.util;

import lombok.Data;

@Data
public class ConfigData {
    public String licenseKey;
    public String fullName;
    public String companyName;
    public String emailAddress;
    public String startDate;
    public String endDate;
    public String uniqueId;
    public String systemId;
    public boolean setupCompleted;
    public boolean clientDetailsCompleted;

}
