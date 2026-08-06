package com.teya.ledger.app.service;

import com.teya.ledger.lib.api.dto.ApplicationVersionDto;
import org.jspecify.annotations.NonNull;

public interface InfoService {

    @NonNull
    ApplicationVersionDto getServerVersion();

}
