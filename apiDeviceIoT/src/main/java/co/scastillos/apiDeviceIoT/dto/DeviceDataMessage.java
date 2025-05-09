package co.scastillos.apiDeviceIoT.dto;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDataMessage {
    private String deviceId;
    private Double temperature;
    private Long timestamp;
}
