package co.com.bancolombia.model.commons;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import java.util.Objects;

/**
 * Context represents the circumstances by which the user and system is working and interacting
 */
@Getter
@Builder(toBuilder=true)
public class Context {

    @NonNull private final String id;
    @NonNull private final String sessionId;
    @NonNull private final String requestDate;
    @NonNull private final String channel;
    @NonNull private final String domain;
    @NonNull private final Agent agent;
    @NonNull private final Device device;
    @NonNull private final Customer customer;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var context = (Context) o;
        return id.equals(context.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @EqualsAndHashCode
    @Builder
    @Getter
    public static class Agent {

        @NonNull private final String name;
        @NonNull private final String appVersion;
    }

    @EqualsAndHashCode
    @Builder
    @Getter
    public static class Device {

        @NonNull private final String id;
        @NonNull private final String ip;
        @NonNull private final String type;
    }


}
