package de.dreipc.xcurator.xcuratorimportservice.models;

import com.google.protobuf.Message;
import de.dreipc.rabbitmq.ProtoModel;
import dreipc.xcurator.proto.XCuratorProtos;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Location implements ProtoModel {

    @Builder.Default
    String name = "";
    Float longitude;
    Float latitude;

    String countryName;

    @Override
    public Message toProto() {
        if (longitude == null | longitude == null) {
            return XCuratorProtos.LocationProto.newBuilder()
                    .setName(name)
                    .build();
        }

        return XCuratorProtos.LocationProto.newBuilder()
                .setLatitude(latitude)
                .setLongitude(longitude)
                .setName(name)
                .setCountryName(countryName)
                .build();
    }
}
