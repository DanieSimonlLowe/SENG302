source staging/.env
fuser -k 9500/tcp || true

export DB_USERNAME
export DB_PASSWORD
export MAPQUEST_API_KEY
export MODERATION_API_KEY

java -jar staging/libs/tab-0.0.1-SNAPSHOT.jar \
    --server.port=9500 \
    --server.servlet.contextPath=/test \
    --spring.application.name=tab \
    --spring.profiles.active=staging
