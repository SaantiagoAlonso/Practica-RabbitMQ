package co.scastillos.apiDeviceIoT.dto;

import lombok.Builder;

import java.util.Date;

@Builder
public record SimulatedDataDto(String nameDevice ,Double temperature, Date dateOfRead) {
}
