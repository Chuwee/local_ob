#!/usr/bin/env bash

#set -x

echo "[+] Preparing aws configs"

mkdir -p ~/.aws
echo """
[profile services]
region = eu-west-1
role_arn = arn:aws:iam::444268313358:role/cicd-role
credential_source = Environment
output=json
""" > ~/.aws/config
echo "[+] Finished aws configs"


cd ${CI_PRIMARY_REPOSITORY_PATH}
echo ${PWD}
export HOMEBREW_NO_INSTALL_CLEANUP=TRUE
# Install CocoaPods
echo "📦 Install CocoaPods"
brew install cocoapods
brew install node@20
brew link node@20
export PATH="/usr/local/opt/node@20/bin:$PATH"
npm config set "//npm.pkg.github.com/:_authToken" "${GITHUB_TOKEN}"
echo "📦 Finished Installing CocoaPods"

# Install dependencies
echo "🥁 Performing npm ci command on root directory"
#npm config set maxsockets 3
npm ci
echo "🥁 Finished [npm ci command on root directory]"

echo "🧢 Performing npm ci command on native directory + build native"
cd ${CI_PRIMARY_REPOSITORY_PATH}/apps/native
npx cap build ios
echo "🧢 Finished [npm ci command on native directory + build native]"

echo "💾 Performing [pod install] command"
cd ${CI_PRIMARY_REPOSITORY_PATH}/apps/native/ios/App
pod install
echo "💾 Finished [pod install] command"

echo "✈️ Performing [npx nx run native:build] command"
cd ${CI_PRIMARY_REPOSITORY_PATH}
npx nx run native:build
echo "✈️ Finished [npx nx run native:build] command"

echo "📲 Performing [npx nx run native:sync:ios] command"
cd ${CI_PRIMARY_REPOSITORY_PATH}
npx nx run native:sync:ios
echo "📲 Finished [npx nx run native:sync:ios] command"
