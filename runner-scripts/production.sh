source /home/gitlab-runner/production/.env

fuser -k 10500/tcp || true

export DB_USERNAME
export DB_PASSWORD
export MAPQUEST_API_KEY
export MODERATION_API_KEY

java -jar production/libs/tab-0.0.1-SNAPSHOT.jar \
    --server.port=10500 \
    --server.servlet.contextPath=/prod \
    --spring.application.name=tab \
    --spring.profiles.active=production
