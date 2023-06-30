#!/bin/bash
set -euo pipefail
IFS=$'\n\t'

eval $(minikube docker-env)

Services=('zlack')

for service in "${Services[@]}"; do
  cd $service
  docker build -t $service:latest .
  cd ..
done

K8sResources=('mongo' 'zlack')

for yaml in "${K8sResources[@]}"; do
  kubectl apply -f kubernetes/${yaml}.yaml
done
