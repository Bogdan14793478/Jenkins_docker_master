# Jenkins Master с Docker поддержкой

Docker образ Jenkins Master с предустановленными плагинами и автоматической конфигурацией.

## Описание

Этот образ содержит:
- Jenkins LTS версия 2.504.3
- Предустановленные плагины (см. `plugins.txt`)
- Groovy скрипты для автоматической настройки при первом запуске
- Поддержка Docker для выполнения сборок в контейнерах

## Структура файлов

```
Jenkins_docker_master/
├── Dockerfile                    # Определение образа
├── Jenkinsfile.groovy           # CI/CD пайплайн для сборки
├── plugins.txt                  # Список плагинов с версиями
├── init.groovy.d/              # Скрипты инициализации
│   ├── 00-execute_scripts.groovy    # Главный скрипт запуска
│   ├── basic-security.groovy        # Настройка безопасности
│   ├── disable-csrf.groovy          # Отключение CSRF (для тестирования)
│   ├── core/                        # Основные модули
│   │   ├── InfraConfig.groovy
│   │   ├── JenkinsScript.groovy
│   │   └── Runner.groovy
│   ├── lib/                         # Библиотеки для создания джоб
│   │   ├── create_folder_tools.groovy
│   │   ├── create_job_read_plugins_versions.groovy
│   │   ├── create_job_update_plugins_versions.groovy
│   │   ├── do_create_job_read_plugins_versions.groovy
│   │   └── job_update_plugins.groovy
│   └── scripts/                     # Скрипты инициализации
│       ├── i1_jenkins_exiting_configuration.groovy
│       ├── i2_create_folder_and_rules.groovy
│       ├── i3_create_job_version_plugin.groovy
│       ├── i4_configure_master_node.groovy
│       ├── i5_ec_2_plugin_configuration.groovy
│       ├── i6_create_job_update_plugins.groovy
│       ├── i7_docker_cloud_configuration.groovy
│       ├── i8_create_multibranch_jobs.groovy
│       └── i9_shared_library_configuration.groovy
└── README.md                    # Этот файл
```

## Установленные плагины

Основные плагины:
- **Pipeline**: workflow-aggregator, pipeline-*
- **Docker**: docker-plugin, docker-commons
- **Cloud**: ec2 (AWS EC2), cloudbees-folder
- **Git**: git, github, github-branch-source
- **UI**: blueocean, dark-theme
- **Security**: matrix-auth, credentials
- **Configuration**: configuration-as-code, job-dsl
- **Monitoring**: prometheus

Полный список см. в `plugins.txt`

## Сборка образа

### Локальная сборка

```bash
docker build -t bogdan1chernet/jenkins-docker-master:latest .
```

### Сборка через Jenkins Pipeline

Используйте `Jenkinsfile.groovy` для автоматической сборки и публикации:

1. Создайте Pipeline Job в Jenkins
2. Укажите этот репозиторий как источник
3. Pipeline автоматически:
   - Соберет образ с тегом на основе git commit
   - Опубликует в Docker Hub

**Требования для Pipeline:**
- Credentials ID: `docker_hub_log` (Docker Hub username/password)

## Запуск контейнера

### Базовый запуск

```bash
docker run -d \
  --name jenkins-master \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  bogdan1chernet/jenkins-docker-master:latest
```

### С Docker поддержкой

Для выполнения Docker команд внутри Jenkins:

```bash
docker run -d \
  --name jenkins-master \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v /usr/bin/docker:/usr/bin/docker \
  bogdan1chernet/jenkins-docker-master:latest
```

### С дополнительными томами

```bash
docker run -d \
  --name jenkins-master \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v /path/to/your/data:/data \
  bogdan1chernet/jenkins-docker-master:latest
```

## Первый запуск

1. Откройте браузер: `http://localhost:8080`
2. Скрипты инициализации выполнятся автоматически
3. Дождитесь завершения установки плагинов (может занять несколько минут)
4. Jenkins будет настроен согласно скриптам в `init.groovy.d/`

## Настройка через Groovy скрипты

Скрипты выполняются в следующем порядке:

1. **i1_jenkins_exiting_configuration.groovy** - Проверка существующей конфигурации
2. **i2_create_folder_and_rules.groovy** - Создание папок и правил
3. **i3_create_job_version_plugin.groovy** - Создание джоб для версионирования плагинов
4. **i4_configure_master_node.groovy** - Настройка Master ноды
5. **i5_ec_2_plugin_configuration.groovy** - Настройка AWS EC2 плагина
6. **i6_create_job_update_plugins.groovy** - Создание джоб для обновления плагинов
7. **i7_docker_cloud_configuration.groovy** - Настройка Docker Cloud
8. **i8_create_multibranch_jobs.groovy** - Создание multibranch pipeline джоб
9. **i9_shared_library_configuration.groovy** - Настройка Shared Libraries

## Переменные окружения

- `JAVA_OPTS`: Опции JVM (по умолчанию: `-Djenkins.install.runSetupWizard=false`)

## Порты

- `8080`: Web UI Jenkins
- `50000`: JNLP порт для подключения агентов

## Volumes

- `/var/jenkins_home`: Данные Jenkins (конфигурация, джобы, логи)
- `/var/run/docker.sock`: Docker socket (для выполнения Docker команд)

## Подключение Slave агентов

### Через Docker Cloud Plugin

1. Перейдите в `Manage Jenkins` → `Manage Nodes and Clouds`
2. Настройте Docker Cloud согласно скрипту `i7_docker_cloud_configuration.groovy`
3. Укажите образ slave: `bogdan1chernet/jenkins-slave-with-docker:latest`

### Через SSH

1. Настройте SSH credentials
2. Добавьте новый узел через `Manage Nodes`
3. Укажите хост и credentials

## Troubleshooting

### Проблемы с Docker

Если Docker команды не работают:
- Убедитесь, что Docker socket смонтирован: `-v /var/run/docker.sock:/var/run/docker.sock`
- Проверьте права доступа к Docker socket

### Проблемы с плагинами

Если плагины не устанавливаются:
- Проверьте версии в `plugins.txt`
- Проверьте логи: `docker logs jenkins-master`

### Проблемы с инициализацией

Если скрипты не выполняются:
- Проверьте логи в `/var/jenkins_home/init.groovy.d/`
- Убедитесь, что файлы скопированы в образ

## Обновление образа

```bash
# Остановите текущий контейнер
docker stop jenkins-master

# Создайте резервную копию данных
docker run --rm -v jenkins_home:/data -v $(pwd):/backup \
  alpine tar czf /backup/jenkins_backup.tar.gz -C /data .

# Обновите образ
docker pull bogdan1chernet/jenkins-docker-master:latest

# Запустите новый контейнер
docker run -d \
  --name jenkins-master \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  bogdan1chernet/jenkins-docker-master:latest
```

## Безопасность

⚠️ **Важно**: 
- Скрипт `disable-csrf.groovy` отключает CSRF защиту (только для тестирования!)
- В продакшене используйте `basic-security.groovy` для настройки безопасности
- Настройте firewall для портов 8080 и 50000
- Используйте HTTPS в продакшене
