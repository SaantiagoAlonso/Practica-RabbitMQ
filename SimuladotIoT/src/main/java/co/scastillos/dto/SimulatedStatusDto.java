package co.scastillos.dto;

import lombok.Builder;

@Builder
public record SimulatedStatusDto(String nameDevice, Integer battery, String status, Double deviceTemperature) {
}
