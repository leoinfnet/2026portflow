#!/bin/sh

set -e

OUTPUT_DIR="/keys"

PRIVATE_KEY="$OUTPUT_DIR/private_key.pem"
PRIVATE_KEY_PKCS8="$OUTPUT_DIR/private_key_pkcs8.pem"
PUBLIC_KEY="$OUTPUT_DIR/public_key.pem"

mkdir -p "$OUTPUT_DIR"

echo "Gerando chave privada RSA..."

openssl genrsa -out "$PRIVATE_KEY" 2048

echo "Convertendo chave privada para PKCS8..."

openssl pkcs8 \
  -topk8 \
  -inform PEM \
  -outform PEM \
  -in "$PRIVATE_KEY" \
  -out "$PRIVATE_KEY_PKCS8" \
  -nocrypt

echo "Gerando chave pública..."

openssl rsa \
  -in "$PRIVATE_KEY" \
  -pubout \
  -out "$PUBLIC_KEY"

echo ""
echo "Chaves geradas com sucesso:"
echo "$PRIVATE_KEY"
echo "$PRIVATE_KEY_PKCS8"
echo "$PUBLIC_KEY"
echo ""
echo "Use private_key_pkcs8.pem no Java para assinar."
echo "Use public_key.pem nos serviços para validar."