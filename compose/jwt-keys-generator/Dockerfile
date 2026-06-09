FROM alpine:3.20

RUN apk add --no-cache openssl

WORKDIR /app

COPY generate-keys.sh /app/generate-keys.sh

RUN chmod +x /app/generate-keys.sh

CMD ["/app/generate-keys.sh"]