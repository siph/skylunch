#!/usr/bin/env bash
kubectl apply -f ./namespace.yaml
kubectl apply -f ./secrets.yaml
kubectl apply -f ./deployment.yaml
kubectl apply -f ./service.yaml
kubectl apply -f ./ingress.yaml
