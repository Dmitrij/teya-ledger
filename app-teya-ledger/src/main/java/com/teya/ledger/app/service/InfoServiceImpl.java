package com.teya.ledger.app.service;

import com.teya.ledger.lib.api.dto.ApplicationVersionDto;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class InfoServiceImpl implements InfoService {

    public final String appArtifact;
    public final String versionFromBuildProperties;
    public final String applicationName;
    public final Integer versionMajor;
    public final Integer versionMinor;
    public final Integer versionBuild;

    @Autowired
    public InfoServiceImpl(BuildProperties buildProperties) {
        this.applicationName = buildProperties.getName();
        this.appArtifact = buildProperties.getArtifact();
        final String vStr = buildProperties.getVersion();
        log.debug("buildProperties.getArtifact: {}", appArtifact == null ? "NULL" : appArtifact);
        log.debug("buildProperties.getVersion: {}", vStr == null ? "NULL" : vStr);

        final String[] versionArray = Objects.requireNonNullElse(vStr, "").split("\\.");
        if (versionArray.length != 3) {
            this.versionFromBuildProperties = "0.0.0";
            this.versionMajor = 0;
            this.versionMinor = 0;
            this.versionBuild = 0;
            log.warn("ApplicationVersionDto: {} - {}.{}.{}", applicationName, versionMajor, versionMinor, versionBuild);
        } else {
            this.versionFromBuildProperties = vStr;
            this.versionMajor = Integer.parseInt(versionArray[0]);
            this.versionMinor = Integer.parseInt(versionArray[1]);
            this.versionBuild = Integer.parseInt(versionArray[2]);
            log.info("ApplicationVersionDto: {} - {}.{}.{}", applicationName, versionMajor, versionMinor, versionBuild);
        }
    }

    @Override
    @NonNull
    public ApplicationVersionDto getServerVersion() {
        return new ApplicationVersionDto(applicationName, versionMajor, versionMinor, versionBuild);
    }

}
