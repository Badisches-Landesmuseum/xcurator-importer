package de.dreipc.xcurator.xcuratorimportservice.models;

import com.google.protobuf.Message;
import de.dreipc.rabbitmq.ProtoModel;
import de.dreipc.rabbitmq.ProtoUtil;
import dreipc.xcurator.proto.XCuratorProtos;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

@Builder
@Data
public class DateRange implements ProtoModel {
    @NotNull
    @Builder.Default
    String name = "";
    Instant start;
    Instant end;
    @NotNull
    @Builder.Default
    String epoch = "";

    @Override
    public Message toProto() {
        try {
            return XCuratorProtos.DateRangeProto.newBuilder()
                    .setStart(ProtoUtil.toProto(this.start))
                    .setEnd(ProtoUtil.toProto(this.end))
                    .setEpoch(epoch)
                    .setName(name)
                    .build();
        } catch (Exception e) {
            return XCuratorProtos.DateRangeProto.newBuilder()
                    .setEpoch(epoch)
                    .setName(name)
                    .build();
        }


    }
}
