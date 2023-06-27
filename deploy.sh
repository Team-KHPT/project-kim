#!/bin/bash
git pull

pm2 stop project-kim || true

pm2 start --name project-kim java -Xmx300m -- -jar build/libs/project-kim-0.0.1-SNAPSHOT.jar

exit 0
