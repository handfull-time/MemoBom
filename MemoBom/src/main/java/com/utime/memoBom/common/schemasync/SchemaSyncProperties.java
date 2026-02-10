package com.utime.memoBom.common.schemasync;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.schema-sync")
public class SchemaSyncProperties {
    private boolean enabled = true;
    private String mapperLocations = "classpath*:mapper/**/*.xml";

    private boolean allowDropColumn = false;
    private boolean allowTightenNullability = false;
    private boolean allowTypeShrink = false;
    private boolean logDdl = true;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getMapperLocations() { return mapperLocations; }
    public void setMapperLocations(String mapperLocations) { this.mapperLocations = mapperLocations; }
    public boolean isAllowDropColumn() { return allowDropColumn; }
    public void setAllowDropColumn(boolean allowDropColumn) { this.allowDropColumn = allowDropColumn; }
    public boolean isAllowTightenNullability() { return allowTightenNullability; }
    public void setAllowTightenNullability(boolean allowTightenNullability) { this.allowTightenNullability = allowTightenNullability; }
    public boolean isAllowTypeShrink() { return allowTypeShrink; }
    public void setAllowTypeShrink(boolean allowTypeShrink) { this.allowTypeShrink = allowTypeShrink; }
    public boolean isLogDdl() { return logDdl; }
    public void setLogDdl(boolean logDdl) { this.logDdl = logDdl; }
}

