#!/usr/bin/env bash

# Build 三个 Module，并将 jar 和 aar 移动到指定的文件夹

set -eu
red=$(tput setaf 1)
green=$(tput setaf 2)

router_api_aar_path="./router-api/build/outputs/aar/router-api-release.aar"
router_compiler_jar_path="./router-compiler/build/libs/router-compiler.jar"
router_annotation_jar_path="./router-annotation/build/libs/router-annotation.jar"

router_api_aar_target_path="./output/router-api.aar"
router_compiler_jar_target_path="./output/router-compiler.jar"
router_annotation_jar_target_path="./output/router-annotation.jar"

moveOutput() {
    cp ${router_api_aar_path} ${router_api_aar_target_path}
    cp ${router_compiler_jar_path} ${router_compiler_jar_target_path}
    cp ${router_annotation_jar_path} ${router_annotation_jar_target_path}
}

gradle -q build

if [ $? -eq 0 ]
then
    if [ ! -e "output" ]
    then
        mkdir "output"
    fi
    moveOutput
    echo "${green}Build done!!${green}"
else
    echo "${red}Build failed!!${red}"
fi