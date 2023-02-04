#!/usr/bin/env bash
kubectl apply -f ./namespace.yaml
kubectl apply -f ./cert-manager.yaml
#kubectl apply -f ./staging-clusterissuer.yaml
kubectl apply -f ./production-clusterissuer.yaml
