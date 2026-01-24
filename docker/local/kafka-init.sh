#!/bin/bash
set -e

echo "[kafka-init] waiting kafka..."
sleep 5

BOOTSTRAP="kafka:9092"

# 토픽 목록 (필요하면 여기 추가)
TOPICS=(
  "manitto.chat.v1:3:1"
)

for t in "${TOPICS[@]}"; do
  IFS=":" read -r name partitions repl <<< "$t"
  echo "[kafka-init] creating topic $name (p=$partitions r=$repl)"
  /opt/bitnami/kafka/bin/kafka-topics.sh \
    --bootstrap-server "$BOOTSTRAP" \
    --create \
    --if-not-exists \
    --topic "$name" \
    --partitions "$partitions" \
    --replication-factor "$repl"
done

echo "[kafka-init] done"
