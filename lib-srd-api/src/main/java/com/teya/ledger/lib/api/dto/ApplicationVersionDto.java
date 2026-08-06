package com.teya.ledger.lib.api.dto;

import com.google.gson.annotations.SerializedName;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Locale;

public final class ApplicationVersionDto {

    @NotNull
    @NotEmpty
    @SerializedName("applicationName")
    public String applicationName;

    @NotNull
    @SerializedName("versionMajor")
    public Integer versionMajor;

    @NotNull
    @SerializedName("versionMinor")
    public Integer versionMinor;

    @NotNull
    @SerializedName("versionBuild")
    public Integer versionBuild;

    public ApplicationVersionDto() {
    }

    public ApplicationVersionDto(String applicationName, Integer versionMajor, Integer versionMinor, Integer versionBuild) {
        this.applicationName = applicationName;
        this.versionMajor = versionMajor;
        this.versionMinor = versionMinor;
        this.versionBuild = versionBuild;
    }

    public static String getVersionString(ApplicationVersionDto v) {
        return String.format(Locale.US, "%d.%d.%d", v.versionMajor, v.versionMinor, v.versionBuild);
    }

    public static String getAppNameAndVersion(ApplicationVersionDto v) {
        return String.format("%s %s", v.applicationName, getVersionString(v));
    }

}
