#!/bin/bash

# Скрипт для сборки и тестирования Jenkins локально

set -e

echo "=========================================="
echo "Jenkins Local Test Script"
echo "=========================================="
echo ""

# Цвета для вывода
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Функция для вывода сообщений
info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Проверка наличия Docker
if ! command -v docker &> /dev/null; then
    error "Docker не установлен. Установите Docker и попробуйте снова."
    exit 1
fi

info "Docker найден: $(docker --version)"

# Остановка и удаление существующего контейнера (если есть)
if [ "$(docker ps -aq -f name=jenkins-local-test)" ]; then
    warn "Останавливаю существующий контейнер..."
    docker stop jenkins-local-test 2>/dev/null || true
    docker rm -f jenkins-local-test 2>/dev/null || true
fi

# Проверка занятости портов
JENKINS_PORT=8080
AGENT_PORT=50000

if lsof -Pi :${JENKINS_PORT} -sTCP:LISTEN -t >/dev/null 2>&1 || docker ps --format '{{.Ports}}' | grep -q ":${JENKINS_PORT}->"; then
    warn "Порт ${JENKINS_PORT} уже занят!"
    warn "Используется альтернативный порт 8081"
    JENKINS_PORT=8081
    AGENT_PORT=50001
fi

# Сборка образа
info "Собираю Docker образ..."
docker build -t jenkins-local-test:latest .

if [ $? -ne 0 ]; then
    error "Ошибка при сборке образа!"
    exit 1
fi

info "Образ успешно собран!"

# Запуск контейнера
info "Запускаю контейнер Jenkins на портах ${JENKINS_PORT}:8080 и ${AGENT_PORT}:50000..."
docker run -d \
    --name jenkins-local-test \
    -p ${JENKINS_PORT}:8080 \
    -p ${AGENT_PORT}:50000 \
    -e JAVA_OPTS="-Djenkins.install.runSetupWizard=false" \
    jenkins-local-test:latest

if [ $? -ne 0 ]; then
    error "Ошибка при запуске контейнера!"
    exit 1
fi

info "Контейнер запущен!"
echo ""
info "Ожидание инициализации Jenkins (это может занять 1-2 минуты)..."
echo ""

# Ожидание готовности Jenkins
MAX_WAIT=180
WAIT_COUNT=0
while [ $WAIT_COUNT -lt $MAX_WAIT ]; do
    if docker logs jenkins-local-test 2>&1 | grep -q "Jenkins is fully up and running"; then
        info "Jenkins готов!"
        break
    fi
    sleep 2
    WAIT_COUNT=$((WAIT_COUNT + 2))
    echo -n "."
done

echo ""
echo ""

# Проверка логов init.groovy.d
info "Проверяю логи инициализации..."
echo ""
echo "=========================================="
echo "Логи init.groovy.d скриптов:"
echo "=========================================="
docker logs jenkins-local-test 2>&1 | grep -A 5 -B 2 "init.groovy.d" || warn "Логи init.groovy.d не найдены"

echo ""
echo "=========================================="
echo "Логи скрипта apply-scripts-for-monitiring:"
echo "=========================================="
docker logs jenkins-local-test 2>&1 | grep -A 10 "apply-scripts-for-monitiring\|ScriptApproval\|Approved\|Failed" || warn "Логи скрипта не найдены"

echo ""
echo "=========================================="
info "Jenkins доступен по адресу: http://localhost:${JENKINS_PORT}"
info "Логин: admin / Пароль: admin123 (из basic-security.groovy)"
echo ""
info "Для просмотра всех логов выполните:"
echo "  docker logs -f jenkins-local-test"
echo ""
info "Для остановки контейнера выполните:"
echo "  docker stop jenkins-local-test"
echo ""
info "Для удаления контейнера выполните:"
echo "  docker rm -f jenkins-local-test"
echo ""
info "Для проверки одобренных скриптов в Jenkins:"
echo "  Управление Jenkins -> In-process Script Approval"
echo "=========================================="
