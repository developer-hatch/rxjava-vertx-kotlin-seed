#!/bin/bash
set -euo pipefail
IFS=$'\n\t'

# shellcheck disable=SC2046
eval $(minikube docker-env)

docker build -t zlack:latest .

K8sResources=('mongo' 'zlack')

for yaml in "${K8sResources[@]}"; do
  kubectl apply -f kubernetes/${yaml}.yaml
done
