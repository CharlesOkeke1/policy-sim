package org.cokeke.policyengine.models;

public record SessionDTO(Long id, Long userId, Long gameId,
    String startTime, String endTime, String engineToken) {
        @Override
        public String toString() {
            return "SessionDTO{" +
                    "id=" + id +
                    ", userId=" + userId +
                    ", gameId=" + gameId +
                    ", startTime='" + startTime + '\'' +
                    ", endTime='" + endTime + '\'' +
                    ", engineToken='" + engineToken + '\'' +
                    '}';
        }
    }