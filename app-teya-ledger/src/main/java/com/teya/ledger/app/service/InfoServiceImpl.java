package com.teya.ledger.app.service;

import com.teya.ledger.lib.api.dto.ApplicationVersionDto;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Slf4j
//@RequiredArgsConstructor
@Service
public class InfoServiceImpl implements InfoService {

    private final Optional<BuildProperties> buildProperties;

    private ApplicationVersionDto lazyApplicationVersionDto;

    public InfoServiceImpl(Optional<BuildProperties> buildProperties) {
        this.buildProperties = buildProperties;
        ApplicationVersionDto av = getServerVersion();
        log.info("ApplicationVersionDto: {}", ApplicationVersionDto.getAppNameAndVersion(av));
    }

    @Override
    @NonNull
    public ApplicationVersionDto getServerVersion() {
        return getApplicationVersionDto();
    }

    @NonNull
    private ApplicationVersionDto getApplicationVersionDto() {
        if (lazyApplicationVersionDto == null) {
            synchronized (this) {
                BuildProperties bp = this.buildProperties.orElse(null);
                final String applicationName = bp == null ? null : bp.getName();
                final String appArtifact = bp == null ? null : bp.getArtifact();
                final String vStr = bp == null ? null : bp.getVersion();

                log.debug("buildProperties.getArtifact: {}", appArtifact == null ? "NULL" : appArtifact);
                log.debug("buildProperties.getVersion: {}", vStr == null ? "NULL" : vStr);

                final String[] versionArray = Objects.requireNonNullElse(vStr, "").split("\\.");
                final String versionFromBuildProperties;
                final int versionMajor;
                final int versionMinor;
                final int versionBuild;
                if (versionArray.length != 3) {
                    versionFromBuildProperties = "0.0.0";
                    versionMajor = 0;
                    versionMinor = 0;
                    versionBuild = 0;
                    log.warn("buildProperties: {} - {}.{}.{}", applicationName, versionMajor, versionMinor, versionBuild);
                } else {
                    versionFromBuildProperties = vStr;
                    versionMajor = Integer.parseInt(versionArray[0]);
                    versionMinor = Integer.parseInt(versionArray[1]);
                    versionBuild = Integer.parseInt(versionArray[2]);
                    log.info("buildProperties: {} - {}.{}.{}", applicationName, versionMajor, versionMinor, versionBuild);
                }
                lazyApplicationVersionDto = new ApplicationVersionDto(applicationName, versionMajor, versionMinor, versionBuild);
            }
        }
        return lazyApplicationVersionDto;
    }

}
