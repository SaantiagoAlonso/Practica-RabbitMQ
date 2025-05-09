package co.scastillos.apiDeviceIoT.dto;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceStatusMessage {
    private String deviceId;
    private String status;
    private Integer battery;
    private Double temperature;
    private Long timestamp;
}