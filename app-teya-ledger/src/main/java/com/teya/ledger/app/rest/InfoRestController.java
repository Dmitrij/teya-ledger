package com.teya.ledger.app.rest;

import com.teya.ledger.app.service.InfoService;
import com.teya.ledger.lib.api.dto.ApplicationVersionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.ServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.teya.ledger.lib.api.TeyaLedgerApi.API_PREFIX;
import static com.teya.ledger.lib.api.TeyaLedgerApi.MEDIA_TYPE_APPLICATION_JSON;
import static com.teya.ledger.lib.api.TeyaLedgerApi.VERSION_PATH;

@Slf4j
@RequiredArgsConstructor
@RestController
// "/api/v1"
@RequestMapping(value = {API_PREFIX}, produces = MEDIA_TYPE_APPLICATION_JSON)
public final class InfoRestController {

    private final InfoService infoService;

    @Operation(summary = "Get Teya Ledger server version",
            description = "Retrieves Teya Ledger version, no authentification needed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK - always replies with Teya Ledger version")
    })
    @GetMapping(value = VERSION_PATH)
    public ApplicationVersionDto getServerVersion(@NonNull ServletRequest servletRequest) {
        log.info("getServerVersion from {}", servletRequest.getRemoteAddr());
        ApplicationVersionDto retVal = infoService.getServerVersion();
        log.info("getServerVersion: {}", ApplicationVersionDto.getAppNameAndVersion(retVal));
        return infoService.getServerVersion();
    }

}
