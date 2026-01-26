

# # Остановка и удаление существующего контейнера (если есть)
# if [ "$(docker ps -aq -f name=jenkins-local-test)" ]; then
#     warn "Останавливаю существующий контейнер..."
#     docker stop jenkins-local-test 2>/dev/null || true
#     docker rm -f jenkins-local-test 2>/dev/null || true
# fi

# # Проверка занятости портов
# JENKINS_PORT=8080
# AGENT_PORT=50000

# if lsof -Pi :${JENKINS_PORT} -sTCP:LISTEN -t >/dev/null 2>&1 || docker ps --format '{{.Ports}}' | grep -q ":${JENKINS_PORT}->"; then
#     warn "Порт ${JENKINS_PORT} уже занят!"
#     warn "Используется альтернативный порт 8081"
#     JENKINS_PORT=8081
#     AGENT_PORT=50001
# fi

# # Сборка образа
# info "Собираю Docker образ..."
# docker build -t jenkins-local-test:latest .

# if [ $? -ne 0 ]; then
#     error "Ошибка при сборке образа!"
#     exit 1
# fi

# info "Образ успешно собран!"

# # Запуск контейнера
# info "Запускаю контейнер Jenkins на портах ${JENKINS_PORT}:8080 и ${AGENT_PORT}:50000..."
# docker run -d \
#     --name jenkins-local-test \
#     -p ${JENKINS_PORT}:8080 \
#     -p ${AGENT_PORT}:50000 \
#     -e JAVA_OPTS="-Djenkins.install.runSetupWizard=false" \
#     jenkins-local-test:latest

# if [ $? -ne 0 ]; then
#     error "Ошибка при запуске контейнера!"
#     exit 1
# fi

# echo "Логи скрипта apply-scripts-for-monitiring:"
# echo "=========================================="
# docker logs jenkins-local-test 2>&1 | grep -A 10 "apply-scripts-for-monitiring\|ScriptApproval\|Approved\|Failed" || warn "Логи скрипта не найдены"

# echo ""
# echo "=========================================="
# info "Jenkins доступен по адресу: http://localhost:${JENKINS_PORT}"
# info "Логин: admin / Пароль: admin123 (из basic-security.groovy)"
# echo ""
# info "Для просмотра всех логов выполните:"
# echo "  docker logs -f jenkins-local-test"
# echo ""
# info "Для остановки контейнера выполните:"
# echo "  docker stop jenkins-local-test"
# echo ""
# info "Для удаления контейнера выполните:"
# echo "  docker rm -f jenkins-local-test"
# echo ""
# info "Для проверки одобренных скриптов в Jenkins:"
# echo "  Управление Jenkins -> In-process Script Approval"
# echo "=========================================="
