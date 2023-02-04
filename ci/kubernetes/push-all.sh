#!/usr/bin/env bash

pushd cert-manager
./push-tls.sh
popd

pushd skylunch
./push-skylunch.sh
popd
