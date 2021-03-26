FROM ubuntu:latest
RUN apt update && apt install -yq curl && apt clean
WORKDIR /app
COPY test.sh /app/test.sh
CMD ["sh", "test.sh"]
